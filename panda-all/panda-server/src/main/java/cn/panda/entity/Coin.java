package cn.panda.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "t_coin_list")
public class Coin {
	
	@Field("coinName")
	private String coinName;
	
	@Field("blockIndex")
	private String blockIndex;
	
	@Field("createTime")
	private String createTime;
	
 	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getBlockIndex() {
		return blockIndex;
	}
	public void setBlockIndex(String blockIndex) {
		this.blockIndex = blockIndex;
	}
}
