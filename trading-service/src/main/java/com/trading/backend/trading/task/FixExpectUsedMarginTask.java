package com.google.backend.trading.task;

import com.google.backend.trading.trace.annotation.TraceId;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author trading
 * @date 2021/12/23 17:58
 */
@Slf4j
@Component
public class FixExpectUsedMarginTask {

	@Autowired
	private StringRedisTemplate redisTemplate;

	private Map<String, Long> fixExpectUsedMarginMap = new HashMap<>();

	@TraceId
	@XxlJob("analyzeExpectUsedMargin")
	public void analyzeExpectUsedMargin() {
		Long dbSize = redisTemplate.execute(RedisServerCommands::dbSize);
		log.info("db size = {}", dbSize);
		String matchKey = "expect-used-margin";
		Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
			Set<String> keysTmp = new HashSet<>();
			Cursor<byte[]> cursor =
					connection.scan(new ScanOptions.ScanOptionsBuilder().match("*" + matchKey + "*").count(1000).build());
			while (cursor.hasNext()) {
				String key = new String(cursor.next());
				keysTmp.add(key);
			}
			return keysTmp;
		});
		if (null == keys) {
			log.info("keys is null");
			return;
		}
		log.info("keys size = {}, keys = {}", keys.size(), keys);
		ArrayList<String> keysList = new ArrayList<>(keys);
		List<String> values = redisTemplate.opsForValue().multiGet(keysList);
		if (null == values) {
			log.info("values is null");
			return;
		}
		Map<String, Long> map = new HashMap<>();
		for (int i = 0; i < keysList.size(); i++) {
			String key = keysList.get(i);
			String value = values.get(i);
			map.put(key, Long.valueOf(value));
		}
		fixExpectUsedMarginMap = map.entrySet().stream().filter(e -> e.getValue() != 0).collect(Collectors.toMap(Map.Entry::getKey,
				Map.Entry::getValue));
		log.warn("fixExpectUsedMarginMap size = {}, data = {}",  fixExpectUsedMarginMap.size(), fixExpectUsedMarginMap);
	}

	@TraceId
	@XxlJob("fixExpectUsedMargin")
	public void fixExpectUsedMargin() {
		log.warn("fixExpectUsedMarginMap size = {}, data = {}",  fixExpectUsedMarginMap.size(), fixExpectUsedMarginMap);
		Long delete = redisTemplate.delete(fixExpectUsedMarginMap.keySet());
		log.warn("delete keys size = {}, keys = {}", delete, fixExpectUsedMarginMap.keySet());
	}
}
