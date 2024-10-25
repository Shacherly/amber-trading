package com.google.backend.trading.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.backend.trading.config.TradeProperties;
import com.google.backend.trading.trace.TraceUtil;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import javax.annotation.PreDestroy;

/**
 * 抽象 loop 线程
 *
 * @author trading
 * @date 2021/10/19 21:04
 */
@Slf4j
public abstract class AbstractLoopThread implements Runnable, ApplicationListener<ApplicationReadyEvent> {

	private final long warnCostTimeMs;

	private final Thread thread;

	@Autowired
	private TradeProperties properties;

	@Autowired
	private RedissonClient redissonClient;

	private final String realLoopName;

	public AbstractLoopThread(long warnCostTimeMs) {
		this.warnCostTimeMs = warnCostTimeMs;
		this.realLoopName = this.getClass().getSimpleName();
		this.thread = new ThreadFactoryBuilder()
				.setDaemon(false)
				.setNameFormat(realLoopName + "-thread-%d")
				.setUncaughtExceptionHandler((t, e) -> log.error("{} thread error, thread = {}, cause = {}",
						realLoopName, t,
						ExceptionUtils.getRootCause(e), e))
				.build().newThread(this);
	}

	@PreDestroy
	public void preDestroy() {
		log.warn("{} thread stopping ...", realLoopName);
		thread.interrupt();
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		String loopStart = System.getProperty("trading.loop.start", "true");
		if (Boolean.parseBoolean(loopStart) && !thread.isAlive()) {
			log.info("{} thread start", realLoopName);
			thread.start();
		}
	}

	@Override
	public void run() {
		RLock lock = redissonClient.getLock(realLoopName);
		//宕机时才会释放锁，用于多实例保证只有一个实例的loop执行
		lock.lock();
		log.info("{} loop lock obtain success, ready handle", lock.getName());
		do {
			if (isEnabled()) {
				long start = System.currentTimeMillis();
				TraceUtil.startTrace();
				try {
					handle();
				} catch (Throwable e) {
					if (e instanceof InterruptedException) {
						Thread.currentThread().interrupt();
					}
					AlarmLogUtil.alarm("{} loop handle cause = {}", realLoopName, ExceptionUtils.getRootCause(e), e);
				} finally {
					long cost = System.currentTimeMillis() - start;
					if (cost > warnCostTimeMs) {
						AlarmLogUtil.alarm("{} loop handle cost = {} ms", realLoopName, cost);
					} else {
						log.info("{} loop handle cost = {} ms", realLoopName, cost);
					}
					TraceUtil.endTrace();
				}
			}
		} while (!Thread.currentThread().isInterrupted());
	}

	private boolean isEnabled() {
		if (!properties.getLoop().isEnabled()) {
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return false;
		}
		return true;
	}

	/**
	 * 业务处理逻辑
	 */
	public abstract void handle() throws Throwable;
}
