package cn.panda.timer;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.panda.config.ConfigConstant;
import cn.panda.entity.Config;
import cn.panda.entity.TradeEntity;
import cn.panda.service.BlockService;
import cn.panda.service.ConfigService;

/**
 * 区块更新。
 * 获得自己的交易。并确认自己是否创建币种成功、交易余额计算。
 * @author www
 *
 */
@Component
public class BlockUpdateTimer {
	private static final Logger logger = Logger.getLogger(BlockUpdateTimer.class);
	
	@Autowired
	private ConfigService configService;
	@Autowired
	private BlockService blockService;

	@Scheduled(cron="0/2 * * * * ?")
	public void run() {
		//从本地区块服务读取区块
		try {
			Config c = configService.queryConfig(ConfigConstant.UPDATEMUDUAL, ConfigConstant.BLOCKINDEX);
			BigInteger blockIndex = new BigInteger(c.getCvalue());
			List<TradeEntity> tes = blockService.queryTradeEntity(c.getCvalue());
			if(tes == null) {
				throw new Exception("未找到对应区块，将重新尝试。");
			}
			for(TradeEntity te : tes) {
				//是否创建币种
				if("1".equals(te.getType())) {
					blockService.createCoin(te, blockIndex);
				}else if("0".equals(te.getType())) {
					blockService.dealTrade(te);
				}
			}
			c.setCvalue(blockIndex.add(BigInteger.ONE).toString());
			configService.updateConfig(c);
			logger.info("区块更新至：" + c.getCvalue());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
