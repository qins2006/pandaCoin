package cn.panda.dao;

import cn.panda.entity.Account;

public interface DataDao {
	/**
	 * 保存地址、余额
	 * @param account
	 */
	public void save(Account account, String collectionName);
	/**
	 * 通过地址查找账户
	 * @param address
	 * @return
	 */
	public Account queryAccount(String address, String collectionName);
	/**
	 * 更新地址余额
	 * @param address
	 * @param amount
	 * @param tableName
	 */
	public void updateAccount(String address, String amount, String collectionName);
}
