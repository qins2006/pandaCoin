package cn.panda.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.panda.config.ConfigConstant;
import cn.panda.dao.ConfigDao;
import cn.panda.entity.Config;
import cn.panda.service.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService {
	@Autowired
	private ConfigDao configDao;
	@Override
	public void save(Config config) {
		configDao.save(config);
	}

	@Override
	public Config queryConfig(String modual, String ckey) {
		Config c = configDao.queryConfig(modual, ckey);
		if(c == null) {
			c = new Config();
			c.setModual(ConfigConstant.UPDATEMUDUAL);
			c.setCkey(ConfigConstant.BLOCKINDEX);
			c.setCvalue("0");
			configDao.save(c);
		}
		return c;
	}

	@Override
	public void updateConfig(Config config) {
		configDao.updateConfig(config);
	}

}
