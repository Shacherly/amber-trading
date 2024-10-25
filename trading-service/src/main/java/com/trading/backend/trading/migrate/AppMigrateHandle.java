package com.google.backend.trading.migrate;

import com.google.common.collect.ImmutableList;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.config.i18n.I18nEnum;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradePositionMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradePositionExample;
import com.google.backend.trading.dao.model.TradePositionFundingCost;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.model.funding.dto.FundingCostStatus;
import com.google.backend.trading.model.margin.PositionStatus;
import com.google.backend.trading.model.swap.SwapType;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.PdtStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TradeTerminator;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.transaction.MarginTransaction;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author trading
 * @date 2021/11/10 11:12
 */
@Slf4j
@Profile({"disuse"})
@Component
public class AppMigrateHandle {

	@Autowired
	@Qualifier("appMigrateJdbcTemplate")
	private JdbcTemplate appMigrateJdbcTemplate;

	@Autowired
	private JdbcTemplate template;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private DefaultTradePositionMapper tradePositionMapper;

	@Autowired
	private MarginTransaction marginTransaction;

	@Autowired
	private DefaultTradeTransactionMapper tradeTransactionMapper;

	@Autowired
	private AssetRequest assetRequest;

	/**
	 * 2021-06-10 12:00:00 （UTC+8）
	 */
	public static final long START = 0L;
	/**
	 * 2021-12-12 00:00:00 （UTC+8）
	 */
	public static final long END = 1639238400000L;
	/**
	 * 2021-12-14 00:00:00 （UTC+8）
	 */
	public static final long FINAL = 1639411200000L;

	public static final String APP_ID_PREFIX = "APP_";

	public static final String MARGIN_PREFIX = "MARGIN_";

	public static final String SPOT_PREFIX = "SPOT_";

	public static final String MIGRATE_PREFIX = "MIGRATE_";

	public static final String MIGRATE_REDUCE_POSITION_PREFIX = "reduce_position_";

	public static final String HASH_VALUE = "1";

	public static final String ACTIVE_SPOT_ORDERS_KEY = "trading.migrate.skip.spot";
	public static final String ACTIVE_MARGIN_ORDERS_KEY = "trading.migrate.skip.margin";
	public static final String ACTIVE_POSITIONS_KEY = "trading.migrate.skip.positions";

	public void clearRedis() {
		redisTemplate.delete(ImmutableList.of(ACTIVE_SPOT_ORDERS_KEY, ACTIVE_MARGIN_ORDERS_KEY, ACTIVE_POSITIONS_KEY));
	}

	public void ping() {
		List<Object> list = appMigrateJdbcTemplate.query("SELECT id from pf_contract_position limit 1", new RowMapper<Object>() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(rowNum);
			}
		});
		log.info("ping select, result = {}", list);
	}

	// ------------------------------------ 上线前冷数据迁移脚本 ----------------------------------
	//历史transaction contract迁移
	public void migrateContractTransaction(long startTime, long endTime) {
		int limit = 1000;
		int offset = 0;
		boolean quit;
		do {
			quit = batchInsertTransactionAboutContract(limit, offset, startTime, endTime);
			offset += limit;
		} while (!quit);
	}

	//历史transaction swap迁移
	public void migrateSwapTransaction(long startTime, long endTime) {
		int limit = 1000;
		int offset = 0;
		boolean quit;
		do {
			quit = batchInsertTransactionAboutSwap(limit, offset, startTime, endTime);
			offset += limit;
		} while (!quit);
	}

	//历史position_funding_cost迁移
	public void migrateFundingCost(long startTime, long endTime) {
		int limit = 1000;
		int offset = 0;
		boolean quit;
		do {
			quit = batchInsertPositionFundingCost(limit, offset, startTime, endTime);
			offset += limit;
		} while (!quit);
	}

	//历史exchange迁移
	public void migrateSwapOrder(long startTime, long endTime) {
		int limit = 1000;
		int offset = 0;
		boolean quit;
		do {
			quit = batchInsertSwapOrder(limit, offset, startTime, endTime);
			offset += limit;
		} while (!quit);
	}

	//历史swap_order迁移
	public void migrateSpotOrder() {
		int limit = 1000;
		int offset = 0;
		boolean quit;
		do {
			quit = batchInsertSpotOrder(limit, offset);
			offset += limit;
		} while (!quit);
	}

	//历史position迁移
	public void migratePosition() {
		int limit = 1000;
		int offset = 0;
		boolean quit;
		do {
			quit = batchInsertPosition(limit, offset);
			offset += limit;
		} while (!quit);
	}

	//历史margin_order迁移
	public void migrateContractOrder() {
		int limit = 1000;
		int offset = 0;
		boolean quit;
		do {
			quit = batchInsertMarginOrder(limit, offset);
			offset += limit;
		} while (!quit);
	}

	// ------------------------------------ 上线时停机后迁移脚本 ----------------------------------
	//trade_user_market_favorite 数据量小，全量迁移
	public void migrateWatchList() {
		batchInsertMarketFavorite();
	}

	//活跃现货挂单，需要附带上时间线之前的活跃订单数据
	public void migrateActiveSpotOrder() {
		Map<Object, Object> activeOrderIdMap = redisTemplate.opsForHash().entries(ACTIVE_SPOT_ORDERS_KEY);
		Long[] spotOrderIdArr = activeOrderIdMap.keySet().stream().map(v -> Long.parseLong(v.toString())).toArray(Long[]::new);
		StringBuilder sqlBuilder = new StringBuilder("select * from pf_swap_order where id in (");
		List<TradeSpotOrder> orders = new ArrayList<>();
		if (spotOrderIdArr.length > 0) {
			for (Long s : spotOrderIdArr) {
				sqlBuilder.append("?,");
			}
			sqlBuilder.replace(sqlBuilder.length() - 1, sqlBuilder.length(), ")");
			String sql = sqlBuilder.toString();
			orders = appMigrateJdbcTemplate.query(sql, spotOrderIdArr, spotOrderRowMapper());
		}

		String currentSql = "select * from pf_swap_order where status in (-1, 0, 1, 4, 6) and created_time > ?" +
				" " +
				"order by " +
				"created_time ";

		List<TradeSpotOrder> newOrders = appMigrateJdbcTemplate.query(currentSql, new Object[]{END}, spotOrderRowMapper());
		//所有订单（历史活跃订单和增量订单）
		orders.addAll(newOrders);

		//true 是活跃订单
		Map<Boolean, List<TradeSpotOrder>> map = orders.stream().filter(Objects::nonNull).collect(
				Collectors.groupingBy(tradeSpotOrder -> OrderStatus.EXECUTING.getName().equals(tradeSpotOrder.getStatus()),
						Collectors.toList()));
		orders = null;
		List<TradeSpotOrder> activeOrders = map.getOrDefault(Boolean.TRUE, new ArrayList<>());
		List<TradeSpotOrder> historyOrders = map.getOrDefault(Boolean.FALSE, new ArrayList<>());
		//cancel活跃订单
		List<TradeSpotOrder> canceledOrders = activeOrders.stream().peek(order -> {
			order.setStatus(OrderStatus.CANCELED.getName());
			order.setLockAmount(BigDecimal.ZERO);
			order.setTerminator(TradeTerminator.SYSTEM.getCode());
		}).collect(Collectors.toList());
		historyOrders.addAll(canceledOrders);

		int[] insertRows = insertSpotOrders(historyOrders);
		int size = historyOrders.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("migrateActiveSpotOrder search size = {}, insert size = {}, size not match", size, sum);
		} else {
			log.info("migrateActiveSpotOrder search size = {}, insert size = {}", size, sum);
		}
	}

	//活跃杠杆挂单，需要附带上时间线之前的活跃订单数据
	public void migrateActiveMarginOrder() {
		Map<Object, Object> activeOrderIdMap = redisTemplate.opsForHash().entries(ACTIVE_MARGIN_ORDERS_KEY);
		Long[] marginOrderIdArr = activeOrderIdMap.keySet().stream().map(v -> Long.parseLong(v.toString())).toArray(Long[]::new);

		//统计fee
		StringBuilder feeSumSqlBuilder = new StringBuilder("select order_id, COALESCE(sum(fee), 0) as total_fee from pf_contract_trade " +
				"where status = 3 and  (");
		List<Object> feeSumParams = new ArrayList<>();
		if (marginOrderIdArr.length > 0) {
			feeSumSqlBuilder.append("id in (");
			for (Long s : marginOrderIdArr) {
				feeSumSqlBuilder.append("?,");
			}
			feeSumSqlBuilder.replace(feeSumSqlBuilder.length() - 1, feeSumSqlBuilder.length(), ") or");
			feeSumParams.addAll(Arrays.asList(marginOrderIdArr));
		}
		feeSumSqlBuilder.append(" created_time > ? ) group by order_id");
		feeSumParams.add(END);
		String feeSumSql = feeSumSqlBuilder.toString();

		Map<String, BigDecimal> feeMap = appMigrateJdbcTemplate.query(feeSumSql, feeSumParams.toArray(),
				rs -> {
					Map<String, BigDecimal> map = new HashMap<>();
					while (rs.next()) {
						String orderId = APP_ID_PREFIX + MARGIN_PREFIX +  rs.getString("order_id");
						BigDecimal totalFee = rs.getBigDecimal("total_fee");
						map.put(orderId, totalFee);
					}
					return map;
				});

		StringBuilder sqlBuilder = new StringBuilder("select * from pf_contract_order where id in (");
		List<TradeMarginOrder> orders = new ArrayList<>();
		if (marginOrderIdArr.length > 0) {
			for (Long s : marginOrderIdArr) {
				sqlBuilder.append("?,");
			}
			sqlBuilder.replace(sqlBuilder.length() - 1, sqlBuilder.length(), ")");
			String sql = sqlBuilder.toString();
			orders = appMigrateJdbcTemplate.query(sql, marginOrderIdArr, marginOrderRowMapper(feeMap));
		}

		String currentSql = "select * from pf_contract_order where status in (-1, 0, 1, 4, 6) and created_time > ?" +
				" " +
				"order by " +
				"created_time ";
		List<TradeMarginOrder> newOrders = appMigrateJdbcTemplate.query(currentSql, new Object[]{END}, marginOrderRowMapper(feeMap));

		//所有订单（历史活跃订单和增量订单）
		orders.addAll(newOrders);

		//true 是活跃订单
		Map<Boolean, List<TradeMarginOrder>> map = orders.stream().filter(Objects::nonNull).collect(
				Collectors.groupingBy(order -> OrderStatus.EXECUTING.getName().equals(order.getStatus()), Collectors.toList()));
		orders = null;
		List<TradeMarginOrder> activeOrders = map.getOrDefault(Boolean.TRUE, new ArrayList<>());
		List<TradeMarginOrder> historyOrders = map.getOrDefault(Boolean.FALSE, new ArrayList<>());
		//cancel活跃订单
		List<TradeMarginOrder> canceledOrders = activeOrders.stream().peek(order -> {
			order.setStatus(OrderStatus.CANCELED.getName());
			order.setTerminator(TradeTerminator.SYSTEM.getCode());
		}).collect(Collectors.toList());
		historyOrders.addAll(canceledOrders);

		int[] insertRows = insertMarginOrders(historyOrders);
		int size = historyOrders.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("migrateActiveMarginOrder search size = {}, insert size = {}, size not match", size, sum);
		} else {
			log.info("migrateActiveMarginOrder search size = {}, insert size = {}", size, sum);
		}
	}

	//杠杆持仓，需要附带上时间线之前的活跃仓位，判断状态进行处理
	public void migrateActivePosition() {
		Map<Object, Object> activePositionIdMap = redisTemplate.opsForHash().entries(ACTIVE_POSITIONS_KEY);
		Long[] positionIdArr = activePositionIdMap.keySet().stream().map(v -> Long.parseLong(v.toString())).toArray(Long[]::new);

		StringBuilder sqlBuilder = new StringBuilder("select * from pf_contract_position where id in (");
		List<TradePosition> positions = new ArrayList<>();
		if (positionIdArr.length > 0) {
			for (Long s : positionIdArr) {
				sqlBuilder.append("?,");
			}
			sqlBuilder.replace(sqlBuilder.length() - 1, sqlBuilder.length(), ")");
			String sql = sqlBuilder.toString();
			positions = appMigrateJdbcTemplate.query(sql, positionIdArr, tradePositionRowMapper());
		}

		String currentSql = "select * from pf_contract_position where created_time > ?" +
				" " +
				"order by " +
				"created_time ";
		List<TradePosition> newPositions = appMigrateJdbcTemplate.query(currentSql, new Object[]{END}, tradePositionRowMapper());

		//所有订单（历史活跃订单和增量订单）
		positions.addAll(newPositions);

		//true 是活跃订单
		Map<Boolean, List<TradePosition>> map = positions.stream().filter(Objects::nonNull).collect(
				Collectors.groupingBy(order -> PositionStatus.ACTIVE.name().equals(order.getStatus()), Collectors.toList()));
		positions = null;
		List<TradePosition> activePositions = map.getOrDefault(Boolean.TRUE, new ArrayList<>());
		List<TradePosition> historyPositions = map.getOrDefault(Boolean.FALSE, new ArrayList<>());

		//查询google已存在的活跃仓位
		TradePositionExample example = new TradePositionExample();
		example.createCriteria().andStatusEqualTo(PositionStatus.ACTIVE.name());
		List<TradePosition> existPositions = tradePositionMapper.selectByExample(example);
		//uid -> symbol -> position
		Map<String, Map<String, List<TradePosition>>> existPositionMap =
				existPositions.stream().collect(Collectors.groupingBy(TradePosition::getUid,
						Collectors.groupingBy(TradePosition::getSymbol)));

		Iterator<TradePosition> iterator = activePositions.iterator();
		while (iterator.hasNext()) {
			TradePosition position = iterator.next();
			//仓位合并生成transaction记录，生成pnl调用资金
			String uid = position.getUid();
			String symbol = position.getSymbol();
			List<TradePosition> tradePositions =
					Optional.of(existPositionMap).map(stringMapMap -> stringMapMap.get(uid)).map(stringListMap -> stringListMap.get(symbol)).orElse(new ArrayList<>());
			//无冲突
			if (tradePositions.isEmpty()) {
				continue;
			}
			if (tradePositions.size() > 1) {
				log.error("position num err, uid = {}, symbol = {}, data = {}", uid, symbol, tradePositions);
			}
			TradePosition existPosition = tradePositions.get(0);
			log.warn("position conflict, existPosition = {}, position = {}", existPosition, position);
			long appPositionCtime = position.getCtime().getTime();
			long existPositionCtime = existPosition.getCtime().getTime();
			TradePosition primaryPosition = position;
			TradePosition slaverPosition = existPosition;
			//app仓位还未入库，需要先入库
			tradePositionMapper.insertSelective(position);
			//同方向仓位
			if (existPosition.getDirection().equals(position.getDirection())) {
				if (appPositionCtime > existPositionCtime) {
					primaryPosition = existPosition;
					slaverPosition = position;
				}
				BigDecimal filledQuantity = slaverPosition.getQuantity();
				BigDecimal filledPrice = slaverPosition.getPrice();
				//关闭时间靠后的仓位
				marginTransaction.closePosition(filledQuantity, BigDecimal.ZERO, BigDecimal.ZERO, slaverPosition);
				//平仓记录
				TradeTransaction closeTransaction = insertTransaction(UUID.randomUUID().toString(), filledQuantity, filledPrice,
						BigDecimal.ZERO, BigDecimal.ZERO, TransactionType.CLOSE_POSITION, slaverPosition, slaverPosition.getUuid());

				BigDecimal baseMultiplier = Direction.BUY == Direction.rivalDirection(closeTransaction.getDirection())? BigDecimal.ONE :
						BigDecimal.ONE.negate();
				BigDecimal quoteMultiplier = Direction.BUY == Direction.rivalDirection(closeTransaction.getDirection())?
						BigDecimal.ONE.negate() : BigDecimal.ONE;
				log.info("closeTransaction symbol = {}, base quantity = {}, quote quantity = {}", closeTransaction.getSymbol(),
						closeTransaction.getBaseQuantity().multiply(baseMultiplier).toPlainString(),
						closeTransaction.getQuoteQuantity().multiply(quoteMultiplier).toPlainString());

				//时间靠前的仓位加仓
				marginTransaction.addPosition(filledQuantity, filledPrice, BigDecimal.ZERO, primaryPosition);
				//加仓记录
				TradeTransaction addTransaction = insertTransaction(UUID.randomUUID().toString(), filledQuantity, filledPrice,
						BigDecimal.ZERO, BigDecimal.ZERO, TransactionType.ADD_POSITION, primaryPosition, primaryPosition.getUuid());

				baseMultiplier = Direction.BUY == Direction.rivalDirection(addTransaction.getDirection())? BigDecimal.ONE :
						BigDecimal.ONE.negate();
				quoteMultiplier = Direction.BUY == Direction.rivalDirection(addTransaction.getDirection())?
						BigDecimal.ONE.negate() : BigDecimal.ONE;
				log.info("addTransaction symbol = {}, base quantity = {}, quote quantity = {}", addTransaction.getSymbol(),
						addTransaction.getBaseQuantity().multiply(baseMultiplier).toPlainString(),
						addTransaction.getQuoteQuantity().multiply(quoteMultiplier).subtract(addTransaction.getPnl()).toPlainString());
			} else { //反向仓位
				if (existPosition.getQuantity().compareTo(position.getQuantity()) > 0) {
					primaryPosition = existPosition;
					slaverPosition = position;
				}
				BigDecimal filledQuantity = slaverPosition.getQuantity();
				BigDecimal filledPrice = slaverPosition.getPrice();
				//关闭时间靠后的仓位
				marginTransaction.closePosition(filledQuantity, BigDecimal.ZERO, BigDecimal.ZERO, slaverPosition);
				//平仓记录
				TradeTransaction closeTransaction = insertTransaction(UUID.randomUUID().toString(), filledQuantity, filledPrice,
						BigDecimal.ZERO, BigDecimal.ZERO, TransactionType.CLOSE_POSITION, slaverPosition, slaverPosition.getUuid());

				BigDecimal baseMultiplier = Direction.BUY == Direction.rivalDirection(closeTransaction.getDirection())? BigDecimal.ONE :
						BigDecimal.ONE.negate();
				BigDecimal quoteMultiplier = Direction.BUY == Direction.rivalDirection(closeTransaction.getDirection())?
						BigDecimal.ONE.negate() : BigDecimal.ONE;
				log.info("closeTransaction symbol = {}, base quantity = {}, quote quantity = {}", closeTransaction.getSymbol(),
						closeTransaction.getBaseQuantity().multiply(baseMultiplier).toPlainString(),
						closeTransaction.getQuoteQuantity().multiply(quoteMultiplier).toPlainString());

				//计算减仓
				BigDecimal reducePnl;
				if (Objects.equals(primaryPosition.getDirection(), Direction.BUY.getName())) {
					reducePnl = filledPrice.subtract(primaryPosition.getPrice()).multiply(filledQuantity);
				} else {
					reducePnl = primaryPosition.getPrice().subtract(filledPrice).multiply(filledQuantity);
				}
				//数量优先的仓位减仓
				marginTransaction.reducePosition(filledQuantity, BigDecimal.ZERO, reducePnl, primaryPosition);
				//减仓记录
				int length = MIGRATE_REDUCE_POSITION_PREFIX.length();
				TradeTransaction reduceTransaction =
						insertTransaction(MIGRATE_REDUCE_POSITION_PREFIX + UUID.randomUUID().toString().substring(length),
								filledQuantity,
								filledPrice,
								BigDecimal.ZERO, reducePnl, TransactionType.REDUCE_POSITION, primaryPosition, primaryPosition.getUuid());

				baseMultiplier = Direction.BUY == Direction.rivalDirection(reduceTransaction.getDirection())? BigDecimal.ONE :
						BigDecimal.ONE.negate();
				quoteMultiplier = Direction.BUY == Direction.rivalDirection(reduceTransaction.getDirection())?
						BigDecimal.ONE.negate() : BigDecimal.ONE;
				log.info("reduceTransaction symbol = {}, base quantity = {}, quote quantity = {}", reduceTransaction.getSymbol(),
						reduceTransaction.getBaseQuantity().multiply(baseMultiplier).toPlainString(),
						reduceTransaction.getQuoteQuantity().multiply(quoteMultiplier).subtract(reduceTransaction.getPnl()).toPlainString());
			}
			//remove app仓位
			iterator.remove();
		}
		int[] insertRows = insertPosition(historyPositions);
		int[] activeInsertRows = insertPosition(activePositions);
		int size = historyPositions.size() + activePositions.size();
		long sum = Arrays.stream(insertRows).sum() + Arrays.stream(activeInsertRows).sum();
		if (size != sum) {
			log.error("migrateActivePosition search size = {}, insert size = {}, size not match", size, sum);
		} else {
			log.info("migrateActivePosition search size = {}, insert size = {}", size, sum);
		}
	}

	public TradeTransaction insertTransaction(String tradeId, BigDecimal quantity, BigDecimal price,
											  BigDecimal fee, BigDecimal pnl, TransactionType type,
											  TradePosition laterPosition, String positionId) {
		TradeTransaction transaction = new TradeTransaction();
		transaction.setUuid(tradeId);
		transaction.setUid(laterPosition.getUid());
		transaction.setOrderId(MIGRATE_PREFIX + laterPosition.getUuid());
		transaction.setPositionId(positionId);
		transaction.setOrderType(OrderType.MARKET.getName());
		transaction.setType(type.name());
		transaction.setSymbol(laterPosition.getSymbol());
		Direction direction = Direction.getByName(laterPosition.getDirection());
		Direction revDirection = Direction.BUY == direction? Direction.SELL : Direction.BUY;
		Direction needDirection = direction;
		if (type == TransactionType.REDUCE_POSITION || type == TransactionType.CLOSE_POSITION) {
			needDirection = revDirection;
		}
		transaction.setDirection(needDirection.getName());
		transaction.setBaseQuantity(quantity);
		transaction.setQuoteQuantity(quantity.multiply(price));
		transaction.setPrice(price);
		transaction.setFee(fee);
		transaction.setFeeCoin(Constants.BASE_COIN);
		transaction.setPnl(pnl);
		transaction.setSource(SourceType.PLACED_BY_CLIENT.getName());
		transaction.setAssetStatus(AssetStatus.COMPLETED.name());
		Date date = new Date();
		transaction.setCtime(date);
		transaction.setMtime(date);
		tradeTransactionMapper.insertSelective(transaction);
		return transaction;
	}

	// ----------------------------- 以下为迁移细节实现 -------------------------------

	/**
	 * 交易记录迁移-杠杆
	 *
	 * @param limit
	 * @param offset
	 * @param startTime
	 * @param endTime
	 * @return 是否结束
	 */
	public boolean batchInsertTransactionAboutContract(int limit, int offset, long startTime, long endTime) {
		//pf_contract_trade
		String sql = "select pct.id, pct.uid, pco.id as order_id, pct.position_id as position_id, pco.type as order_type, pcp.uuid as " +
				"position_uuid," +
				" pct.type, " +
				"pct" +
				".symbol, pct" +
				".direction, pct.size," +
				" pct.price, pct" +
				".fee, pct.pnl, pct.ctime, pct.utime, pco.source " +
				"from pf_contract_trade pct left join pf_contract_order pco " +
				"    on pct.order_id = pco.id left join pf_contract_position pcp on pct.position_id = pcp.id where pct.status = 3 and " +
				"pct" +
				".created_time > ? and pct.created_time <= ? order by pct.created_time limit ? offset ?";
		List<TradeTransaction> transactions = appMigrateJdbcTemplate.query(sql, new Object[]{startTime, endTime, limit, offset},
				(rs, rowNum) -> {
					TradeTransaction transaction = new TradeTransaction();
					transaction.setUuid(MARGIN_PREFIX + rs.getLong("id"));
					transaction.setUid(rs.getString("uid"));
					transaction.setOrderId(APP_ID_PREFIX + MARGIN_PREFIX + rs.getString("order_id"));
					//1 GTC 2 IOC 3 MARKET
					int orderType = rs.getInt("order_type");
					OrderType orderTypeEnum;
					if (orderType == 3) {
						orderTypeEnum = OrderType.MARKET;
					} else {
						orderTypeEnum = OrderType.LIMIT;
					}
					transaction.setOrderType(orderTypeEnum.getName());
					transaction.setPositionId(APP_ID_PREFIX + rs.getString("position_id"));
					// 1 开仓; 2 加仓; 3 减仓; 4 平仓;  7强制平仓
					int type = rs.getInt("type");
					TransactionType transactionTypeEnum;
					switch (type) {
						case 1:
							transactionTypeEnum = TransactionType.OPEN_POSITION;
							break;
						case 2:
							transactionTypeEnum = TransactionType.ADD_POSITION;
							break;
						case 3:
							transactionTypeEnum = TransactionType.REDUCE_POSITION;
							break;
						case 4:
						case 7:
							transactionTypeEnum = TransactionType.CLOSE_POSITION;
							break;
						default:
							throw new RuntimeException("TransactionType not support");
					}
					transaction.setType(transactionTypeEnum.getName());
					transaction.setSymbol(rs.getString("symbol").toUpperCase());
					int direction = rs.getInt("direction");
					transaction.setDirection(direction == 1 ? Direction.BUY.getName() : Direction.SELL.getName());
					BigDecimal size = rs.getBigDecimal("size");
					BigDecimal price = rs.getBigDecimal("price");
					BigDecimal fee = rs.getBigDecimal("fee");
					BigDecimal pnl = rs.getBigDecimal("pnl");
					Date ctime = rs.getTimestamp("ctime");
					Date utime = rs.getTimestamp("utime");
					int source = rs.getInt("source");
					transaction.setBaseQuantity(size);
					transaction.setQuoteQuantity(size.multiply(price).setScale(16, RoundingMode.UP));
					transaction.setPrice(price);
					transaction.setFee(fee);
					// 原pnl是扣除了手续费后的，所以需要加回来
					transaction.setPnl(pnl.add(fee));
					transaction.setCtime(ctime);
					transaction.setMtime(utime);
					transaction.setPnlConversion(pnl.add(fee));
					SourceType sourceType;
					//0 user create  2 stop trigger 3 limit trigger 4 system close  5 user close
					switch (source) {
						case 0:
							sourceType = SourceType.PLACED_BY_CLIENT;
							break;
						case 2:
						case 3:
							sourceType = SourceType.TAKE_PROFIT_STOP_LOSS;
							break;
						case 4:
							sourceType = SourceType.FORCE_CLOSE;
							break;
						case 5:
							sourceType = SourceType.PLACED_BY_CLIENT;
							break;
						default:
							throw new RuntimeException("SourceType not support");
					}
					transaction.setSource(sourceType.getName());
					return transaction;
				});
		if (transactions.size() == 0) {
			log.info("batchInsertTransactionAboutContract search size is zero");
			return true;
		}
		int[] insertRows = insertTradeTransactionRows(transactions);
		int size = transactions.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("batchInsertTransactionAboutContract limit = {}, offset = {}, search size = {}, insert size = {}, size not match",
					limit, offset,
					size, sum);
		} else {
			log.info("batchInsertTransactionAboutContract limit = {}, offset = {}, search size = {}, insert size = {}", limit, offset,
					size, sum);
		}
		return false;
	}

	private int[] insertTradeTransactionRows(List<TradeTransaction> transactions) {
		String insertSql = "INSERT INTO trade_transaction (uuid, uid, order_id, order_type, position_id, type, symbol, direction, " +
				"base_quantity, quote_quantity, price, fee, pnl, ctime, mtime, pnl_conversion, source) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				TradeTransaction transaction = transactions.get(i);
				ps.setString(1, transaction.getUuid());
				ps.setString(2, transaction.getUid());
				ps.setString(3, transaction.getOrderId());
				ps.setString(4, transaction.getOrderType());
				ps.setString(5, transaction.getPositionId());
				ps.setString(6, transaction.getType());
				ps.setString(7, transaction.getSymbol());
				ps.setString(8, transaction.getDirection());
				ps.setBigDecimal(9, transaction.getBaseQuantity());
				ps.setBigDecimal(10, transaction.getQuoteQuantity());
				ps.setBigDecimal(11, transaction.getPrice());
				ps.setBigDecimal(12, transaction.getFee());
				ps.setBigDecimal(13, transaction.getPnl());
				ps.setTimestamp(14, new Timestamp(transaction.getCtime().getTime()));
				ps.setTimestamp(15, new Timestamp(transaction.getMtime().getTime()));
				ps.setBigDecimal(16, transaction.getPnlConversion());
				ps.setString(17, transaction.getSource());
			}

			@Override
			public int getBatchSize() {
				return transactions.size();
			}
		});
	}

	/**
	 * 交易记录迁移-现货
	 *
	 * @param limit
	 * @param offset
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public boolean batchInsertTransactionAboutSwap(int limit, int offset, long startTime, long endTime) {
		//pf_swap_trade
		String sql = "select pst.id, pst.uid, pso.id as order_id, pso.type as order_type, pst.symbol, pst.direction, pst.deal, pst" +
				".price, pst.fee,pst.ctime,pst.utime,pst.amount " +
				" from pf_swap_trade pst left join pf_swap_order pso" +
				"  on pst.order_id = pso.id where  pst.created_time > ? and pst.created_time <= ? order by pst.created_time " +
				"limit ? offset ?";
		List<TradeTransaction> transactions = appMigrateJdbcTemplate.query(sql, new Object[]{startTime, endTime, limit, offset},
				(rs, rowNum) -> {
					TradeTransaction transaction = new TradeTransaction();
					transaction.setUuid(SPOT_PREFIX + rs.getLong("id"));
					transaction.setUid(rs.getString("uid"));
					transaction.setOrderId(APP_ID_PREFIX + SPOT_PREFIX + rs.getString("order_id"));
					//1 GTC 2 IOC 3 MARKET 4 liquid
					int orderType = rs.getInt("order_type");
					OrderType orderTypeEnum;
					if (orderType == 3 || orderType == 4 || orderType == 6) {
						orderTypeEnum = OrderType.MARKET;
					} else {
						orderTypeEnum = OrderType.LIMIT;
					}
					transaction.setOrderType(orderTypeEnum.getName());
					transaction.setPositionId(null);
					transaction.setType(TransactionType.SPOT.getName());
					transaction.setSymbol(rs.getString("symbol").toUpperCase());
					int direction = rs.getInt("direction");
					transaction.setDirection(direction == 1 ? Direction.BUY.getName() : Direction.SELL.getName());
					BigDecimal deal = rs.getBigDecimal("deal");
					BigDecimal amount = rs.getBigDecimal("amount");
					BigDecimal price = rs.getBigDecimal("price");
					BigDecimal fee = rs.getBigDecimal("fee");
					Date ctime = rs.getTimestamp("ctime");
					Date utime = rs.getTimestamp("utime");
					transaction.setBaseQuantity(deal);
					transaction.setQuoteQuantity(amount);
					transaction.setPrice(price);
					transaction.setFee(fee);
					transaction.setPnl(null);
					transaction.setCtime(ctime);
					transaction.setMtime(utime);
					transaction.setPnlConversion(null);
					SourceType sourceType;
					if (orderType == 4) {
						sourceType = SourceType.LIQUIDATION;
					} else {
						sourceType = SourceType.PLACED_BY_CLIENT;
					}
					transaction.setSource(sourceType.getName());
					return transaction;
				});
		if (transactions.size() == 0) {
			log.info("batchInsertTransactionAboutSwap search size is zero");
			return true;
		}
		int[] insertRows = insertTradeTransactionRows(transactions);
		int size = transactions.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("batchInsertTransactionAboutSwap limit = {}, offset = {}, search size = {}, insert size = {}, size not match", limit
					, offset,
					size, sum);
		} else {
			log.info("batchInsertTransactionAboutSwap limit = {}, offset = {}, search size = {}, insert size = {}", limit, offset, size,
					sum);
		}
		return false;
	}

	/**
	 * 兑换订单迁移
	 *
	 * @param limit
	 * @param offset
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public boolean batchInsertSwapOrder(int limit, int offset, long startTime, long endTime) {
		//pf_exchange_order
		String sql = "select id, uuid, uid, send_coin, receive_coin, fee, delegate_direction, fee_rate,  direction, price, " +
				"exchange_price," +
				" status, error_code," +
				" ctime, utime, real_num, num, exchange_num, symbol " +
				"from pf_exchange_order where status in (1, -2) and created_time > ? and created_time <= ? order by created_time limit ?" +
				" " +
				"offset" +
				" ?";
		List<TradeSwapOrder> orders = appMigrateJdbcTemplate.query(sql, new Object[]{startTime, endTime, limit, offset},
				new RowMapper<TradeSwapOrder>() {
					@Override
					public TradeSwapOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
						TradeSwapOrder order = new TradeSwapOrder();
						order.setUuid(APP_ID_PREFIX + SPOT_PREFIX + rs.getLong("id"));
						order.setUid(rs.getString("uid"));

						int direction = rs.getInt("direction");
						String symbol = rs.getString("symbol").toUpperCase();
						String[] coinArr = symbol.split("_");

						String sendCoin;
						String receiveCoin;
						if (StringUtils.isEmpty(rs.getString("send_coin")) || StringUtils.isEmpty(rs.getString("receive_coin"))) {
							sendCoin = direction == 1? coinArr[1]: coinArr[0];
							receiveCoin = direction == 1? coinArr[0]: coinArr[1];
						} else {
							sendCoin = rs.getString("send_coin").toUpperCase();
							receiveCoin = rs.getString("receive_coin").toUpperCase();
						}
						order.setFromCoin(sendCoin);
						order.setToCoin(receiveCoin);
						order.setFee(rs.getBigDecimal("fee"));
						SwapType mode;
						int delegateDirection = rs.getInt("delegate_direction");
						// delegate_direction, 1（委托兑入） = 固定获得， 2（委托兑出） = 固定支付
						if (1 == delegateDirection) {
							mode = SwapType.OBTAINED;
						} else {
							mode = SwapType.PAYMENT;
						}
						order.setMode(mode.getName());
						order.setFeeRate(rs.getBigDecimal("fee_rate"));
						BigDecimal price = rs.getBigDecimal("price");
						// 1 买 2卖
						BigDecimal orderPrice;
						if (direction == 1) {
							orderPrice = BigDecimal.ONE.divide(price, Constants.DEFAULT_PRECISION, RoundingMode.HALF_UP);
						} else {
							orderPrice = price;
						}
						order.setOrderPrice(orderPrice);
						BigDecimal dealPrice = null;
						String memo = "";
						OrderStatus orderStatus = OrderStatus.CANCELED;
						int status = rs.getInt("status");
						int errorCode = rs.getInt("error_code");
						if (status == 1) {
							orderStatus = OrderStatus.COMPLETED;
						}
						// 110122 订单超时，110121 可用余额不足
						if (errorCode == 110122) {
							memo = I18nEnum.TRADING_SWAP_ORDER_MEMO_TIMEOUT.getKey();
							orderStatus = OrderStatus.CANCELED;
						} else if (errorCode == 110121) {
							memo = I18nEnum.TRADING_SWAP_ORDER_MEMO_NO_BALANCE.getKey();
							orderStatus = OrderStatus.CANCELED;
						}
						order.setStatus(orderStatus.getName());
						order.setMemo(memo);
						BigDecimal exchangePrice = rs.getBigDecimal("exchange_price");
						if (orderStatus == OrderStatus.COMPLETED) {
							if (direction == 1 && exchangePrice.compareTo(BigDecimal.ZERO) != 0) {
								dealPrice = BigDecimal.ONE.divide(exchangePrice, Constants.DEFAULT_PRECISION, RoundingMode.HALF_UP);
							} else {
								dealPrice = exchangePrice;
							}
						}
						order.setDealPrice(dealPrice);

						BigDecimal realNum = rs.getBigDecimal("real_num");
						BigDecimal num = rs.getBigDecimal("num");
						BigDecimal exchangeNum = rs.getBigDecimal("exchange_num");

						order.setCtime(rs.getTimestamp("ctime"));
						order.setMtime(rs.getTimestamp("utime"));
						String feeCoin;
						BigDecimal fromQuantity;
						BigDecimal toQuantity;
						if (delegateDirection == 1) {
							fromQuantity = realNum;
							toQuantity = num;
							feeCoin = sendCoin;
						} else {
							fromQuantity = num;
							toQuantity = exchangeNum;
							feeCoin = receiveCoin;
						}
						order.setFromQuantity(fromQuantity);
						order.setToQuantity(toQuantity);
						order.setSource(SourceType.PLACED_BY_API.getName());
						order.setFeeCoin(feeCoin);
						return order;
					}
				});
		if (orders.size() == 0) {
			log.info("batchInsertSwapOrder search size is zero");
			return true;
		}

		String insertSql = "insert into trade_swap_order (uuid, uid, from_coin, " +
				"      to_coin, fee, mode, " +
				"      fee_rate, order_price, deal_price, " +
				"      status, memo, ctime, " +
				"      mtime, from_quantity, to_quantity, " +
				"      source, fee_coin) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] insertRows = template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				TradeSwapOrder order = orders.get(i);
				ps.setString(1, order.getUuid());
				ps.setString(2, order.getUid());
				ps.setString(3, order.getFromCoin());
				ps.setString(4, order.getToCoin());
				ps.setBigDecimal(5, order.getFee());
				ps.setString(6, order.getMode());
				ps.setBigDecimal(7, order.getFeeRate());
				ps.setBigDecimal(8, order.getOrderPrice());
				ps.setBigDecimal(9, order.getDealPrice());
				ps.setString(10, order.getStatus());
				ps.setString(11, order.getMemo());
				ps.setTimestamp(12, new Timestamp(order.getCtime().getTime()));
				ps.setTimestamp(13, new Timestamp(order.getMtime().getTime()));
				ps.setBigDecimal(14, order.getFromQuantity());
				ps.setBigDecimal(15, order.getToQuantity());
				ps.setString(16, order.getSource());
				ps.setString(17, order.getFeeCoin());
			}

			@Override
			public int getBatchSize() {
				return orders.size();
			}
		});
		int size = orders.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("batchInsertSwapOrder limit = {}, offset = {}, search size = {}, insert size = {}, size not match", limit
					, offset,
					size, sum);
		} else {
			log.info("batchInsertSwapOrder limit = {}, offset = {}, search size = {}, insert size = {}", limit, offset, size,
					sum);
		}
		return false;
	}

	/**
	 * 现货订单迁移
	 *
	 * @param limit
	 * @param offset
	 * @return
	 */
	public boolean batchInsertSpotOrder(int limit, int offset) {
		//pf_swap_order
		String sql = "select * from pf_swap_order where status in (-1, 0, 1, 4, 6) and created_time > ? and created_time <= ?" +
				" " +
				"order by " +
				"created_time limit ? " +
				"offset ?";
		List<TradeSpotOrder> orders = appMigrateJdbcTemplate.query(sql, new Object[]{START, END, limit, offset}, spotOrderRowMapper());
		//true 是活跃订单
		Map<Boolean, List<TradeSpotOrder>> map =
				orders.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(new Function<TradeSpotOrder, Boolean>() {
					@Override
					public Boolean apply(TradeSpotOrder tradeSpotOrder) {
						return OrderStatus.EXECUTING.getName().equals(tradeSpotOrder.getStatus());
					}
				}, Collectors.toList()));
		orders = null;
		List<TradeSpotOrder> activeOrders = map.getOrDefault(Boolean.TRUE, new ArrayList<>());
		List<TradeSpotOrder> historyOrders = map.getOrDefault(Boolean.FALSE, new ArrayList<>());

		Map<String, String> activeOrderIdMap =
				activeOrders.stream().map(TradeSpotOrder::getUuid).map(v -> v.replaceFirst(APP_ID_PREFIX + SPOT_PREFIX, "")).collect(Collectors.toMap(v -> v, s -> HASH_VALUE));
		redisTemplate.opsForHash().putAll(ACTIVE_SPOT_ORDERS_KEY, activeOrderIdMap);
		//历史订单为空
		if (historyOrders.size() == 0) {
			log.info("batchInsertSpotOrder search size is zero");
			return true;
		}

		int[] insertRows = insertSpotOrders(historyOrders);
		int size = historyOrders.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("batchInsertSpotOrder limit = {}, offset = {}, active size = {}, history size = {}, insert size = {}, size not match",
					limit
					, offset,
					activeOrderIdMap.size(), size, sum);
		} else {
			log.info("batchInsertSpotOrder limit = {}, offset = {}, active size = {}, history size = {}, insert size = {}", limit, offset,
					activeOrderIdMap.size(), size,
					sum);
		}
		return false;
	}

	private int[] insertSpotOrders(List<TradeSpotOrder> historyOrders) {
		String insertSql = "insert into trade_spot_order (uuid, uid, status, " +
				"      type, source, strategy, " +
				"      symbol, direction, is_quote, " +
				"      quantity, lock_amount, price, " +
				"      quantity_filled, amount_filled, filled_price, " +
				"      trigger_price, trigger_compare, fee, " +
				"      notes, error, ctime, " +
				"      mtime, fee_coin, terminator" +
				"      )" +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] insertRows = template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				TradeSpotOrder order = historyOrders.get(i);
				ps.setString(1, order.getUuid());
				ps.setString(2, order.getUid());
				ps.setString(3, order.getStatus());
				ps.setString(4, order.getType());
				ps.setString(5, order.getSource());
				ps.setString(6, order.getStrategy());
				ps.setString(7, order.getSymbol());
				ps.setString(8, order.getDirection());
				ps.setBoolean(9, order.getIsQuote());
				ps.setBigDecimal(10, order.getQuantity());
				ps.setBigDecimal(11, order.getLockAmount());
				ps.setBigDecimal(12, order.getPrice());
				ps.setBigDecimal(13, order.getQuantityFilled());
				ps.setBigDecimal(14, order.getAmountFilled());
				ps.setBigDecimal(15, order.getFilledPrice());
				ps.setBigDecimal(16, order.getTriggerPrice());
				ps.setString(17, order.getTriggerCompare());
				ps.setBigDecimal(18, order.getFee());
				ps.setString(19, order.getNotes());
				ps.setString(20, order.getError());
				ps.setTimestamp(21, new Timestamp(order.getCtime().getTime()));
				ps.setTimestamp(22, new Timestamp(order.getMtime().getTime()));
				ps.setString(23, order.getFeeCoin());
				ps.setString(24, order.getTerminator());
			}

			@Override
			public int getBatchSize() {
				return historyOrders.size();
			}
		});
		return insertRows;
	}

	public RowMapper<TradeSpotOrder> spotOrderRowMapper() {
		return (rs, rowNum) -> {
			TradeSpotOrder order = new TradeSpotOrder();
			long id = rs.getLong("id");
			order.setUuid(APP_ID_PREFIX + SPOT_PREFIX + id);
			order.setUid(rs.getString("uid"));
			OrderStatus orderStatus;
			SourceType sourceType = SourceType.PLACED_BY_CLIENT;
			TradeTerminator tradeTerminator = TradeTerminator.CLIENT;
			int status = rs.getInt("status");
			//挂单中的订单
			if (status == -1 || status == 0) {
				orderStatus = OrderStatus.EXECUTING;
			} else if (status == 1) { //已完成
				orderStatus = OrderStatus.COMPLETED;
			} else if (status == 4) { //系统撤销
				orderStatus = OrderStatus.CANCELED;
				tradeTerminator = TradeTerminator.SYSTEM;
			} else if (status == 6) { //用户撤销
				orderStatus = OrderStatus.CANCELED;
			} else {
				//脏数据 skip
				return null;
			}
			// 1GTC 2IOC(FAK) 3market 4liquidation
			int type = rs.getInt("type");
			OrderType orderType;
			TradeStrategy tradeStrategy = null;
			if (type == 1) {
				orderType = OrderType.LIMIT;
				tradeStrategy = TradeStrategy.GTC;
			} else if (type == 2) {
				orderType = OrderType.LIMIT;
				tradeStrategy = TradeStrategy.IOC;
			} else if (type == 4) {
				orderType = OrderType.MARKET;
				sourceType = SourceType.LIQUIDATION;
			} else {
				orderType = OrderType.MARKET;
			}
			order.setStatus(orderStatus.getName());
			order.setType(orderType.getName());
			order.setSource(sourceType.getName());
			if (null != tradeStrategy) {
				order.setStrategy(tradeStrategy.getName());
			}
			String symbol = rs.getString("symbol").toUpperCase();
			order.setSymbol(symbol);
			int direction = rs.getInt("direction");
			BigDecimal deal = rs.getBigDecimal("deal");
			BigDecimal amount = rs.getBigDecimal("amount");
			BigDecimal feeRate = rs.getBigDecimal("fee_rate");
			Direction tradeDirection;
			BigDecimal fee = null;
			String feeCoin;
			if (direction == 1) {
				tradeDirection = Direction.BUY;
				if (null != deal && null != feeRate) {
					fee = deal.multiply(feeRate);
				}
				//base
				feeCoin = symbol.split("_")[0];
			} else {
				tradeDirection = Direction.SELL;
				if (null != deal && null != feeRate) {
					fee = amount.multiply(feeRate);
				}
				//quote
				feeCoin = symbol.split("_")[1];
			}
			order.setDirection(tradeDirection.getName());
			order.setIsQuote(false);
			order.setQuantity(rs.getBigDecimal("num"));
			order.setLockAmount(BigDecimal.ZERO);
			order.setPrice(rs.getBigDecimal("price"));

			order.setQuantityFilled(deal);
			order.setAmountFilled(amount);
			if (null != deal && null != amount && deal.compareTo(BigDecimal.ZERO) != 0) {
				order.setFilledPrice(amount.divide(deal, Constants.DEFAULT_PRECISION, RoundingMode.HALF_UP));
			}
			order.setTriggerPrice(null);
			order.setTriggerCompare(null);
			order.setFee(fee);
			order.setNotes(null);
			order.setError(null);
			Date ctime = rs.getTimestamp("ctime");
			Date utime = rs.getTimestamp("utime");
			order.setCtime(ctime);
			order.setMtime(utime);
			order.setFeeCoin(feeCoin);
			order.setTerminator(tradeTerminator.getCode());
			return order;
		};
	}

	/**
	 * 仓位数据迁移 1w
	 *
	 * @param limit
	 * @param offset
	 * @return
	 */
	public boolean batchInsertPosition(int limit, int offset) {
		//pf_swap_order
		String sql = "select * from pf_contract_position where created_time > ? and created_time <= ? order by created_time limit ? " +
				"offset ?";
		List<String> skipPositions = new ArrayList<>();
		List<TradePosition> positions = appMigrateJdbcTemplate.query(sql, new Object[]{START, END, limit, offset},
				tradePositionRowMapper());
		//true 是活跃仓位
		Map<Boolean, List<TradePosition>> map =
				positions.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(new Function<TradePosition, Boolean>() {
					@Override
					public Boolean apply(TradePosition position) {
						return PositionStatus.ACTIVE.name().equals(position.getStatus());
					}
				}, Collectors.toList()));
		positions = null;
		List<TradePosition> activePositions = map.getOrDefault(Boolean.TRUE, new ArrayList<>());
		List<TradePosition> historyPositions = map.getOrDefault(Boolean.FALSE, new ArrayList<>());

		Map<String, String> activePositionIdMap =
				activePositions.stream().map(TradePosition::getUuid).map(v -> v.replaceFirst(APP_ID_PREFIX,
				"")).collect(Collectors.toMap(v -> v, s-> HASH_VALUE));

		redisTemplate.opsForHash().putAll(ACTIVE_POSITIONS_KEY, activePositionIdMap);

		if (historyPositions.size() == 0) {
			log.info("batchInsertPosition search size is zero");
			return true;
		}

		int[] insertRows = insertPosition(historyPositions);
		int size = historyPositions.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("batchInsertPosition limit = {}, offset = {}, active size = {}, history size = {}, insert size = {}, size not match",
					limit
					, offset,
					activePositionIdMap.size(), size, sum);
		} else {
			log.info("batchInsertPosition limit = {}, offset = {}, active size = {}, history size = {}, insert size = {}", limit, offset,
					activePositionIdMap.size(), size,
					sum);
		}
		return false;
	}

	private int[] insertPosition(List<TradePosition> historyPositions) {
		String insertSql = "insert into trade_position (uid, status, symbol,  " +
				"      direction, quantity, price,  " +
				"      auto_settle, stop_loss_price, stop_loss_percentage,  " +
				"      take_profit_price, take_profit_percentage,  " +
				"      max_quantity, pnl, ctime,  " +
				"      mtime, uuid)" +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] insertRows = template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				TradePosition position = historyPositions.get(i);
				ps.setString(1, position.getUid());
				ps.setString(2, position.getStatus());
				ps.setString(3, position.getSymbol());
				ps.setString(4, position.getDirection());
				ps.setBigDecimal(5, position.getQuantity());
				ps.setBigDecimal(6, position.getPrice());
				ps.setBoolean(7, position.getAutoSettle());
				ps.setBigDecimal(8, position.getStopLossPrice());
				ps.setBigDecimal(9, position.getStopLossPercentage());
				ps.setBigDecimal(10, position.getTakeProfitPrice());
				ps.setBigDecimal(11, position.getTakeProfitPercentage());
				ps.setBigDecimal(12, position.getMaxQuantity());
				ps.setBigDecimal(13, position.getPnl());
				ps.setTimestamp(14, new Timestamp(position.getCtime().getTime()));
				ps.setTimestamp(15, new Timestamp(position.getMtime().getTime()));
				ps.setString(16, position.getUuid());
			}

			@Override
			public int getBatchSize() {
				return historyPositions.size();
			}
		});
		return insertRows;
	}

	private RowMapper<TradePosition> tradePositionRowMapper() {
		return new RowMapper<TradePosition>() {
			@Override
			public TradePosition mapRow(ResultSet rs, int rowNum) throws SQLException {
				TradePosition position = new TradePosition();
				long id = rs.getLong("id");
				position.setUid(rs.getString("uid"));
				int status = rs.getInt("status");
				PositionStatus positionStatus;
				// 0 active 1 closed
				if (status == 0) {
					positionStatus = PositionStatus.ACTIVE;
				} else {
					positionStatus = PositionStatus.CLOSE;
				}
				position.setStatus(positionStatus.name());
				String symbol = rs.getString("symbol").toUpperCase();
				position.setSymbol(symbol);
				int direction = rs.getInt("direction");
				Direction tradeDirection;
				if (direction == 1) {
					tradeDirection = Direction.BUY;
				} else {
					tradeDirection = Direction.SELL;
				}
				position.setDirection(tradeDirection.getName());
				BigDecimal size = rs.getBigDecimal("size");
				BigDecimal maxSize = rs.getBigDecimal("max_size");
				BigDecimal open = rs.getBigDecimal("open");
				BigDecimal pnl = rs.getBigDecimal("pnl");
				position.setQuantity(size);
				position.setPrice(open);
				position.setAutoSettle(false);
				BigDecimal tp = rs.getBigDecimal("tp");
				BigDecimal sl = rs.getBigDecimal("sl");
				//历史仓位清空止盈止损数据
				if (positionStatus == PositionStatus.CLOSE) {
					position.setStopLossPrice(null);
					position.setStopLossPercentage(BigDecimal.ZERO);
					position.setTakeProfitPrice(null);
					position.setTakeProfitPercentage(BigDecimal.ZERO);
				} else {
					position.setStopLossPrice(sl);
					position.setStopLossPercentage(BigDecimal.ONE);
					position.setTakeProfitPrice(tp);
					position.setTakeProfitPercentage(BigDecimal.ONE);

				}
				position.setMaxQuantity(maxSize);
				position.setPnl(pnl);
				position.setCtime(rs.getTimestamp("ctime"));
				position.setMtime(rs.getTimestamp("utime"));
				position.setUuid(APP_ID_PREFIX + id);
				return position;
			}
		};
	}

	/**
	 * 仓位资金费率迁移 8w
	 *
	 * @param limit
	 * @param offset
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public boolean batchInsertPositionFundingCost(int limit, int offset, long startTime, long endTime) {
		//pf_contract_position_commission
		String sql = "select pcpc.*, pcp.direction as direction from pf_contract_position_commission pcpc left join pf_contract_position" +
				" " +
				"pcp on pcpc" +
				".contract_position_id " +
				"= pcp.id" +
				" where pcpc.created_time >" +
				" ?" +
				" and pcpc.created_time <= ? order by " +
				"pcpc.created_time, pcpc.updated_time, pcpc.contract_position_id " +
				"limit ? " +
				"offset ?";
		List<TradePositionFundingCost> fundingCosts = appMigrateJdbcTemplate.query(sql, new Object[]{startTime, endTime, limit, offset},
				new RowMapper<TradePositionFundingCost>() {
					@Override
					public TradePositionFundingCost mapRow(ResultSet rs, int rowNum) throws SQLException {
						TradePositionFundingCost fundingCost = new TradePositionFundingCost();
						long id = rs.getLong("id");
						fundingCost.setUuid(id + "");
						fundingCost.setUid(rs.getString("uid"));
						fundingCost.setStatus(FundingCostStatus.COMPLETED.getName());
						fundingCost.setQuantity(BigDecimal.ZERO);
						fundingCost.setFundingCost(rs.getBigDecimal("amount"));
						fundingCost.setPositionId(APP_ID_PREFIX + rs.getLong("contract_position_id"));
						fundingCost.setSymbol(rs.getString("symbol").toUpperCase());
						int direction = rs.getInt("direction");
						Direction tradeDirection;
						if (direction == 1) {
							tradeDirection = Direction.BUY;
						} else {
							tradeDirection = Direction.SELL;
						}
						fundingCost.setDirection(tradeDirection.getName());
						fundingCost.setCtime(rs.getTimestamp("ctime"));
						fundingCost.setMtime(rs.getTimestamp("utime"));
						fundingCost.setCoin("USD");
						fundingCost.setPrice(BigDecimal.ZERO);
						fundingCost.setLend(rs.getBigDecimal("buy_commission"));
						fundingCost.setBorrow(rs.getBigDecimal("sell_commission"));
						String commissionRound = rs.getString("commission_round");
						long round;
						try {
							round = Long.parseLong(commissionRound);
						} catch (Exception e) {
							//ignore
							round = rs.getLong("created_time");
						}
						fundingCost.setRound(round);
						return fundingCost;
					}
				});
		List<TradePositionFundingCost> newFundingCost = fundingCosts.stream().filter(Objects::nonNull).collect(Collectors.toList());
		fundingCosts = null;
		if (newFundingCost.size() == 0) {
			log.info("batchInsertPositionFundingCost search size is zero");
			return true;
		}

		String insertSql = "insert into trade_position_funding_cost (uuid, uid, status,  " +
				"      quantity, funding_cost, position_id,  " +
				"      symbol, direction, ctime,  " +
				"      mtime, coin, price,  " +
				"      lend, borrow, round)" +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] insertRows = template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				TradePositionFundingCost fundingCost = newFundingCost.get(i);
				ps.setString(1, fundingCost.getUuid());
				ps.setString(2, fundingCost.getUid());
				ps.setString(3, fundingCost.getStatus());
				ps.setBigDecimal(4, fundingCost.getQuantity());
				ps.setBigDecimal(5, fundingCost.getFundingCost());
				ps.setString(6, fundingCost.getPositionId());
				ps.setString(7, fundingCost.getSymbol());
				ps.setString(8, fundingCost.getDirection());
				ps.setTimestamp(9, new Timestamp(fundingCost.getCtime().getTime()));
				ps.setTimestamp(10, new Timestamp(fundingCost.getMtime().getTime()));
				ps.setString(11, fundingCost.getCoin());
				ps.setBigDecimal(12, fundingCost.getPrice());
				ps.setBigDecimal(13, fundingCost.getLend());
				ps.setBigDecimal(14, fundingCost.getBorrow());
				ps.setLong(15, fundingCost.getRound());
			}

			@Override
			public int getBatchSize() {
				return newFundingCost.size();
			}
		});
		int size = newFundingCost.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("batchInsertPositionFundingCost limit = {}, offset = {}, search size = {}, insert size = {}, size not match", limit
					, offset,
					size, sum);
		} else {
			log.info("batchInsertPositionFundingCost limit = {}, offset = {}, search size = {}, insert size = {}", limit, offset, size,
					sum);
		}
		return false;
	}

	/**
	 * 杠杆订单迁移
	 *
	 * @param limit
	 * @param offset
	 * @return
	 */
	public boolean batchInsertMarginOrder(int limit, int offset) {
		//统计fee
		String feeSumSql = "select order_id, COALESCE(sum(fee), 0) as total_fee from pf_contract_trade where status = 3 and  created_time" +
				" " +
				"> ? " +
				"and " +
				"created_time " +
				"<= ? group by order_id";
		Map<String, BigDecimal> feeMap = appMigrateJdbcTemplate.query(feeSumSql, new Object[]{START, END},
				rs -> {
					Map<String, BigDecimal> map = new HashMap<>();
					while (rs.next()) {
						String orderId = APP_ID_PREFIX + MARGIN_PREFIX + rs.getString("order_id");
						BigDecimal totalFee = rs.getBigDecimal("total_fee");
						map.put(orderId, totalFee);
					}
					return map;
				});

		//pf_contract_order
		String sql = "select * from pf_contract_order where status in (-1, 0, 1, 4, 6) and created_time > ? and created_time <= " +
				"? " +
				"order by " +
				"created_time limit ? " +
				"offset ?";
		List<TradeMarginOrder> margins = appMigrateJdbcTemplate.query(sql, new Object[]{START, END, limit, offset},
				marginOrderRowMapper(feeMap));
		//true 是活跃订单
		Map<Boolean, List<TradeMarginOrder>> map =
				margins.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(new Function<TradeMarginOrder, Boolean>() {
					@Override
					public Boolean apply(TradeMarginOrder order) {
						return OrderStatus.EXECUTING.getName().equals(order.getStatus());
					}
				}, Collectors.toList()));
		margins = null;
		List<TradeMarginOrder> activeOrders = map.getOrDefault(Boolean.TRUE, new ArrayList<>());
		List<TradeMarginOrder> historyOrders = map.getOrDefault(Boolean.FALSE, new ArrayList<>());

		Map<String, String> activeOrderIdMap =
				activeOrders.stream().map(TradeMarginOrder::getUuid).map(v -> v.replaceFirst(APP_ID_PREFIX + MARGIN_PREFIX, "")).collect(Collectors.toMap(v -> v, s -> HASH_VALUE));
		redisTemplate.opsForHash().putAll(ACTIVE_MARGIN_ORDERS_KEY, activeOrderIdMap);

		margins = null;
		if (historyOrders.size() == 0) {
			log.info("batchInsertMarginOrder search size is zero");
			return true;
		}

		int[] insertRows = insertMarginOrders(historyOrders);
		int size = historyOrders.size();
		long sum = Arrays.stream(insertRows).sum();
		if (size != sum) {
			log.error("batchInsertMarginOrder limit = {}, offset = {}, active size = {}, history size = {}, insert size = {}, size not match"
					, limit
					, offset,
					activeOrderIdMap.size(), size, sum);
		} else {
			log.info("batchInsertMarginOrder limit = {}, offset = {}, active size = {}, history size = {}, insert size = {}", limit, offset,
					activeOrderIdMap.size(), size,
					sum);
		}
		return false;
	}

	private int[] insertMarginOrders(List<TradeMarginOrder> historyOrders) {
		String insertSql = "insert into trade_margin_order (uuid, uid, status,  " +
				"      type, strategy, symbol,  " +
				"      direction, quantity, price,  " +
				"      reduce_only, trigger_price, trigger_compare,  " +
				"      quantity_filled, filled_price, fee,  " +
				"      notes, error, ctime,  " +
				"      mtime, source, terminator " +
				"      )" +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int[] insertRows = template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				TradeMarginOrder order = historyOrders.get(i);
				ps.setString(1, order.getUuid());
				ps.setString(2, order.getUid());
				ps.setString(3, order.getStatus());
				ps.setString(4, order.getType());
				ps.setString(5, order.getStrategy());
				ps.setString(6, order.getSymbol());
				ps.setString(7, order.getDirection());
				ps.setBigDecimal(8, order.getQuantity());
				ps.setBigDecimal(9, order.getPrice());
				ps.setBoolean(10, order.getReduceOnly());
				ps.setBigDecimal(11, order.getTriggerPrice());
				ps.setString(12, order.getTriggerCompare());
				ps.setBigDecimal(13, order.getQuantityFilled());
				ps.setBigDecimal(14, order.getFilledPrice());
				ps.setBigDecimal(15, order.getFee());
				ps.setString(16, order.getNotes());
				ps.setString(17, order.getError());
				ps.setTimestamp(18, new Timestamp(order.getCtime().getTime()));
				ps.setTimestamp(19, new Timestamp(order.getMtime().getTime()));
				ps.setString(20, order.getSource());
				ps.setString(21, order.getTerminator());
			}

			@Override
			public int getBatchSize() {
				return historyOrders.size();
			}
		});
		return insertRows;
	}

	private RowMapper<TradeMarginOrder> marginOrderRowMapper(Map<String, BigDecimal> feeMap) {
		return new RowMapper<TradeMarginOrder>() {
			@Override
			public TradeMarginOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
				TradeMarginOrder order = new TradeMarginOrder();
				long id = rs.getLong("id");
				String orderId = APP_ID_PREFIX + MARGIN_PREFIX + id;
				order.setUuid(orderId);
				order.setUid(rs.getString("uid"));
				OrderStatus orderStatus;
				SourceType sourceType;
				TradeTerminator tradeTerminator = TradeTerminator.CLIENT;
				int status = rs.getInt("status");
				//挂单中的订单
				if (status == -1 || status == 0) {
					orderStatus = OrderStatus.EXECUTING;
				} else if (status == 1) { //已完成
					orderStatus = OrderStatus.COMPLETED;
				} else if (status == 4) { //系统撤销
					orderStatus = OrderStatus.CANCELED;
					tradeTerminator = TradeTerminator.SYSTEM;
				} else if (status == 6) { //用户撤销
					orderStatus = OrderStatus.CANCELED;
				} else {
					//脏数据 skip
					return null;
				}// 1GTC 2IOC(FAK) 3market
				int type = rs.getInt("type");
				OrderType orderType;
				TradeStrategy tradeStrategy = null;
				if (type == 1) {
					orderType = OrderType.LIMIT;
					tradeStrategy = TradeStrategy.GTC;
				} else if (type == 2) {
					orderType = OrderType.LIMIT;
					tradeStrategy = TradeStrategy.IOC;
				} else {
					orderType = OrderType.MARKET;
				}
				order.setStatus(orderStatus.getName());
				order.setType(orderType.getName());
				if (null != tradeStrategy) {
					order.setStrategy(tradeStrategy.getName());
				}
				String symbol = rs.getString("symbol").toUpperCase();
				order.setSymbol(symbol);
				int direction = rs.getInt("direction");
				Direction tradeDirection;
				if (direction == 1) {
					tradeDirection = Direction.BUY;
				} else {
					tradeDirection = Direction.SELL;
				}
				order.setDirection(tradeDirection.getName());
				order.setQuantity(rs.getBigDecimal("size"));
				order.setPrice(rs.getBigDecimal("price"));

				int source = rs.getInt("source");
				boolean reduceOnly = false;
				//0 user create  2 stop trigger 3 limit trigger 4 system close  5 user close
				switch (source) {
					case 0:
						sourceType = SourceType.PLACED_BY_CLIENT;
						break;
					case 2:
					case 3:
						sourceType = SourceType.TAKE_PROFIT_STOP_LOSS;
						break;
					case 4:
						sourceType = SourceType.FORCE_CLOSE;
						break;
					case 5:
						sourceType = SourceType.PLACED_BY_CLIENT;
						reduceOnly = true;
						break;
					default:
						throw new RuntimeException("SourceType not support");
				}

				order.setReduceOnly(reduceOnly);
				order.setTriggerPrice(null);
				order.setTriggerCompare(null);
				BigDecimal dealSize = rs.getBigDecimal("deal_size");
				BigDecimal amount = rs.getBigDecimal("amount");
				order.setQuantityFilled(dealSize);
				if (null != dealSize && null != amount && dealSize.compareTo(BigDecimal.ZERO) != 0) {
					order.setFilledPrice(amount.divide(dealSize, Constants.DEFAULT_PRECISION, RoundingMode.HALF_UP));
				}
				order.setFee(null == feeMap ? BigDecimal.ZERO : feeMap.getOrDefault(orderId, BigDecimal.ZERO));
				order.setNotes(null);
				order.setError(null);
				order.setCtime(rs.getTimestamp("ctime"));
				order.setMtime(rs.getTimestamp("utime"));
				order.setSource(sourceType.getName());
				order.setTerminator(tradeTerminator.getCode());
				return order;
			}
		};
	}

	/**
	 * 行情关注迁移（全量）
	 *
	 * @return
	 */
	public void batchInsertMarketFavorite() {
		List<WatchList> list = mongoTemplate.findAll(WatchList.class, "watch_list");
		log.info("batchInsertMarketFavorite data size = {}", list.size());
		String insertSql = "insert into trade_user_market_favorite (symbol_arr, " +
				"      uid, ctime, mtime " +
				"      )" +
				"VALUES (?,?,?,?)";
		template.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				WatchList watchList = list.get(i);
				List<String> symbolList =
						watchList.getWatchCoinList().stream().filter(v -> !v.equalsIgnoreCase("USD")).map(v -> v.toUpperCase() + "_USD").collect(Collectors.toList());
				Array array = ps.getConnection().createArrayOf("VARCHAR", symbolList.toArray());
				ps.setArray(1, array);
				ps.setString(2, watchList.getUid());
				java.util.Date ctime = watchList.getCtime();
				java.util.Date utime = watchList.getUtime();
				ctime = null == ctime ? new java.util.Date() : ctime;
				utime = null == utime ? new java.util.Date() : utime;
				ps.setTimestamp(3, new Timestamp(ctime.getTime()));
				ps.setTimestamp(4, new Timestamp(utime.getTime()));
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	public void migrateSettleAsset() {
		TradeTransactionExample example = new TradeTransactionExample();
		example.createCriteria().andUuidLike(MIGRATE_REDUCE_POSITION_PREFIX + "%")
				//排除无效订单
				.andPdtStatusEqualTo(PdtStatus.COMPLETED.name());
		List<TradeTransaction> tradeTransactions = tradeTransactionMapper.selectByExample(example);
		log.info("settle asset transaction size = {}", tradeTransactions.size());
		for (TradeTransaction tradeTransaction : tradeTransactions) {
			assetRequest.doClosePosition(tradeTransaction);
		}
	}


	/**
	 * 重新计算pro在持仓位的pnl
	 */
	public void reCalProPositionPnl() {
		TradePositionExample example = new TradePositionExample();
		example.createCriteria().andStatusEqualTo(PositionStatus.ACTIVE.name()).andUuidLike("PRO-%");
		List<TradePosition> allPositions = tradePositionMapper.selectByExample(example);
		StringBuilder sqlBuilder = new StringBuilder("update trade_position set pnl = tmp.pnl from (values");
		List<Object> params = new ArrayList<>();
		int num = 0;
		for (TradePosition proPosition : allPositions) {
			Long id = proPosition.getId();
			String quote = CommonUtils.coinPair(proPosition.getSymbol()).getSecond();
			if (!Constants.BASE_COIN.equals(quote)) {
				BigDecimal usdPnl = BigDecimal.ZERO;
				if (PositionStatus.ACTIVE.name().equals(proPosition.getStatus())) {
					BigDecimal pnl = proPosition.getPnl();
					if (null == pnl || BigDecimal.ZERO.compareTo(pnl) == 0) {
						continue;
					}
					BigDecimal midPrice = SymbolDomain.nonNullGet(quote + Constants.BASE_QUOTE).midPrice();
					usdPnl = pnl.multiply(midPrice);
				}
				sqlBuilder.append("(?, ?),");
				params.add(id);
				params.add(usdPnl);
				num ++;
			}
		}
		if (params.isEmpty()) {
			return;
		}
		sqlBuilder.replace(sqlBuilder.length() - 1, sqlBuilder.length(), ")");
		sqlBuilder.append(" as tmp (id, pnl) where trade_position.id = tmp.id");
		String sql = sqlBuilder.toString();
		int updateRows = template.update(sql, params.toArray());
		log.info("rewrite pro position pnl, expect = {}, actual = {}", num, updateRows);
	}
}
