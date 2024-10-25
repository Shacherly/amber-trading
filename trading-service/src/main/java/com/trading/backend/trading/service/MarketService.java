package com.google.backend.trading.service;

import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.dao.model.TradeUserMarketFavoriteLite;
import com.google.backend.trading.model.favorite.api.LiteMarketSymbolVo;
import com.google.backend.trading.model.favorite.api.MarketSymbolVo;
import com.google.backend.trading.model.user.UserInfo;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/4 16:52
 */
public interface MarketService {

    /**
     * 查询所有收藏列表
     * @param uid
     * @return
     */
    List<String> listAllFavorite(String uid);

    /**
     * 覆盖更新收藏
	 * @param uid
	 * @param symbolList
	 */
    void favoriteOverride(String uid, String[] symbolList);


	/**
	 * 更新收藏列表
	 * @param uid
	 * @param symbol
	 * @param favorite true 添加收藏 false 取消收藏
	 */
	void favoriteUpdate(String uid, String symbol, boolean favorite);

	/**
	 * 市场币对列表
	 * @param uid
	 * @param type
	 * @param onlyFavorite
	 * @return
	 */
	List<MarketSymbolVo> marketSymbolList(String uid, String type, boolean onlyFavorite);

	void favoriteUpdateLite(String uid, String symbol, boolean favorite);

	TradeUserMarketFavoriteLite queryTradeUserMarketFavoriteLiteByUid(String uid);

	List<LiteMarketSymbolVo> getLiteMarket(UserInfo userInfo);
}
