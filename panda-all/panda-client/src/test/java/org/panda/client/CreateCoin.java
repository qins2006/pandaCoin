package org.panda.client;

import java.io.UnsupportedEncodingException;

import org.panda.HTTPHelper;

import cn.panda.entity.TradeEntity;
import cn.panda.utils.ECDSAUtils;

public class CreateCoin {
	/**
	 * 创建主币种panda币（熊猫币）
	 */
	@org.junit.Test
	public void createPanda() {
		try {
			String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBDeowxYhK+Qkrv9ScbY1qtRXMvZFxeFmUNxOzEgq6VuA==";
			String publicKey= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/5xeVT8EEdauYQUs5KV21Ozv9xmWzyMDoGaFg9ywyAcAoawz+Aj0ius4jyvjy+wGnafelonUYJHoC/bOroQiFw==";
			String address = "0x4173c715a814a30c9167d24aeb01aaef";
			
			HTTPHelper httpHelper = new HTTPHelper();
			TradeEntity te = new TradeEntity();
			te.setAmount("10000000");
			te.setCoinType("panda");
			te.setFromAddress(address);
			te.setGas("0");
			te.setGasCoinType("panda");
			te.setToAddress(address);
			te.setType("1");
			te.setTradehash(te.md5Tradehash());
			te.setPublickey(publicKey);
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);
			te.setSign(sign);
			
			boolean b = HTTPHelper.sendTrade(te);
			if(b) {
				System.out.println(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建子币种deer币种(小鹿币)
	 */
	@org.junit.Test
	public void createDeer() {
		try {
			String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBDeowxYhK+Qkrv9ScbY1qtRXMvZFxeFmUNxOzEgq6VuA==";
			String publicKey= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/5xeVT8EEdauYQUs5KV21Ozv9xmWzyMDoGaFg9ywyAcAoawz+Aj0ius4jyvjy+wGnafelonUYJHoC/bOroQiFw==";
			String address = "0x4173c715a814a30c9167d24aeb01aaef";
			
			HTTPHelper httpHelper = new HTTPHelper();
			TradeEntity te = new TradeEntity();
			te.setAmount("1000000");
			te.setCoinType("Deer");
			te.setFromAddress(address);
			te.setGas("1000");
			te.setGasCoinType("panda");
			te.setToAddress(address);
			te.setType("1");
			te.setTradehash(te.md5Tradehash());
			te.setPublickey(publicKey);
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);
			te.setSign(sign);
			boolean b = HTTPHelper.sendTrade(te);
			if(b) {
				System.out.println(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@org.junit.Test
	public void trade() {
		
		try {
			String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBDeowxYhK+Qkrv9ScbY1qtRXMvZFxeFmUNxOzEgq6VuA==";
			String publicKey= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/5xeVT8EEdauYQUs5KV21Ozv9xmWzyMDoGaFg9ywyAcAoawz+Aj0ius4jyvjy+wGnafelonUYJHoC/bOroQiFw==";
			String address = "0x4173c715a814a30c9167d24aeb01aaef";
			
			String address0 = "0x694bc428d78c5149158c29a2a02a54b4";
			String publicKey0= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEHnHSQn7sgHgfV1adaUhZ45O0v5EUkR1Oee1Vilg6SEW6kBmkl/jVN/uXjluZYGA7g0iGMeiM7sJei2iUd6pqzw==";
			String privateKey0 = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCek3iy79+kskR1HkKYaX7AzEKrasnUqOqKdss/RrdUyQ==";
			
			HTTPHelper httpHelper = new HTTPHelper();
			TradeEntity te = new TradeEntity();
			te.setAmount("10.02");//转账币额
			te.setCoinType("Deer");
			te.setFromAddress(address);
			te.setGas("0.0052");//gas
			//te.setGasCoinType("panda");
			te.setGasCoinType("panda");
			te.setToAddress(address0);
			te.setType("0");
			te.setTradehash(te.md5Tradehash());
			te.setPublickey(publicKey);
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);
			te.setSign(sign);
			boolean b = HTTPHelper.sendTrade(te);
			if(b) {
				System.out.println(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
