package cn.panda.dao;

import cn.panda.entity.Coin;

public interface CoinDao {
	/**
	 * 保存币种信息
	 * @param coin
	 */
	public void save(Coin coin);
	/**
	 * 查询币种
	 * @param coinType
	 * @return
	 */
	public Coin queryCoin(String coinType);
	/**
	 * 
	 * @param coin
	 */
	public void update(Coin coin);
}
