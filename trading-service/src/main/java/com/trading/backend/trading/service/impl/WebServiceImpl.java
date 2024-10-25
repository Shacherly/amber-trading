package com.google.backend.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotMarginMiddleOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.dao.model.TradeMarginOrderModification;
import com.google.backend.trading.dao.model.TradeSpotMarginMiddleOrder;
import com.google.backend.trading.dao.model.TradeSpotMarginMiddleOrderExample;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderExample;
import com.google.backend.trading.dao.model.TradeSpotOrderModification;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.mapstruct.web.WebMapStruct;
import com.google.backend.trading.model.common.PageReq;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.model.web.MiddleOrderType;
import com.google.backend.trading.model.web.OrderHistoryReq;
import com.google.backend.trading.model.web.OrderHistoryRes;
import com.google.backend.trading.model.web.OrderInfoRes;
import com.google.backend.trading.model.web.OrderModificationVo;
import com.google.backend.trading.model.web.TransactionInfoRes;
import com.google.backend.trading.model.web.TransactionReq;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.WebService;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 现货处理逻辑的实现
 *
 * @author savion.chen
 * @date 2021/9/30 10:46
 */
@Slf4j
@Service
public class WebServiceImpl implements WebService {
    @Resource
    private DefaultTradeSpotMarginMiddleOrderMapper defaultTradeSpotMarginMiddleOrderMapper;

    @Resource
    private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;

    @Resource
    private DefaultTradeSpotOrderMapper defaultTradeSpotOrderMapper;

    @Resource
    private DefaultTradeTransactionMapper defaultTradeTransactionMapper;

    @Resource
    private WebMapStruct webMapStruct;
    @Resource
    private MarginService marginService;
    @Resource
    private SpotService spotService;
    @Override
    public PageResult<OrderInfoRes> orderActive(PageReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(),true);
        TradeSpotMarginMiddleOrderExample example = new TradeSpotMarginMiddleOrderExample();
        TradeSpotMarginMiddleOrderExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("MTIME DESC");
        criteria.andUidEqualTo(uid);
        criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
        return this.getOrderInfoPageResult(example);
    }

    /**
     * adam.wang modify 20211016
     * @param req
     * @param uid
     * @return
     */
    @Override
    public PageResult<OrderHistoryRes> orderHistory(OrderHistoryReq req, String uid) {
        List<OrderHistoryRes> res = new ArrayList<>();

        //中间表获取历史数据
        PageInfo<TradeSpotMarginMiddleOrder> pageInfo = getTradeSpotMarginMiddleOrderPageInfo(req, uid);
        List<TradeSpotMarginMiddleOrder> list = pageInfo.getList();
        //获取订单详情
        if(ListUtil.isNotEmpty(list)){
            for (TradeSpotMarginMiddleOrder item : list) {
                OrderHistoryRes orderInfo ;
                if (MiddleOrderType.MARGIN.name().equals(item.getType())) {
                    //杠杆逻辑
                    TradeMarginOrderExample example = new TradeMarginOrderExample();
                    example.createCriteria().andUuidEqualTo(item.getOrderId());
                    List<TradeMarginOrder> tradeMarginOrders = defaultTradeMarginOrderMapper.selectByExample(example);
                    if (CollectionUtils.isEmpty(tradeMarginOrders)) {
                        continue;
                    }
                    TradeMarginOrder tradeMarginOrder = tradeMarginOrders.get(0);
                    orderInfo = webMapStruct.tradeMarginOrder2OrderHistoryRes(tradeMarginOrder);
                    //查询修改记录
                    List<TradeMarginOrderModification> list1 = marginService.orderModifications(tradeMarginOrder.getUuid());
                    if (ListUtil.isNotEmpty(list1)) {
                        List<OrderModificationVo> orderModificationVos = webMapStruct.tradeMarginOrderModification2Vos(list1);
                        orderInfo.setList(orderModificationVos);
                    }
                } else {
                    //现货逻辑
                    TradeSpotOrderExample example = new TradeSpotOrderExample();
                    example.createCriteria().andUuidEqualTo(item.getOrderId());
                    List<TradeSpotOrder> tradeSpotOrders = defaultTradeSpotOrderMapper.selectByExample(example);
                    if (CollectionUtils.isEmpty(tradeSpotOrders)) {
                        continue;
                    }
                    TradeSpotOrder tradeSpotOrder = tradeSpotOrders.get(0);
                    orderInfo= webMapStruct.tradeSpotOrder2OrderHistoryRes(tradeSpotOrder);
                    //查询修改记录
                    List<TradeSpotOrderModification> list1 = spotService.orderModifications(tradeSpotOrder.getUuid());
                    if (ListUtil.isNotEmpty(list1)) {
                        List<OrderModificationVo> orderModificationVos = webMapStruct.tradeSpotOrderModification2Vos(list1);
                        orderInfo.setList(orderModificationVos);
                    }
                }
                res.add(orderInfo);
            }
        }
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), res);
    }

    /**
     * 获取杠杆、现货订单数据
     * @param req
     * @param uid
     * @return
     */
    private PageInfo<TradeSpotMarginMiddleOrder> getTradeSpotMarginMiddleOrderPageInfo(OrderHistoryReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(),true);
        TradeSpotMarginMiddleOrderExample example = new TradeSpotMarginMiddleOrderExample();
        TradeSpotMarginMiddleOrderExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("MTIME DESC");
        criteria.andUidEqualTo(uid);

        if(StringUtils.isNotBlank(req.getStatus())){
            criteria.andStatusIn(OrderStatus.getByCode(req.getStatus()).stream().map(OrderStatus::getName).collect(Collectors.toList()));
        }
        criteria.andStatusIn(OrderStatus.HISTORY_STATUS);
        if(StringUtils.isNotBlank(req.getDirection())){
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (StringUtils.isNotEmpty(req.getSymbol())) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        if (req.getStartTime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartTime());
            criteria.andMtimeGreaterThan(startTime);
        }
        if (req.getEndTime() != null) {
            Date endTime = CommonUtils.getNowTime(req.getEndTime());
            criteria.andMtimeLessThan(endTime);
        }
        List<TradeSpotMarginMiddleOrder> middleOrders = defaultTradeSpotMarginMiddleOrderMapper.selectByExample(example);
        PageInfo<TradeSpotMarginMiddleOrder> pageInfo = new PageInfo<>(middleOrders);
        return pageInfo;
    }

    private PageResult<OrderInfoRes> getOrderInfoPageResult(TradeSpotMarginMiddleOrderExample example) {
        List<TradeSpotMarginMiddleOrder> middleOrders = defaultTradeSpotMarginMiddleOrderMapper.selectByExample(example);
        List<String> marginOrderIds = new ArrayList<>();
        List<String> spotOrderIds = new ArrayList<>();
        for (TradeSpotMarginMiddleOrder middleOrder: middleOrders) {
            if (Objects.equals(middleOrder.getType(), MiddleOrderType.MARGIN.name())) {
                marginOrderIds.add(middleOrder.getOrderId());
            }else {
                spotOrderIds.add(middleOrder.getOrderId());
            }
        }

        HashMap<String, TradeMarginOrder> marginOrderMap = new HashMap<>();
        if (!ListUtil.isEmpty(marginOrderIds)) {
            TradeMarginOrderExample marginOrderExample = new TradeMarginOrderExample();
            TradeMarginOrderExample.Criteria marginOrderCriteria = marginOrderExample.createCriteria();
            marginOrderCriteria.andUuidIn(marginOrderIds);
            List<TradeMarginOrder> marginOrders = defaultTradeMarginOrderMapper.selectByExample(marginOrderExample);
            for (TradeMarginOrder order: marginOrders) {
                marginOrderMap.put(order.getUuid(), order);
            }
        }

        HashMap<String, TradeSpotOrder> spotOrderMap = new HashMap<>();
        if (!ListUtil.isEmpty(spotOrderIds)) {
            TradeSpotOrderExample spotOrderExample = new TradeSpotOrderExample();
            TradeSpotOrderExample.Criteria spotOrderCriteria = spotOrderExample.createCriteria();
            spotOrderCriteria.andUuidIn(spotOrderIds);
            List<TradeSpotOrder> spotOrders = defaultTradeSpotOrderMapper.selectByExample(spotOrderExample);
            for (TradeSpotOrder order : spotOrders) {
                spotOrderMap.put(order.getUuid(), order);
            }
        }

        List<OrderInfoRes> infoRes = new ArrayList<>();
        for (TradeSpotMarginMiddleOrder middleOrder: middleOrders) {
            if (Objects.equals(middleOrder.getType(), MiddleOrderType.MARGIN.name())) {
                TradeMarginOrder order = marginOrderMap.get(middleOrder.getOrderId());
                OrderInfoRes orderInfo = webMapStruct.tradeMarginOrder2OrderRes(order);
                infoRes.add(orderInfo);
            }
            else {
                TradeSpotOrder order = spotOrderMap.get(middleOrder.getOrderId());
                OrderInfoRes orderInfo = webMapStruct.tradeSpotOrder2OrderRes(order);
                infoRes.add(orderInfo);
            }
        }
        PageInfo<TradeSpotMarginMiddleOrder> pageInfo = new PageInfo<>(middleOrders);
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), infoRes);
    }

    @Override
    public PageResult<TransactionInfoRes> transaction(TransactionReq req, String uid) {
        PageHelper.startPage(req.getPage(), req.getPageSize(), true);
        TradeTransactionExample example = new TradeTransactionExample();
        TradeTransactionExample.Criteria criteria = example.createCriteria();
        //查询有效订单
        criteria.andPdtStatusEqualTo(PdtStatus.COMPLETED.name());
        example.setOrderByClause("CTIME DESC");
        criteria.andUidEqualTo(uid);
        //web端不展示SWAP
        criteria.andTypeNotEqualTo(TransactionType.SWAP.getName());
        if (req.getSymbol() != null) {
            criteria.andSymbolEqualTo(req.getSymbol());
        }
        if (StringUtils.isNotEmpty(req.getDirection())) {
            criteria.andDirectionEqualTo(req.getDirection());
        }
        if (req.getStartTime() != null) {
            Date startTime = CommonUtils.getNowTime(req.getStartTime());
            criteria.andCtimeGreaterThan(startTime);
        }
        if (req.getEndTime() != null) {
            Date endTime = CommonUtils.getNowTime(req.getEndTime());
            criteria.andCtimeLessThan(endTime);
        }
        List<TradeTransaction> transactions = defaultTradeTransactionMapper.selectByExample(example);
        PageInfo<TradeTransaction> pageInfo = new PageInfo<>(transactions);
        List<TransactionInfoRes> infoRes = webMapStruct.tradeTransactions2TransactionRes(pageInfo.getList());
        return PageResult.generate(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), infoRes);
    }
}
