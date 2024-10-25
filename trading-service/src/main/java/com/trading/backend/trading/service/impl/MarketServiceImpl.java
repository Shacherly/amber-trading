package com.google.backend.trading.service.impl;

import com.google.backend.trading.client.feign.KlineInfoClient;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeUserMarketFavoriteLiteMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeUserMarketFavoriteMapper;
import com.google.backend.trading.dao.mapper.TradeUserMarketFavoriteLiteMapper;
import com.google.backend.trading.dao.mapper.TradeUserMarketFavoriteMapper;
import com.google.backend.trading.dao.model.TradeUserMarketFavorite;
import com.google.backend.trading.dao.model.TradeUserMarketFavoriteExample;
import com.google.backend.trading.dao.model.TradeUserMarketFavoriteLite;
import com.google.backend.trading.dao.model.TradeUserMarketFavoriteLiteExample;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.commonconfig.dto.CoinSymbolConfig;
import com.google.backend.trading.model.favorite.api.LiteMarketSymbolVo;
import com.google.backend.trading.model.favorite.api.MarketSymbolVo;
import com.google.backend.trading.model.kline.dto.PriceChange24h;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.MarketService;
import com.google.backend.trading.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author adam.wang
 * @date 2021/10/4 17:01
 */
@Service
public class MarketServiceImpl implements MarketService {

    @Resource
    private DefaultTradeUserMarketFavoriteMapper defaultTradeUserMarketFavoriteMapper;

    @Autowired
    private TradeUserMarketFavoriteMapper userMarketFavoriteMapper;

    @Resource
    private DefaultTradeUserMarketFavoriteLiteMapper defaultTradeUserMarketFavoriteLiteMapper;
    @Resource
    private TradeUserMarketFavoriteLiteMapper tradeUserMarketFavoriteLiteMapper;


    @Autowired
    private KlineInfoClient klineInfoClient;

    @Override
    public List<String> listAllFavorite(String uid) {
        TradeUserMarketFavoriteExample example = new TradeUserMarketFavoriteExample();
        TradeUserMarketFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<TradeUserMarketFavorite> userMarketFavorites = defaultTradeUserMarketFavoriteMapper.selectByExample(example);
        if (userMarketFavorites.isEmpty()) {
            TradeUserMarketFavorite init = new TradeUserMarketFavorite();
            init.setSymbolArr(Constants.DEFAULT_MARKET_FAVORITE_LIST);
            init.setUid(uid);
            userMarketFavoriteMapper.insertIgnoreConflict(init);
            return new ArrayList<>(Arrays.asList(Constants.DEFAULT_MARKET_FAVORITE_LIST));
        }
        return new ArrayList<>(Arrays.asList(userMarketFavorites.get(0).getSymbolArr()));
    }

    @Override
    public void favoriteOverride(String uid, String[] symbolList) {
        String[] newSymbolList = (String[]) Arrays.stream(symbolList).filter(symbol -> !symbol.contains(Constants.IDK_COIN)).toArray();
        TradeUserMarketFavoriteExample example = new TradeUserMarketFavoriteExample();
        TradeUserMarketFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        TradeUserMarketFavorite update = new TradeUserMarketFavorite();
        update.setSymbolArr(newSymbolList);
        defaultTradeUserMarketFavoriteMapper.updateByExample(update, example);
    }

    @Override
    public void favoriteUpdate(String uid, String symbol, boolean favorite) {
        if (symbol.contains(Constants.IDK_COIN)) {
            return;
        }
        if (favorite) {
            userMarketFavoriteMapper.addSymbolToFavorite(uid, symbol);
        } else {
            userMarketFavoriteMapper.removeSymbolFromFavorite(uid, symbol);
        }
    }

    @Override
    public List<MarketSymbolVo> marketSymbolList(String uid, String type, boolean onlyFavorite) {
        List<String> favoriteSymbolList = Collections.emptyList();
        if (null != uid) {
            favoriteSymbolList = listAllFavorite(uid);
        }
        Response<List<PriceChange24h>> res =
                klineInfoClient.priceChange24h(StringUtils.collectionToDelimitedString(SymbolDomain.CACHE.keySet(), ","));
        //symbol - 24h price change rate
        Map<String, BigDecimal> priceChange24hMap = new HashMap<>();
        if (BusinessExceptionEnum.SUCCESS.getCode() == res.getCode() && null != res.getData()) {
            List<PriceChange24h> priceChange24hList = res.getData();
            priceChange24hMap = priceChange24hList.stream().collect(Collectors.toMap(PriceChange24h::getSymbol, priceChange24h -> {
                BigDecimal price = priceChange24h.getPrice();
                BigDecimal priceOld = priceChange24h.getPriceOld();
                return price.subtract(priceOld).divide(priceOld, Constants.PRICE_CHANGE_RATE_PRECISION, RoundingMode.DOWN);
            }));
        }
        Map<String, BigDecimal> finalPriceChange24hMap = priceChange24hMap;
        List<String> finalFavoriteSymbolList = favoriteSymbolList;
        return SymbolDomain.CACHE.values().stream().filter(symbolDomain -> {
            CoinSymbolConfig config = symbolDomain.getCoinSymbolConfig();
            if (Constants.SPOT_TYPE.equals(type)) {
                return config.isSpotValid();
            } else {
                return config.isMarginValid();
            }
        }).filter(symbolDomain -> !symbolDomain.getSymbol().contains(Constants.IDK_COIN)).filter(symbolDomain -> !onlyFavorite || finalFavoriteSymbolList.contains(symbolDomain.getSymbol())).map(symbolDomain -> {
            MarketSymbolVo vo = new MarketSymbolVo();
            String symbol = symbolDomain.getSymbol();
            Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
            String base = coinPair.getFirst();
            vo.setSymbol(symbol);
            CoinSymbolConfig config = symbolDomain.getCoinSymbolConfig();
            vo.setPriority(config.getPriority());
            vo.setPrice(symbolDomain.midPriceOrZero());
            int index = finalFavoriteSymbolList.indexOf(symbol);
            if (index >= 0) {
                vo.setFavorite(true);
                vo.setFavoritePriority(index);
            }
            vo.setChange24h(finalPriceChange24hMap.getOrDefault(symbol, BigDecimal.ZERO));
            String marketCategory = CoinDomain.nonNullGet(base).getCommonConfig().getMarketCategory();
            vo.setMarketCategory(marketCategory);
            return vo;
        }).sorted(Comparator.comparing(MarketSymbolVo::getPriority)).collect(Collectors.toList());
    }

    @Override
    public void favoriteUpdateLite(String uid, String symbol, boolean favorite) {
        if (symbol.contains(Constants.IDK_COIN)) {
            return;
        }
        this.queryTradeUserMarketFavoriteLiteByUid(uid);
        if (favorite) {
            tradeUserMarketFavoriteLiteMapper.addSymbolToFavorite(uid, symbol);
        } else {
            tradeUserMarketFavoriteLiteMapper.removeSymbolFromFavorite(uid, symbol);
        }
    }

    @Override
    public TradeUserMarketFavoriteLite queryTradeUserMarketFavoriteLiteByUid(String uid) {
        TradeUserMarketFavoriteLiteExample example = new TradeUserMarketFavoriteLiteExample();
        TradeUserMarketFavoriteLiteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<TradeUserMarketFavoriteLite> marketFavoriteLites = defaultTradeUserMarketFavoriteLiteMapper.selectByExample(example);
        if (marketFavoriteLites.isEmpty()) {
            TradeUserMarketFavoriteLite init = new TradeUserMarketFavoriteLite();
            init.setSymbolArr(new String[]{});
            init.setUid(uid);
            tradeUserMarketFavoriteLiteMapper.insertIgnoreConflict(init);
            return init;
        } else {
            return marketFavoriteLites.get(0);
        }
    }

    @Override
    public List<LiteMarketSymbolVo> getLiteMarket(UserInfo userInfo) {
        List<String> userLiteMarket = new ArrayList<>();
        if (null != userInfo) {
            String uid = userInfo.getUid();
            TradeUserMarketFavoriteLite tradeUserMarketFavoriteLite = this.queryTradeUserMarketFavoriteLiteByUid(uid);
            userLiteMarket = new ArrayList<>(Arrays.asList(tradeUserMarketFavoriteLite.getSymbolArr()));
        }
        return mergeUserLiteCoin(userLiteMarket);
    }

    private List<LiteMarketSymbolVo> mergeUserLiteCoin(List<String> userLiteMarket) {
        List<CoinDomain> liteCoin = CoinDomain.getLiteCoin();
        List<LiteMarketSymbolVo> userTopMarket = userLiteMarket.stream()
                .map(e -> new LiteMarketSymbolVo(e, true))
                .collect(Collectors.toList());
        List<LiteMarketSymbolVo> liteMarketSymbolVoList = liteCoin.stream()
                .filter(e -> userTopMarket.stream().noneMatch(utmarket -> Objects.equals(utmarket.getSymbol(), e.getName() + Constants.BASE_QUOTE)))
                .map(e -> new LiteMarketSymbolVo(e.getName() + Constants.BASE_QUOTE, false)).collect(Collectors.toList());
        userTopMarket.addAll(liteMarketSymbolVoList);


        return userTopMarket.stream().filter(e -> {
            String symbol = e.getSymbol();
            SymbolDomain symbolDomain = SymbolDomain.nullableGet(symbol);

            String baseCoin = CommonUtils.getBaseCoin(symbol);
            CoinDomain coinDomain = CoinDomain.nullableGet(baseCoin);

            return symbolDomain != null && coinDomain != null
                    && coinDomain.getSwapConfig().isSupport()
                    && !org.apache.commons.lang3.StringUtils.equals(baseCoin, Constants.BASE_COIN);
        }).collect(Collectors.toList());
    }
}
