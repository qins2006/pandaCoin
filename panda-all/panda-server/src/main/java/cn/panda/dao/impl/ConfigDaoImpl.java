package cn.panda.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.Mongo;

import cn.panda.dao.ConfigDao;
import cn.panda.entity.Account;
import cn.panda.entity.Config;

@Repository
public class ConfigDaoImpl implements ConfigDao{

	@Autowired
	private MongoTemplate mongoTemplate;
	@Override
	public void save(Config config) {
		mongoTemplate.save(config);
	}

	@Override
	public Config queryConfig(String modual, String ckey) {
		Criteria criteria = Criteria.where("modual").is(modual);
		criteria.and("ckey").is(ckey);
		Query query = new Query();
		query.addCriteria(criteria);
		return mongoTemplate.findOne(query, Config.class);
	}

	@Override
	public void updateConfig(Config config) {
		Query query = new Query();
		Criteria criteria = Criteria.where("modual").is(config.getModual());
		criteria.and("ckey").is(config.getCkey());
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("cvalue", config.getCvalue());
		mongoTemplate.updateFirst(query, update, Config.class);
	}

}
