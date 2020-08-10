package cn.panda.dao.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import cn.panda.dao.DataDao;
import cn.panda.entity.Account;

@Repository
public class DataDaoImpl implements DataDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void save(Account account, String collectionName) {
		mongoTemplate.save(account, collectionName);
	}

	@Override
	public Account queryAccount(String address, String collectionName) {
		Criteria criteria = Criteria.where("address").is(address);
		Query query = new Query();
		query.addCriteria(criteria);
		return mongoTemplate.findOne(query, Account.class, collectionName);
	}

	@Override
	public void updateAccount(String address, String amount, String collectionName) {
		Query query = new Query();
		Criteria criteria = Criteria.where("address").is(address);
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("amount", amount);
		mongoTemplate.updateFirst(query, update, collectionName);
	}

}
