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
			//私钥
			String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBDeowxYhK+Qkrv9ScbY1qtRXMvZFxeFmUNxOzEgq6VuA==";
			//公钥
			String publicKey= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/5xeVT8EEdauYQUs5KV21Ozv9xmWzyMDoGaFg9ywyAcAoawz+Aj0ius4jyvjy+wGnafelonUYJHoC/bOroQiFw==";
			//地址
			String address = "0x4173c715a814a30c9167d24aeb01aaef";
			
			HTTPHelper httpHelper = new HTTPHelper();
			TradeEntity te = new TradeEntity();
			te.setAmount("10000000");//主币种初始额
			te.setCoinType("panda");//主币种名称
			te.setFromAddress(address);//转出地址
			te.setGas("0");//主币种这里为0.
			te.setGasCoinType("panda");//gas币种也为0
			te.setToAddress(address);//转入地址=转出地址
			te.setType("1");//交易类型1
			te.setTradehash(te.md5Tradehash());//生成交易哈希
			te.setPublickey(publicKey);//公钥
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);//签名
			te.setSign(sign);
			//发送创建主币交易至项目后台，后台转发至HiteBaas平台
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
			//私钥
			String privateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBDeowxYhK+Qkrv9ScbY1qtRXMvZFxeFmUNxOzEgq6VuA==";
			//公钥
			String publicKey= "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/5xeVT8EEdauYQUs5KV21Ozv9xmWzyMDoGaFg9ywyAcAoawz+Aj0ius4jyvjy+wGnafelonUYJHoC/bOroQiFw==";
			//地址
			String address = "0x4173c715a814a30c9167d24aeb01aaef";
			
			HTTPHelper httpHelper = new HTTPHelper();
			TradeEntity te = new TradeEntity();
			te.setAmount("1000000");//初始币额
			te.setCoinType("Deer");//子币种名称
			te.setFromAddress(address);//创建地址（初始额接收地址）
			te.setGas("1000");//消耗1000的panda币作为gas
			te.setGasCoinType("panda");//使用主币种作为燃油费
			te.setToAddress(address);//转出地址=转入地址
			te.setType("1");//交易类型为1
			te.setTradehash(te.md5Tradehash());//生成交易哈希值
			te.setPublickey(publicKey);//公钥
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);//签名
			te.setSign(sign);
			//发送创建主币交易至项目后台，后台转发至HiteBaas平台
			boolean b = HTTPHelper.sendTrade(te);
			if(b) {
				System.out.println(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 币种交易
	 */
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
			te.setCoinType("Deer");//币种名称，如果这里是主币转账的话则为  panda
			te.setFromAddress(address);//转出地址
			te.setGas("0.0052");//gas
			//te.setGasCoinType("panda");
			te.setGasCoinType("panda");//使用主币作为燃油费
			te.setToAddress(address0);//转入地址
			te.setType("0");//交易类型0
			te.setTradehash(te.md5Tradehash());//生成交易哈希
			te.setPublickey(publicKey);//转出公钥
			String sign = ECDSAUtils.sign(te.getTradehash().getBytes("UTF-8"), privateKey);//使用转出私钥签名
			te.setSign(sign);
			//发送创建主币交易至项目后台，后台转发至HiteBaas平台
			boolean b = HTTPHelper.sendTrade(te);
			if(b) {
				System.out.println(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
