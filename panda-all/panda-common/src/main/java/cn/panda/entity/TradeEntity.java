package cn.panda.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import cn.panda.utils.ECDSAUtils;

public class TradeEntity implements Serializable{
	
	/**
	 * 交易哈希值
	 */
	private String tradehash;
	/**
	 * 转出地址
	 */
	private String fromAddress;
	/**
	 * 转入地址
	 */
	private String toAddress;
	/**
	 * 转出金额
	 */
	private String amount;
	/**
	 * 交易币种
	 */
	private String coinType;
	/**
	 * 燃油费
	 */
	private String gas;
	/**
	 * 燃油费币种
	 */
	private String gasCoinType;
	/**
	 * 0 : 转账交易
	 * 1:	创建币种
	 * (当 fromAddress=toAddress时，coinType 不存在，gasCoinType='panda'时，代表是创建代币)。
	 */
	private String type;
	/**
	 * 签名
	 */
	private String sign;
	/**
	 * 私钥
	 */
	private String publickey;
	
	@Override
	public String toString() {
		return "TradeEntity [fromAddress=" + fromAddress + ", toAddress=" + toAddress + ", amount=" + amount
				+ ", coinType=" + coinType + ", gas=" + gas + ", gasCoinType=" + gasCoinType + ", type" + type + "]";
	}
	/**
	 * 交易哈希
	 * @return
	 */
	public String md5Tradehash() {
		return "0x" + ECDSAUtils.encryptMD5(toString());
	}
	/**
	 * 验证地址
	 * @return
	 * @throws Exception 
	 */
	public void verifyAddress() throws Exception {
		String address0 = "0x" + ECDSAUtils.encryptMD5(publickey);
		if(!fromAddress.equals(address0)) {
			throw new Exception("地址与公钥不匹配");
		}
	}
	/**
	 * 验证签名
	 * @return
	 * @throws Exception 
	 */
	public void verifySign() throws Exception {
		try {
			boolean b =  ECDSAUtils.verify(tradehash.getBytes("UTF-8"), publickey, sign);
			if(!b) {
				throw new Exception("签名验证不通过");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("签名验证不通过");
		}
	}
	
	public String getTradehash() {
		return tradehash;
	}
	public void setTradehash(String tradehash) {
		this.tradehash = tradehash;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCoinType() {
		return coinType;
	}
	public void setCoinType(String coinType) {
		this.coinType = coinType;
	}
	public String getGas() {
		return gas;
	}
	public void setGas(String gas) {
		this.gas = gas;
	}
	public String getGasCoinType() {
		return gasCoinType;
	}
	public void setGasCoinType(String gasCoinType) {
		this.gasCoinType = gasCoinType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getPublickey() {
		return publickey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	
}
