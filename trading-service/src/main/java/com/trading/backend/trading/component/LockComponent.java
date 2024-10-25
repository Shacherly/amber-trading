package com.google.backend.trading.component;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author trading
 * @date 2021/12/15 13:18
 */
@Slf4j
@Component
public class LockComponent {

	private final RedissonClient redissonClient;

	public LockComponent(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}


	public boolean lockPosition(String uid, String symbol) {
		RLock lock = redissonClient.getLock(uid + ":handle-position:" + symbol);
		try {
			return lock.tryLock(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return false;
		}
	}

	public void unlockPosition(String uid, String symbol) {
		RLock lock = redissonClient.getLock(uid + ":handle-position:" + symbol);
		lock.unlock();
	}
}
