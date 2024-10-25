package com.google.backend.trading.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author trading
 * @date 2021/11/4 15:44
 */
@Data
@Component
@ConfigurationProperties(prefix = "trade")
public class TradeProperties {

	private Traders traders = new Traders();

	private IDK idk = new IDK();

	private Loop loop = new Loop();

	private Task task = new Task();

	private Risk risk = new Risk();

	private HttpTrafficDisabledControl trafficDisabledControl = new HttpTrafficDisabledControl();

	private Sensors sensors = new Sensors();

	private Margin margin = new Margin();

	@Data
	public static class Traders {

		private List<String> uidArr = Collections.emptyList();

	}

	@Data
	public static class IDK {

		private List<String> uidArr = Collections.emptyList();

	}

	@Data
	public static class Loop {

		private boolean enabled = true;

	}

	@Data
	public static class Task {

		private boolean enabled = true;

	}

	@Data
	public static class Risk {

		private boolean enabled = true;

	}

	@Data
	public static class HttpTrafficDisabledControl {

		private boolean enabled = false;
	}

	@Data
	public static class Sensors {

		private String host;

		private String logPath;
	}

	@Data
	public static class Margin {

		private boolean ipCompliance = true;

	}
}
