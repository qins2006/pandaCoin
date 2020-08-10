package cn.panda.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import cn.panda.dao.CoinDao;
import cn.panda.dao.DataDao;
import cn.panda.entity.Account;
import cn.panda.entity.Coin;
import cn.panda.entity.TradeEntity;
import cn.panda.service.BlockService;
import cn.panda.vo.base.BlockInfoReqVo;
import cn.panda.vo.base.TradeJson;

@Service
@Transactional
public class BlockServiceImpl implements BlockService {
	private static final Logger logger = Logger.getLogger(BlockServiceImpl.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${panda.main.coin}")
	private String pandaMainCoin;

	@Value("${panda.subcoin.cost}")
	private BigDecimal pandaSubcoinCost;
	@Value("${hitebaas.mynode.url}")
	private String hitebaasMynodeUrl;
	
	@Autowired
	private CoinDao coinDao;
	@Autowired
	private DataDao dataDao;
	
	@Override
	public List<TradeEntity> queryTradeEntity(String blockIndex) {
		BlockInfoReqVo bn = new BlockInfoReqVo();
		bn.setBn(blockIndex);
		ParameterizedTypeReference<List<TradeJson>> ptr = new ParameterizedTypeReference<List<TradeJson>>() {};
		HttpHeaders headers = new HttpHeaders();
        MimeType mimeType = MimeTypeUtils.parseMimeType("application/json");
        MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype(), Charset.forName("UTF-8"));
        headers.setContentType(mediaType);
        HttpEntity<BlockInfoReqVo> httpEntity = new HttpEntity<BlockInfoReqVo>(bn, headers);
        ResponseEntity<List<TradeJson>> response = restTemplate.exchange(hitebaasMynodeUrl, HttpMethod.POST, httpEntity, ptr);
        if(response.getStatusCode() == HttpStatus.OK) {
        	List<TradeJson> tjs =  response.getBody();
        	List<TradeEntity> results = new ArrayList<>();
        	for(TradeJson tj : tjs) {
        		TradeEntity te = new Gson().fromJson(tj.getContent(), TradeEntity.class);
        		results.add(te);
        	}
        	return results;
        }
		return null;
	}

	@Override
	public void createCoin(TradeEntity te, BigInteger blockIndex) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//验证报文合法性，不合法报文则丢弃
		if(pandaMainCoin.equals(te.getCoinType())){
			Coin coin = coinDao.queryCoin(te.getCoinType());
			coin.setBlockIndex(blockIndex.toString());
			coinDao.update(coin);
			Account account = new Account();
			account.setAddress(te.getFromAddress());
			account.setAmount(new BigDecimal(te.getAmount()));
			account.setCreateTime(df.format(new Date()));
			dataDao.save(account, getCollectionName(te.getCoinType()));
		}else {
			if(pandaMainCoin.equals(te.getGasCoinType()) && pandaSubcoinCost.compareTo(new BigDecimal(te.getGas())) == 0) {
				DecimalFormat df0 = new DecimalFormat("0.000000");
				Coin coin = coinDao.queryCoin(te.getCoinType());
				coin.setBlockIndex(blockIndex.toString());
				coinDao.update(coin);
				Account account = new Account();
				account.setAddress(te.getFromAddress());
				account.setAmount(new BigDecimal(te.getAmount()));
				account.setCreateTime(df.format(new Date()));
				dataDao.save(account, getCollectionName(te.getCoinType()));
				
				Account account0 = dataDao.queryAccount(te.getFromAddress(), getCollectionName(pandaMainCoin));
				account0.setAmount(account0.getAmount().subtract(new BigDecimal(te.getGas())));
				dataDao.updateAccount(account0.getAddress(), df0.format(account0.getAmount()), getCollectionName(pandaMainCoin));
			}
		}
		
	}

	@Override
	public void dealTrade(TradeEntity te) {
		//验证报文合法性，不合法报文则丢弃
		String fromAddress = te.getFromAddress();
		String toAddress = te.getToAddress();
		String coinType = te.getCoinType();
		String gasCoinType = te.getGasCoinType();
		String amount = te.getAmount();
		String gas = te.getGas();
		
		DecimalFormat df = new DecimalFormat("0.000000");
		if(pandaMainCoin.equals(gasCoinType)) {
			if(pandaMainCoin.equals(coinType)) {
				//如果是主币
				Account fromAmount = dataDao.queryAccount(fromAddress, getCollectionName(coinType));
				Account toAmount = dataDao.queryAccount(toAddress, getCollectionName(coinType));
				BigDecimal cost = new BigDecimal(amount).add(new BigDecimal(gas));
				//检查在支出方地址余额够的情况下
				if(fromAmount != null && fromAmount.getAmount().compareTo(cost) > 0){
					fromAmount.setAmount(fromAmount.getAmount().subtract(cost));
					//更新支出方余额
					dataDao.updateAccount(fromAddress, df.format(fromAmount.getAmount()), getCollectionName(coinType));
					
					if(toAmount == null) {
						toAmount = new Account();
						toAmount.setAddress(toAddress);
						toAmount.setAmount(new BigDecimal(amount));
						toAmount.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						dataDao.save(toAmount, getCollectionName(coinType));
					}else {
						toAmount.setAmount(toAmount.getAmount().add(new BigDecimal(amount)));
						dataDao.updateAccount(toAddress, df.format(toAmount.getAmount()), getCollectionName(coinType));
					}
				}
			}else {
				Account fromAmount = dataDao.queryAccount(fromAddress, getCollectionName(coinType));
				Account toAmount = dataDao.queryAccount(toAddress, getCollectionName(coinType));
				Account fromGasAmount = dataDao.queryAccount(fromAddress, getCollectionName(pandaMainCoin));
				BigDecimal costAmount = new BigDecimal(amount);
				BigDecimal costGas = new BigDecimal(gas);
				if(fromAmount != null && fromGasAmount != null 
						&& fromAmount.getAmount().compareTo(costAmount) > 0 
						&& fromGasAmount.getAmount().compareTo(costGas) > 0){
					fromAmount.setAmount(fromAmount.getAmount().subtract(costAmount));
					fromGasAmount.setAmount(fromGasAmount.getAmount().subtract(costGas));
					//更新支出方余额
					dataDao.updateAccount(fromAddress, df.format(fromAmount.getAmount()), getCollectionName(coinType));
					dataDao.updateAccount(fromAddress, df.format(fromGasAmount.getAmount()), getCollectionName(pandaMainCoin));
					
					if(toAmount == null) {
						toAmount = new Account();
						toAmount.setAddress(toAddress);
						toAmount.setAmount(new BigDecimal(amount));
						toAmount.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						dataDao.save(toAmount, getCollectionName(coinType));
					}else {
						toAmount.setAmount(toAmount.getAmount().add(new BigDecimal(amount)));
						dataDao.updateAccount(toAddress, df.format(toAmount.getAmount()), getCollectionName(coinType));
					}
				}
			}
		}
	}
	private String getCollectionName(String coinType) {
		return "t_account_" + coinType;
	}
}
