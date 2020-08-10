package cn.panda.service;

import cn.panda.entity.Account;
import cn.panda.entity.Coin;
import cn.panda.entity.TradeEntity;

public interface DataService {
	/**
	 * 通过地址查找账户
	 * @param address
	 * @return
	 */
	public Account queryAccount(String address, String collectionName);
	/**
	 * 转账方法
	 * @param tradeEntity
	 */
	public void transfer(TradeEntity tradeEntity);
	/**
	 * 查询币种
	 * @param coinType
	 * @return
	 */
	public Coin queryCoin(String coinType);
	/**
	 * 保存币种信息
	 * @param coin
	 */
	public void save(Coin coin);
}
