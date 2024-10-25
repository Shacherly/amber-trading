package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author trading
 * @date 2021/10/11 14:45
 */
public interface TradeUserTradeSettingMapper {

	int insertIgnoreConflict(TradeUserTradeSetting setting);

	void updateUserTradeSettingByUid(TradeUserTradeSetting setting);

	List<String> selectNeedSettleUidList(@Param("list") List<String> settleTimeZoneIdList, @Param("settleTime") Date settleTime);
}
