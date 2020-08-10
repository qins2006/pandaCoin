package cn.panda.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import cn.panda.dao.CoinDao;
import cn.panda.entity.Coin;

@Repository
public class CoinDaoImpl implements CoinDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Override
	public void save(Coin coin) {
		mongoTemplate.save(coin);
		
	}
	@Override
	public Coin queryCoin(String coinType) {
		Criteria criteria = Criteria.where("coinName").is(coinType);
		Query query = new Query();
		query.addCriteria(criteria);
		return mongoTemplate.findOne(query, Coin.class);
	}
	@Override
	public void update(Coin coin) {
		Query query = new Query();
		Criteria criteria = Criteria.where("coinName").is(coin.getCoinName());
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("blockIndex", coin.getBlockIndex());
		mongoTemplate.updateFirst(query, update, Coin.class);
	}
	
}
