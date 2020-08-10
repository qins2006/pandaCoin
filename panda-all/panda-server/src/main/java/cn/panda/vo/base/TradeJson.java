package cn.panda.vo.base;

import org.springframework.web.multipart.MultipartFile;

public class TradeJson {
	
	/**
	 * 企业英文缩写名称
	 */
	private String enterpriseEnShortName;
	/**
	 * 数据内容
	 */
	private String content;
	/**
	 * 创建时间
	 */
	private String createTime;
	
	/**
	 * 交易编号
	 */
	private String orderNo;
	
	/**
	 * 交易类型
	 * 
	 */
	private String tradeType;
	
	/**
	 * 附件
	 */
	private MultipartFile file;
	
	private String name;
	
	private String md5;

	@Override
	public String toString() {
		return "TradeJson [enterpriseEnShortName=" + enterpriseEnShortName + ", content=" + content + ", createTime="
				+ createTime + ", tradeType=" + tradeType + "]";
	}

	public String getEnterpriseEnShortName() {
		return enterpriseEnShortName;
	}

	public void setEnterpriseEnShortName(String enterpriseEnShortName) {
		this.enterpriseEnShortName = enterpriseEnShortName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	
	

}
