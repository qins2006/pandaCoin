package cn.panda.service;

import cn.panda.entity.Config;

public interface ConfigService {
	/**
	 * 保存配置
	 * @param config
	 */
	public void save(Config config);
	/**
	 * 查询配置
	 * @param cKey
	 * @param cValue
	 * @return
	 */
	public Config queryConfig(String modual, String ckey);
	/**
	 * 更新配置
	 */
	public void updateConfig(Config config);
}
