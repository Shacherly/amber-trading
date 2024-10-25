package com.google.backend.trading.push;

import lombok.Data;

import java.util.List;

/**
 * @author trading
 * @date 2021/10/23 15:10
 */
@Data
public class WsPushMessage<T> {

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 消费者，值：web、app、api，不指定表示所有
	 */
	private List<String> consumers;

	/**
	 * 主动推送，如 order，asset，需要登记到event文档中
	 */
	private String event;

	/**
	 * 推送时间，毫秒数
	 */
	private Long ts;

	/**
	 *  消息内容，websocket会原样推给前端
	 */
	private T payload;

	public static <T> WsPushMessage<T> buildAllConsumersMessage(String uid, PushEventEnum eventEnum, T payload) {
		WsPushMessage<T> message = new WsPushMessage<>();
		message.setUserId(uid);
		message.setConsumers(null);
		message.setEvent(eventEnum.getCode());
		message.setTs(System.currentTimeMillis());
		message.setPayload(payload);
		return message;
	}


}
