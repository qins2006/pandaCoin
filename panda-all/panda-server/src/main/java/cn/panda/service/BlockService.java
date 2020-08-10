package cn.panda.service;

import java.math.BigInteger;
import java.util.List;

import cn.panda.entity.TradeEntity;

public interface BlockService {
	/**
	 * 获取区块内容
	 * @param blockIndex
	 * @return
	 */
	public List<TradeEntity> queryTradeEntity(String blockIndex);
	/**
	 * 这是创建代币的交易
	 * @param te
	 */
	public void createCoin(TradeEntity te, BigInteger blockIndex);
	/**
	 * 处理交易
	 * @param te
	 */
	public void dealTrade(TradeEntity te);
}
