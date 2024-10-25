package com.google.backend.trading.service.impl;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeUserTradeSettingMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.dao.model.TradeUserTradeSettingExample;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.push.usertouch.ChannelType;
import com.google.backend.trading.push.usertouch.ParamsKey;
import com.google.backend.trading.push.usertouch.ScenarioType;
import com.google.backend.trading.push.usertouch.TemplateCode;
import com.google.backend.trading.push.usertouch.UserTouchMessage;
import com.google.backend.trading.service.PushMsgService;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 推送消息的实现
 *
 * @author savion.chen
 * @date 2021/11/13 10:33
 */
@Slf4j
@Service
public class PushMsgServiceImpl implements PushMsgService {

    @Autowired
    private PushComponent pushComponent;
    @Autowired
    private DefaultTradeUserTradeSettingMapper userSettingMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 换行暂用html标签，直到aceup支持换行解析
     */
    public final String newline = "<br>";

    //-------------通用消息推送处理------------

    @Override
    public void submitOrderOk(String userId, boolean isSpot) {
//        try {
//            List<ChannelType> channels = new ArrayList<ChannelType>();
//            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
//            channels.add(ChannelType.TOAST);
//            if (isSpot) {
//                msgCodes.add(TemplateCode.SPOT_SUBMIT_TOAST);
//            } else {
//                msgCodes.add(TemplateCode.MARGIN_SUBMIT_TOAST);
//            }
//            UserTouchMessage msg = new UserTouchMessage(userId,
//                    ScenarioType.ENTRUST_SUBMIT, channels, msgCodes, null);
//            pushComponent.pushClientMessage(msg);
//        } catch (Exception e) {
//            log.error("submitOrderOk={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
//        }
    }

    @Override
    public void cancelOrderOk(String userId, boolean isSpot) {
//        try {
//            List<ChannelType> channels = new ArrayList<ChannelType>();
//            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
//            channels.add(ChannelType.TOAST);
//            if (isSpot) {
//                msgCodes.add(TemplateCode.SPOT_CANCEL_TOAST);
//            } else {
//                msgCodes.add(TemplateCode.MARGIN_CANCEL_TOAST);
//            }
//            UserTouchMessage msg = new UserTouchMessage(userId,
//                    ScenarioType.ENTRUST_CANCEL, channels, msgCodes, null);
//            pushComponent.pushClientMessage(msg);
//        } catch (Exception e) {
//            log.error("cancelOrderOk={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
//        }
    }

    @Override
    public void modifyOrderOk(String userId, boolean isSpot) {
//        try {
//            List<ChannelType> channels = new ArrayList<ChannelType>();
//            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
//            channels.add(ChannelType.TOAST);
//            if (isSpot) {
//                msgCodes.add(TemplateCode.SPOT_MODIFY_TOAST);
//            } else {
//                msgCodes.add(TemplateCode.MARGIN_MODIFY_TOAST);
//            }
//            UserTouchMessage msg = new UserTouchMessage(userId,
//                    ScenarioType.ENTRUST_MODIFY, channels, msgCodes, null);
//            pushComponent.pushClientMessage(msg);
//        } catch (Exception e) {
//            log.error("modifyOrderOk={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
//        }
    }

    //-------------现货的消息推送处理------------

    @Override
    public void spotTriggerOk(TradeSpotOrder order) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            if (this.isSendEmail(order.getUid())) {
                channels.add(ChannelType.EMAIL);
                msgCodes.add(TemplateCode.SPOT_TRIGGER_EMAIL);
            }
            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            msgCodes.add(TemplateCode.SPOT_TRIGGER_TOAST);
            msgCodes.add(TemplateCode.SPOT_TRIGGER_PUSH);

            Map<String, String> params = new TreeMap<String, String>();
            params.put(ParamsKey.CURRENCY_PAIRS.getName(), order.getSymbol());
            BigDecimal price = CommonUtils.roundPrice(order.getTriggerPrice(), order.getSymbol());
            params.put(ParamsKey.TRIGGER_PRICE.getName(), NumberUtils.thousandthComma(price));

            UserTouchMessage msg = new UserTouchMessage(order.getUid(),
                    ScenarioType.ENTRUST_TRIGGER, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("spotTriggerOk={} error={}", order.getUid(), ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void spotOrderTraded(TradeSpotOrder order) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            boolean isBuy = Direction.isBuy(order.getDirection());
            if (this.isSendEmail(order.getUid())) {
                channels.add(ChannelType.EMAIL);
                if (isBuy) {
                    msgCodes.add(TemplateCode.SPOT_BUY_EMAIL);
                } else {
                    msgCodes.add(TemplateCode.SPOT_SELL_EMAIL);
                }
            }

            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            String targetCoin;
            if (isBuy) {
                msgCodes.add(TemplateCode.SPOT_BUY_TOAST);
                msgCodes.add(TemplateCode.SPOT_BUY_PUSH);
                targetCoin = CommonUtils.getObtainedCoin(order.getSymbol(), order.getDirection());
            } else {
                msgCodes.add(TemplateCode.SPOT_SELL_TOAST);
                msgCodes.add(TemplateCode.SPOT_SELL_PUSH);
                targetCoin = CommonUtils.getPaymentCoin(order.getSymbol(), order.getDirection());
            }

            Map<String, String> params = new TreeMap<String, String>();
            params.put(ParamsKey.CURRENCY_PAIRS.getName(), order.getSymbol());
            BigDecimal fillPrice = CommonUtils.roundPrice(order.getFilledPrice(), order.getSymbol());
            params.put(ParamsKey.FILLED_PRICE.getName(), NumberUtils.thousandthComma(fillPrice));
            BigDecimal fillQty;
            if (order.getIsQuote()) {
                fillQty = CommonUtils.roundAmount(order.getAmountFilled(), targetCoin);
            } else {
                fillQty = CommonUtils.roundAmount(order.getQuantityFilled(), targetCoin);
            }
            params.put(ParamsKey.AMOUNT.getName(), NumberUtils.thousandthComma(fillQty));
            params.put(ParamsKey.CRYPTOCURRENCY.getName(), targetCoin);

            UserTouchMessage msg = new UserTouchMessage(order.getUid(),
                    ScenarioType.ENTRUST_TRADED, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("spotOrderTraded={} error={}", order.getUid(), ExceptionUtils.getRootCauseMessage(e), e);
        }
    }


    //-------------杠杆的消息推送处理------------

    @Override
    public void marginTriggerOk(TradeMarginOrder order) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            if (this.isSendEmail(order.getUid())) {
                channels.add(ChannelType.EMAIL);
                msgCodes.add(TemplateCode.MARGIN_TRIGGER_EMAIL);
            }

            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            msgCodes.add(TemplateCode.MARGIN_TRIGGER_TOAST);
            msgCodes.add(TemplateCode.MARGIN_TRIGGER_PUSH);
            Map<String, String> params = new TreeMap<String, String>();
            params.put(ParamsKey.CURRENCY_PAIRS.getName(), order.getSymbol());
            BigDecimal price = CommonUtils.roundPrice(order.getTriggerPrice(), order.getSymbol());
            params.put(ParamsKey.TRIGGER_PRICE.getName(), NumberUtils.thousandthComma(price));

            UserTouchMessage msg = new UserTouchMessage(order.getUid(),
                    ScenarioType.ENTRUST_TRIGGER, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginTriggerOk={} error={}", order.getUid(), ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void marginOrderTraded(TradeTransaction transaction) {
        String uid = transaction.getUid();
        try {
            List<ChannelType> channels = new ArrayList<>();
            List<TemplateCode> msgCodes = new ArrayList<>();
            boolean isBuy = Direction.isBuy(transaction.getDirection());
            if (this.isSendEmail(uid)) {
                channels.add(ChannelType.EMAIL);
                if (isBuy) {
                    msgCodes.add(TemplateCode.MARGIN_LONG_EMAIL);
                } else {
                    msgCodes.add(TemplateCode.MARGIN_SHORT_EMAIL);
                }
            }

            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            String targetCoin;
            if (isBuy) {
                msgCodes.add(TemplateCode.MARGIN_LONG_TOAST);
                msgCodes.add(TemplateCode.MARGIN_LONG_PUSH);
                targetCoin = CommonUtils.getObtainedCoin(transaction.getSymbol(), transaction.getDirection());
            } else {
                msgCodes.add(TemplateCode.MARGIN_SHORT_TOAST);
                msgCodes.add(TemplateCode.MARGIN_SHORT_PUSH);
                targetCoin = CommonUtils.getPaymentCoin(transaction.getSymbol(), transaction.getDirection());
            }

            Map<String, String> params = new TreeMap<>();
            params.put(ParamsKey.CURRENCY_PAIRS.getName(), transaction.getSymbol());
            BigDecimal fillPrice = CommonUtils.roundPrice(transaction.getPrice(), transaction.getSymbol());
            params.put(ParamsKey.FILLED_PRICE.getName(), NumberUtils.thousandthComma(fillPrice));
            BigDecimal fillQty = CommonUtils.roundAmount(transaction.getBaseQuantity(), targetCoin);
            params.put(ParamsKey.AMOUNT.getName(), NumberUtils.thousandthComma(fillQty));
            params.put(ParamsKey.CRYPTOCURRENCY.getName(), targetCoin);

            UserTouchMessage msg = new UserTouchMessage(uid,
                    ScenarioType.ENTRUST_TRADED, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginOrderTraded={} error={}", uid, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }


    @Override
    public void marginStopSinglePosition(TradePosition pos, boolean isProfit, BigDecimal pnl, BigDecimal amount) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            if (this.isSendEmail(pos.getUid())) {
                channels.add(ChannelType.EMAIL);
                if (isProfit) {
                    msgCodes.add(TemplateCode.STOP_PROFIT_EMAIL);
                } else {
                    msgCodes.add(TemplateCode.STOP_LOSS_EMAIL);
                }
            }

            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            channels.add(ChannelType.SMS);
            channels.add(ChannelType.IN_MAIL);
            if (isProfit) {
                msgCodes.add(TemplateCode.STOP_PROFIT_TOAST);
                msgCodes.add(TemplateCode.STOP_PROFIT_PUSH);
                msgCodes.add(TemplateCode.STOP_PROFIT_SMS);
                msgCodes.add(TemplateCode.STOP_PROFIT_IN_MAIL);
            } else {
                msgCodes.add(TemplateCode.STOP_LOSS_TOAST);
                msgCodes.add(TemplateCode.STOP_LOSS_PUSH);
                msgCodes.add(TemplateCode.STOP_LOSS_SMS);
                msgCodes.add(TemplateCode.STOP_LOSS_IN_MAIL);
            }

            Map<String, String> params = new TreeMap<String, String>();
            params.put(ParamsKey.CURRENCY_PAIRS.getName(), pos.getSymbol());
            BigDecimal triggerPrice;
            if (isProfit) {
                triggerPrice = CommonUtils.roundPrice(pos.getTakeProfitPrice(), pos.getSymbol());
            } else {
                triggerPrice = CommonUtils.roundPrice(pos.getStopLossPrice(), pos.getSymbol());
            }
            params.put(ParamsKey.TRIGGER_PRICE.getName(), NumberUtils.thousandthComma(triggerPrice));
            BigDecimal roundAmount = CommonUtils.roundAmount(amount, Constants.DEFAULT_COIN);
            params.put(ParamsKey.AMOUNT.getName(), NumberUtils.thousandthComma(roundAmount));
            BigDecimal roundPnl = CommonUtils.roundAmount(pnl, Constants.DEFAULT_COIN);
            params.put(ParamsKey.REALIZED_PNL.getName(), NumberUtils.thousandthComma(roundPnl));
            params.put(ParamsKey.CRYPTOCURRENCY.getName(), Constants.DEFAULT_COIN);

            UserTouchMessage msg = new UserTouchMessage(pos.getUid(),
                    ScenarioType.STOP_SINGLE_POS, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginStopSinglePosition={} error={}", pos.getUid(), ExceptionUtils.getRootCauseMessage(e), e);
        }
    }


    @Override
    public void marginStopCrossedPosition(String userId, BigDecimal pnl) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            boolean isProfit = CommonUtils.isPositive(pnl);
            if (this.isSendEmail(userId)) {
                channels.add(ChannelType.EMAIL);
                if (isProfit) {
                    msgCodes.add(TemplateCode.STOP_PROFIT_DONE_EMAIL);
                } else {
                    msgCodes.add(TemplateCode.STOP_LOSS_DONE_EMAIL);
                }
            }

            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            channels.add(ChannelType.SMS);
            channels.add(ChannelType.IN_MAIL);
            if (isProfit) {
                msgCodes.add(TemplateCode.STOP_PROFIT_DONE_TOAST);
                msgCodes.add(TemplateCode.STOP_PROFIT_DONE_PUSH);
                msgCodes.add(TemplateCode.STOP_PROFIT_DONE_SMS);
                msgCodes.add(TemplateCode.STOP_PROFIT_DONE_IN_MAIL);
            } else {
                msgCodes.add(TemplateCode.STOP_LOSS_DONE_TOAST);
                msgCodes.add(TemplateCode.STOP_LOSS_DONE_PUSH);
                msgCodes.add(TemplateCode.STOP_LOSS_DONE_SMS);
                msgCodes.add(TemplateCode.STOP_LOSS_DONE_IN_MAIL);
            }

            Map<String, String> params = new TreeMap<String, String>();
            BigDecimal roundPnl = CommonUtils.roundAmount(pnl, Constants.BASE_COIN);
            params.put(ParamsKey.REALIZED_PNL.getName(), NumberUtils.thousandthComma(roundPnl));
            params.put(ParamsKey.CRYPTOCURRENCY.getName(), Constants.DEFAULT_COIN);

            UserTouchMessage msg = new UserTouchMessage(userId,
                    ScenarioType.STOP_CROSSED_POS, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginStopCrossedPosition={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }


    @Override
    public void marginSettleDone(String userId, BigDecimal cost) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            channels.add(ChannelType.EMAIL);
            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            msgCodes.add(TemplateCode.SETTLE_DONE_EMAIL);
            msgCodes.add(TemplateCode.SETTLE_DONE_TOAST);
            msgCodes.add(TemplateCode.SETTLE_DONE_PUSH);

            Map<String, String> params = new TreeMap<String, String>();
            BigDecimal roundPnl = CommonUtils.roundAmount(cost, Constants.BASE_COIN);
            params.put(ParamsKey.FUNDING_FEE.getName(), NumberUtils.thousandthComma(roundPnl));
            params.put(ParamsKey.CRYPTOCURRENCY.getName(), Constants.DEFAULT_COIN);

            UserTouchMessage msg = new UserTouchMessage(userId,
                    ScenarioType.SETTLE, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginSettleDone={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void marginDelivery(String userId, boolean isOk) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            channels.add(ChannelType.TOAST);
            if (isOk) {
                msgCodes.add(TemplateCode.DELIVERY_OK_TOAST);
            } else {
                msgCodes.add(TemplateCode.DELIVERY_FAIL_TOAST);
            }

            UserTouchMessage msg = new UserTouchMessage(userId,
                    ScenarioType.DELIVERY, channels, msgCodes, null);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginDelivery={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void marginAutoDelivery(String userId) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();

            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);

            msgCodes.add(TemplateCode.AUTO_DELIVERY_TOAST);
            msgCodes.add(TemplateCode.AUTO_DELIVERY_PUSH);

            UserTouchMessage msg = new UserTouchMessage(userId,
                    ScenarioType.DELIVERY, channels, msgCodes, null);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginAutoDelivery={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void positionAutoSettleSuccess(String userId, List<String> successSymbols) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            if (this.isSendEmail(userId)) {
                channels.add(ChannelType.EMAIL);
                msgCodes.add(TemplateCode.AUTO_DELIVERY_SUCCESS_EMAIL);
            }

            Map<String, String> params = new TreeMap<String, String>();
            params.put(ParamsKey.CURRENCY_PAIRS_SUCCEED.getName(), StringUtils.collectionToDelimitedString(successSymbols, newline));

            UserTouchMessage msg = new UserTouchMessage(userId,
                    ScenarioType.DELIVERY, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("positionAutoSettleSuccess={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void positionAutoSettleFail(String userId, List<String> failSymbols) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            if (this.isSendEmail(userId)) {
                channels.add(ChannelType.EMAIL);
                msgCodes.add(TemplateCode.AUTO_DELIVERY_FAIL_EMAIL);
            }

            Map<String, String> params = new TreeMap<String, String>();
            params.put(ParamsKey.CURRENCY_PAIRS_FAILED.getName(), StringUtils.collectionToDelimitedString(failSymbols, newline));

            UserTouchMessage msg = new UserTouchMessage(userId,
                    ScenarioType.DELIVERY, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("positionAutoSettleFail={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void marginNotEnough(TradeMarginOrder order) {
        String uid = order.getUid();
        ScenarioType type = ScenarioType.NO_MARGIN;
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            if (Direction.isBuy(order.getDirection())) {
                msgCodes.add(TemplateCode.NOT_MARGIN_BUY_TOAST);
                msgCodes.add(TemplateCode.NOT_MARGIN_BUY_PUSH);
            } else {
                msgCodes.add(TemplateCode.NOT_MARGIN_SELL_TOAST);
                msgCodes.add(TemplateCode.NOT_MARGIN_SELL_PUSH);
            }

            Map<String, String> params = new TreeMap<String, String>();
            params.put(ParamsKey.CURRENCY_PAIRS.getName(), order.getSymbol());
            UserTouchMessage msg = new UserTouchMessage(uid,
                    type, channels, msgCodes, params);
            if (checkFrequency(uid, type, order.getSymbol())) {
                pushComponent.pushClientMessage(msg);
            }
        } catch (Exception e) {
            log.error("marginNotEnough={} error={}", uid, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void marginForceClose(String userId) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            channels.add(ChannelType.EMAIL);
            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            channels.add(ChannelType.SMS);
            channels.add(ChannelType.IN_MAIL);

            msgCodes.add(TemplateCode.FORCE_CLOSE_EMAIL);
            msgCodes.add(TemplateCode.FORCE_CLOSE_TOAST);
            msgCodes.add(TemplateCode.FORCE_CLOSE_PUSH);
            msgCodes.add(TemplateCode.FORCE_CLOSE_SMS);
            msgCodes.add(TemplateCode.FORCE_CLOSE_IN_MAIL);

            UserTouchMessage msg = new UserTouchMessage(userId,
                    ScenarioType.FORCE_CLOSE, channels, msgCodes, null);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginForceClose={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void marginPartForceClose(String userId) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            channels.add(ChannelType.EMAIL);
            channels.add(ChannelType.TOAST);
            channels.add(ChannelType.AURORA);
            channels.add(ChannelType.SMS);
            channels.add(ChannelType.IN_MAIL);

            msgCodes.add(TemplateCode.PART_FORCE_CLOSE_EMAIL);
            msgCodes.add(TemplateCode.PART_FORCE_CLOSE_TOAST);
            msgCodes.add(TemplateCode.PART_FORCE_CLOSE_PUSH);
            msgCodes.add(TemplateCode.PART_FORCE_CLOSE_SMS);
            msgCodes.add(TemplateCode.PART_FORCE_CLOSE_IN_MAIL);

            UserTouchMessage msg = new UserTouchMessage(userId,
                    ScenarioType.PART_FORCE_CLOSE, channels, msgCodes, null);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("marginPartForceClose={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void alarmPriceToUser(String userId, String coin, BigDecimal price) {
        try {
            List<ChannelType> channels = new ArrayList<ChannelType>();
            List<TemplateCode> msgCodes = new ArrayList<TemplateCode>();
            channels.add(ChannelType.AURORA);
            msgCodes.add(TemplateCode.ALARM_PRICE_PUSH);
            Map<String, String> params = new TreeMap<String, String>();
            params.put("currency", coin);
            params.put("trigger_price", NumberUtils.thousandthComma(price));
            UserTouchMessage msg = new UserTouchMessage(userId,
                    null, channels, msgCodes, params);
            pushComponent.pushClientMessage(msg);
        } catch (Exception e) {
            log.error("alarmPriceToUser={} error={}", userId, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }


    private boolean isSendEmail(String userId) {
        TradeUserTradeSettingExample example = new TradeUserTradeSettingExample();
        example.createCriteria().andUidEqualTo(userId);
        List<TradeUserTradeSetting> settingList = userSettingMapper.selectByExample(example);
        if (!settingList.isEmpty()) {
            TradeUserTradeSetting setting = settingList.get(0);
            return setting.getEmailNotification();
        }
        return true;
    }

    public static final String DEFAULT_VALUE = "1";

    private boolean checkFrequency(String uid, ScenarioType type, String symbol) {
        String prefix = "user-touch";
        switch (type) {
            case NO_MARGIN:
                Boolean success = redisTemplate.opsForValue().setIfAbsent(prefix + ":" + uid + ":" + symbol + ":" + type.name(),
                        DEFAULT_VALUE,
                        Duration.ofHours(24));
                return Boolean.TRUE.equals(success);
            default:
                return false;
        }
    }

}
