package cn.panda.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.panda.dao.CoinDao;
import cn.panda.dao.DataDao;
import cn.panda.entity.Account;
import cn.panda.entity.Coin;
import cn.panda.entity.TradeEntity;
import cn.panda.service.DataService;


@Service
public class DataServiceImpl implements DataService {
	@Autowired
	private DataDao dataDao;
	@Autowired
	private CoinDao coinDao;
	@Override
	public Account queryAccount(String address, String collectionName) {
		return dataDao.queryAccount(address, collectionName);
	}

	@Override
	public void transfer(TradeEntity tradeEntity) {
		String fromAddress = tradeEntity.getFromAddress();
		String coinType = tradeEntity.getCoinType();
		String toAddress = tradeEntity.getToAddress();
		String gasCoinType = tradeEntity.getCoinType();
	}

	@Override
	public Coin queryCoin(String coinType) {
		return coinDao.queryCoin(coinType);
	}

	@Override
	public void save(Coin coin) {
		coinDao.save(coin);
	}
	
}
