package cn.panda.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hitebaas.api.DataHelper;

import cn.panda.controller.base.BaseController;
import cn.panda.entity.Account;
import cn.panda.entity.Coin;
import cn.panda.entity.TradeEntity;
import cn.panda.service.DataService;
import cn.panda.vo.base.TradeRespVo;

@RestController
public class PandaController extends BaseController{
	private static final Logger logger = Logger.getLogger(PandaController.class);
	@Value("${hitebaas.appid}")
	private String appId;
	@Value("${hitebaas.appkey}")
	private String appKey;
	@Value("${hitebaas.secretkey}")
	private String secretKey;
	@Autowired
	private DataService dataService;
	
	@Value("${panda.main.coin}")
	private String pandaMainCoin;
	@Value("${panda.main.address}")
	private String pandaMainAddress;
	@Value("${panda.main.amount}")
	private String pandaMainAmount;
	/**
	 * 最小gas花费
	 */
	@Value("${panda.gas.min}")
	private BigDecimal minGas;
	@Value("${panda.subcoin.cost}")
	private BigDecimal pandaSubcoinCost;
	/**
	 * 接收交易的接口
	 * @return
	 */
	@PostMapping(value="/panda/trade", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<TradeRespVo> trade(@RequestBody TradeEntity tradeEntity){
		TradeRespVo trpVo = new TradeRespVo();
		try {
			if(StringUtils.isBlank(tradeEntity.getFromAddress())
					|| StringUtils.isBlank(tradeEntity.getToAddress())
					|| StringUtils.isBlank(tradeEntity.getTradehash())
					|| StringUtils.isBlank(tradeEntity.getGasCoinType())
					|| StringUtils.isBlank(tradeEntity.getGas())
					|| StringUtils.isBlank(tradeEntity.getCoinType())
					|| StringUtils.isBlank(tradeEntity.getAmount())
					|| StringUtils.isBlank(tradeEntity.getType())) {
				throw new Exception("交易数据不能为空");
			}
			if(tradeEntity.getFromAddress().length() != 34 
					|| tradeEntity.getToAddress().length() != 34 
					|| tradeEntity.getTradehash().length() != 34) {
				throw new Exception("交易数据格式不能为空");
			}
			String coinType = tradeEntity.getCoinType();//交易币种
			String gasCoinType = tradeEntity.getGasCoinType();//交易币种
			BigDecimal amount = null;
			BigDecimal gas = null;
			try {
				amount = new BigDecimal(tradeEntity.getAmount());
				gas = new BigDecimal(tradeEntity.getGas());	
			}catch (Exception e) {
				throw e;
			}
			//交易哈希验证
			String tradeHash = tradeEntity.md5Tradehash();
			if(!tradeHash.equals(tradeEntity.getTradehash())) {
				throw new Exception("交易哈希不正确");
			}
			if(!pandaMainCoin.equals(gasCoinType)) {
				throw new Exception("gas消耗的主币不正确");
			}
			//验证地址
			tradeEntity.verifyAddress();
			//验证签名
			tradeEntity.verifySign();
			//判断是否是创建代币交易还是转账交易
			if("1".equals(tradeEntity.getType())) {
				//(当 fromAddress=toAddress时，coinType 不存在，gasCoinType='panda'时，代表是创建代币
				if(!tradeEntity.getFromAddress().equals(tradeEntity.getToAddress())) {
					throw new Exception("创建币种时转出转入地址不一致");
				}
				//检查币种是否已经存在
				Coin c = dataService.queryCoin(coinType);
				if(c != null) {
					throw new Exception("币种已经存在");
				}
				//创建币种约定消耗 1000个panda币 作为创币费用。
				Coin pandaC = dataService.queryCoin(gasCoinType);
				if(pandaC == null) {
					//主币不存在，则创建主币
					if(pandaMainAddress.equals(tradeEntity.getFromAddress())) {
						//首次创建主币
						if(!pandaMainCoin.equals(coinType)) {
							throw new Exception("主币名称不对");
						}
						//sendTrade(tradeEntity);
						pandaC = new Coin();
						pandaC.setBlockIndex("-1");
						pandaC.setCoinName(pandaMainCoin);
						pandaC.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:dd").format(new Date()));
						dataService.save(pandaC);
						trpVo.setCode(0);
						trpVo.setMsg("Succcess");
						return new ResponseEntity<TradeRespVo>(trpVo, HttpStatus.OK);
					}else {
						throw new Exception("非法地址不能创建主币");
					}
				}else {
					//主币存在则创建代币
					//先把币种名称放进数据库
					if(pandaSubcoinCost.compareTo(gas) !=0) {
						throw new Exception("创建子币种gas花费必须是： " + new DecimalFormat("0.00").format(pandaSubcoinCost) + " panda币");
					}
					//验证金额是否足够
					Account pandaCoin = dataService.queryAccount(tradeEntity.getFromAddress(), getCollectionName(pandaMainCoin));
					if(pandaCoin == null || pandaCoin.getAmount().compareTo(pandaSubcoinCost) < 0) {
						throw new Exception("panda币不足，无法创建新币种");
					}
					Coin subCoin = new Coin();
					subCoin.setCoinName(coinType);
					subCoin.setBlockIndex("-1");
					subCoin.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:dd").format(new Date()));
					dataService.save(subCoin);
					sendTrade(tradeEntity);
					trpVo.setCode(0);
					trpVo.setMsg("Succcess");
					return new ResponseEntity<TradeRespVo>(trpVo, HttpStatus.OK);
				}
			}else if("0".equals(tradeEntity.getType())) {
				//如果是交易。
				if(minGas.compareTo(gas) < 0) {
					throw new Exception("gas过低不合法");
				}
				//金额验证
				Account accountCoinType = dataService.queryAccount(tradeEntity.getFromAddress(), getCollectionName(coinType));
				Account accountGasCoinType = dataService.queryAccount(tradeEntity.getFromAddress(), getCollectionName(gasCoinType));
				if(pandaMainCoin.equals(coinType)) {
					//如果是主币转账
					BigDecimal cost = amount.add(gas);
					if(accountCoinType == null || accountCoinType.getAmount().compareTo(cost) < 0) {
						throw new Exception("余额不足");
					}
				}else {
					if(accountCoinType == null || accountCoinType.getAmount().compareTo(amount) < 0) {
						throw new Exception("余额不足");
					}
					if(accountGasCoinType == null || accountGasCoinType.getAmount().compareTo(gas) < 0) {
						throw new Exception("gas余额不足，请保证至少有" + new DecimalFormat("0.00").format(minGas) + "的panda币");
					}
				}
				sendTrade(tradeEntity);
				trpVo.setCode(0);
				trpVo.setMsg("Succcess");
				return new ResponseEntity<TradeRespVo>(trpVo, HttpStatus.OK);
			}else {
				throw new Exception("交易类型不正确");
			}
		}catch (Exception e) {
			trpVo.setCode(-1);
			trpVo.setMsg(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		return new ResponseEntity<TradeRespVo>(trpVo, HttpStatus.OK);
	}
	
	private void sendTrade(TradeEntity tradeEntity) throws Exception {
		try {
			DataHelper<TradeEntity> dataHelper = new DataHelper<TradeEntity>();
			boolean b = dataHelper.send(tradeEntity, appId, appKey, secretKey, null);
			if(!b) {
				throw new Exception("数据发送失败");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
}
