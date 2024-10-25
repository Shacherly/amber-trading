package com.google.backend.trading.model.trade;

import org.springframework.util.StringUtils;

/**
 * 优先币种
 * @author adam.wang
 * @date 2021/10/10 18:03
 */
public enum PriorityCoinWithUSD {

    /**
     * USD最优先
     */
    USD("USD",0),
    /**
     * USDT第二
     */
    USDT("USDT",1),
    /**
     * BTC第三
     */
    BTC("BTC",2),
    /**
     * ETH第四
     */
    ETH("ETH",3),
    ;

    /**
     * 币种
     */
    private final String coin;

    /**
     * 优先级，越小越优先
     */
    private final int priority;


    PriorityCoinWithUSD(String coin, int priority) {
        this.coin=coin;
        this.priority=priority;
    }

    /**
     * 判断币种是否属于优先币种
     * @param coin
     * @return
     */
    public static Boolean inPriorityCoin(String coin){
        for(PriorityCoinWithUSD p: PriorityCoinWithUSD.values()){
            if(p.coin.equals(coin)){
                return true;
            }
        }
        return false;
    }

    public static int getPriorityByCoin(String coin){
        for(PriorityCoinWithUSD p: PriorityCoinWithUSD.values()){
            if(p.coin.equals(coin)){
                return p.priority;
            }
        }
        return 9999;
    }
    /**
     * if coin1 优先 coin2 return >=1
     * if coin2 优先 coin1 return <=-1
     * if coin1 = coin2 return 0
     * @param coin1
     * @param coin2
     * @return
     */
    public static int coinCompare(String coin1,String coin2){
        if(StringUtils.isEmpty(coin1)||StringUtils.isEmpty(coin2)){
            throw new RuntimeException("coin must not be null");
        }
        if(coin1.equalsIgnoreCase(coin2)){
            return 0;
        }
        Boolean coin1Boolean = inPriorityCoin(coin1);
        Boolean coin2Boolean = inPriorityCoin(coin2);
        if(coin1Boolean&&coin2Boolean){
            return getPriorityByCoin(coin1)-getPriorityByCoin(coin2);
        }else if(coin1Boolean){
            return -1;
        }else if(coin2Boolean){
            return 1;
        }else{
            return coin1.compareTo(coin2);
        }

    }


}
