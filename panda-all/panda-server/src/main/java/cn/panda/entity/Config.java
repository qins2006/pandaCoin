package cn.panda.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "t_config")
public class Config {
	
	@Field("modual")
	private String modual;
	
	@Field("ckey")
	private String ckey;
	
	@Field("cvalue")
	private String cvalue;
	
	public String getModual() {
		return modual;
	}
	public void setModual(String modual) {
		this.modual = modual;
	}
	public String getCkey() {
		return ckey;
	}
	public void setCkey(String ckey) {
		this.ckey = ckey;
	}
	public String getCvalue() {
		return cvalue;
	}
	public void setCvalue(String cvalue) {
		this.cvalue = cvalue;
	}
	
}
