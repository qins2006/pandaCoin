package cn.panda.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import cn.panda.controller.base.BaseController;
import cn.panda.entity.Account;
import cn.panda.entity.Coin;
import cn.panda.service.DataService;
import cn.panda.vo.base.TradeRespVo;

@RestController
public class DataController extends BaseController{
	
	private static final Logger logger = Logger.getLogger(DataController.class);
	@Autowired
	private DataService dataService;

	@GetMapping(value="/data/{pair}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<TradeRespVo> queryamount(@PathVariable("pair") String pair){
		TradeRespVo tradeRespVo = new TradeRespVo();
		try {
			if(StringUtils.isBlank(pair)) {
				throw new Exception("参数不能为空");
			}
			if(!pair.contains("@")) {
				throw new Exception("参数不合法");
			}
			String[] pairs = pair.split("@");
			String address = pairs[0];
			String coinType = pairs[1];
			if(34 != address.length()) {
				throw new Exception("地址长度不对");
			}
			Coin c = dataService.queryCoin(coinType);
			if(c == null) {
				throw new Exception("币种不存在");
			}
			Account account = dataService.queryAccount(address, getCollectionName(coinType));
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("amount", account.getAmount());
			result.put("address", account.getAddress());
			tradeRespVo.setCode(0);
			tradeRespVo.setMsg("Success");
			tradeRespVo.setContent(result);
		}catch (Exception e) {
			tradeRespVo.setCode(-1);
			tradeRespVo.setMsg(e.getMessage());
		}
		return new ResponseEntity<TradeRespVo>(tradeRespVo,HttpStatus.OK);
	}
	
}
