package cn.panda.controller.base;

import java.util.UUID;

public class BaseController {
	protected String getCollectionName(String coinType) {
		return "t_account_" + coinType;
	}
	
	protected String uuid() {
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString();
		return uuidStr;
	}
}
