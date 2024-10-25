package com.google.backend.trading.migrate;

import com.google.backend.trading.trace.annotation.TraceId;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author trading
 * @date 2021/11/17 16:28
 */
@Slf4j
@Profile({"disuse"})
@Component
public class MigrateTask {

	@Autowired(required = false)
	private AppMigrateHandle appMigrateHandle;

	@TraceId
	@XxlJob("migrateTask")
	public void migrateTask() {
		log.info("start migrate ----------------------------------");
		try {
			appMigrateHandle.migrateContractTransaction(AppMigrateHandle.START, AppMigrateHandle.END);
			appMigrateHandle.migrateSwapTransaction(AppMigrateHandle.START, AppMigrateHandle.END);
			appMigrateHandle.migrateFundingCost(AppMigrateHandle.START, AppMigrateHandle.END);
			appMigrateHandle.migrateSwapOrder(AppMigrateHandle.START, AppMigrateHandle.END);
			appMigrateHandle.migrateSpotOrder();
			appMigrateHandle.migrateContractOrder();
			appMigrateHandle.migratePosition();
		} catch (Exception e) {
			log.error("migrate err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
			return;
		}
		log.info("finish migrate ----------------------------------");
	}

	@TraceId
	@XxlJob("migrateClearRedis")
	public void migrateClearRedis() {
		appMigrateHandle.clearRedis();
	}


	@TraceId
	@XxlJob("migrateSecondTask")
	public void migrateSecondTask() {
		log.info("start migrate ----------------------------------");
		try {
			appMigrateHandle.migrateWatchList();
			appMigrateHandle.migrateActiveSpotOrder();
			appMigrateHandle.migrateActiveMarginOrder();
			appMigrateHandle.migrateFundingCost(AppMigrateHandle.END, AppMigrateHandle.FINAL);
			appMigrateHandle.migrateContractTransaction(AppMigrateHandle.END, AppMigrateHandle.FINAL);
			appMigrateHandle.migrateSwapTransaction(AppMigrateHandle.END, AppMigrateHandle.FINAL);
			appMigrateHandle.migrateSwapOrder(AppMigrateHandle.END, AppMigrateHandle.FINAL);
		} catch (Exception e) {
			log.error("migrateSecondTask err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
		log.info("finish migrate ----------------------------------");
	}

	@TraceId
	@XxlJob("migrateFinalTask")
	public void migrateFinalTask() {
		log.info("start migrate ----------------------------------");
		try {
			appMigrateHandle.migrateActivePosition();
		} catch (Exception e) {
			log.error("migrateFinalTask err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
		log.info("finish migrate ----------------------------------");
	}

	@TraceId
	@XxlJob("migrateSettleAsset")
	public void migrateSettleAsset() {
		log.info("start migrate ----------------------------------");
		try {
			appMigrateHandle.migrateSettleAsset();
		} catch (Exception e) {
			log.error("migrateSettleAsset err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
		log.info("finish migrate ----------------------------------");
	}

	@TraceId
	@XxlJob("rewriteProPositionPnl")
	public void rewriteProPositionPnl() {
		log.info("start rewrite ----------------------------------");
		try {
			appMigrateHandle.reCalProPositionPnl();
		} catch (Exception e) {
			log.error("rewriteProPositionPnl err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
		log.info("finish rewrite ----------------------------------");
	}

	@TraceId
	@XxlJob("ping")
	public void ping() {
		try {
			appMigrateHandle.ping();
		} catch (Exception e) {
			log.error("ping fail, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
	}
}
