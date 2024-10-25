package com.google.backend.trading.component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trading
 * @date 2021/10/9 19:49
 */
@ApiModel(value = "时区实体")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public enum TimeZone {
	/**
	 * 时区字典 id是唯一标识，zoneOffsetSeconds 为秒级别offset， zoneRegion 为对应的地区名称
	 */
	UTC_NE_1200_International("(UTC-12:00) International Date Line West", -43200, "International Date Line West"),
	UTC_NE_1100_Coordinated("(UTC-11:00) Coordinated Universal Time-11", -39600, "Coordinated Universal Time-11"),
	UTC_NE_1000_Hawaii("(UTC-10:00) Hawaii", -36000, "Hawaii"),
	UTC_NE_0900_Alaska("(UTC-09:00) Alaska", -32400, "Alaska"),
	UTC_NE_0800_Baja("(UTC-08:00) Baja California", -28800, "Baja California"),
	UTC_NE_0700_Pacific("(UTC-07:00) Pacific Time (US & Canada)", -25200, "Pacific Time (US & Canada)"),
	UTC_NE_0800_Pacific("(UTC-08:00) Pacific Time (US & Canada)", -28800, "Pacific Time (US & Canada)"),
	UTC_NE_0700_Arizona("(UTC-07:00) Arizona", -25200, "Arizona"),
	UTC_NE_0700_Chihuahua("(UTC-07:00) Chihuahua, La Paz, Mazatlan", -25200, "Chihuahua, La Paz, Mazatlan"),
	UTC_NE_0700_Mountain("(UTC-07:00) Mountain Time (US & Canada)", -25200, "Mountain Time (US & Canada)"),
	UTC_NE_0600_Central_America("(UTC-06:00) Central America", -21600, "Central America"),
	UTC_NE_0600_Central("(UTC-06:00) Central Time (US & Canada)", -21600, "Central Time (US & Canada)"),
	UTC_NE_0600_Guadalajara("(UTC-06:00) Guadalajara, Mexico City, Monterrey", -21600, "Guadalajara, Mexico City, Monterrey"),
	UTC_NE_0600_Saskatchewan("(UTC-06:00) Saskatchewan", -21600, "Saskatchewan"),
	UTC_NE_0500_Bogota("(UTC-05:00) Bogota, Lima, Quito", -18000, "Bogota, Lima, Quito"),
	UTC_NE_0500_Eastern("(UTC-05:00) Eastern Time (US & Canada)", -18000, "Eastern Time (US & Canada)"),
	UTC_NE_0500_Indiana("(UTC-05:00) Indiana (East)", -18000, "Indiana (East)"),
	UTC_NE_0430_Caracas("(UTC-04:30) Caracas", -16200, "Caracas"),
	UTC_NE_0400_Asuncion("(UTC-04:00) Asuncion", -14400, "Asuncion"),
	UTC_NE_0400_Atlantic("(UTC-04:00) Atlantic Time (Canada)", -14400, "Atlantic Time (Canada)"),
	UTC_NE_0400_Cuiaba("(UTC-04:00) Cuiaba", -14400, "Cuiaba"),
	UTC_NE_0400_Georgetown("(UTC-04:00) Georgetown, La Paz, Manaus, San Juan", -14400, "Georgetown, La Paz, Manaus, San Juan"),
	UTC_NE_0400_Santiago("(UTC-04:00) Santiago", -14400, "Santiago"),
	UTC_NE_0330_Newfoundland("(UTC-03:30) Newfoundland",-12600 , "Newfoundland"),
	UTC_NE_0300_Brasilia("(UTC-03:00) Brasilia", -10800, "Brasilia"),
	UTC_NE_0300_Buenos("(UTC-03:00) Buenos Aires", -10800, "Buenos Aires"),
	UTC_NE_0300_Cayenne("(UTC-03:00) Cayenne, Fortaleza", -10800, "Cayenne, Fortaleza"),
	UTC_NE_0300_Greenland("(UTC-03:00) Greenland", -10800, "Greenland"),
	UTC_NE_0300_Montevideo("(UTC-03:00) Montevideo", -10800, "Montevideo"),
	UTC_NE_0300_Salvador("(UTC-03:00) Salvador", -10800, "Salvador"),
	UTC_NE_0200_Coordinated("(UTC-02:00) Coordinated Universal Time-02", -7200, "Coordinated Universal Time-02"),
	UTC_NE_0200_Mid_Atlantic("(UTC-02:00) Mid-Atlantic - Old", -7200, "Mid-Atlantic - Old"),
	UTC_NE_0100_Azores("(UTC-01:00) Azores", -3600, "Azores"),
	UTC_NE_0100_Cape("(UTC-01:00) Cape Verde Is.", -3600, "Cape Verde Is."),
	UTC_0000_Casablanca("(UTC) Casablanca", 0, "Casablanca"),
	UTC_0000_Coordinated("(UTC) Coordinated Universal Time", 0, "Coordinated Universal Time"),
	UTC_0000_Edinburgh("(UTC) Edinburgh, London", 0, "Edinburgh, London"),
	UTC_0100_Edinburgh("(UTC+01:00) Edinburgh, London", 3600, "Edinburgh, London"),
	UTC_0000_Dublin("(UTC) Dublin, Lisbon", 0, "Dublin, Lisbon"),
	UTC_0000_Monrovia("(UTC) Monrovia, Reykjavik", 0, "Monrovia, Reykjavik"),
	UTC_0100_Amsterdam("(UTC+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna", 3600, "Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna"),
	UTC_0100_Belgrade("(UTC+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague", 3600, "Belgrade, Bratislava, Budapest, Ljubljana, Prague"),
	UTC_0100_Brussels("(UTC+01:00) Brussels, Copenhagen, Madrid, Paris", 3600, "Brussels, Copenhagen, Madrid, Paris"),
	UTC_0100_Sarajevo("(UTC+01:00) Sarajevo, Skopje, Warsaw, Zagreb", 3600, "Sarajevo, Skopje, Warsaw, Zagreb"),
	UTC_0100_West("(UTC+01:00) West Central Africa", 3600, "West Central Africa"),
	UTC_0100_Windhoek("(UTC+01:00) Windhoek", 3600, "Windhoek"),
	UTC_0200_Athens("(UTC+02:00) Athens, Bucharest", 7200, "Athens, Bucharest"),
	UTC_0200_Beirut("(UTC+02:00) Beirut", 7200, "Beirut"),
	UTC_0200_Cairo("(UTC+02:00) Cairo", 7200, "Cairo"),
	UTC_0200_Damascus("(UTC+02:00) Damascus", 7200, "Damascus"),
	UTC_0200_Europe("(UTC+02:00) E. Europe", 7200, "E. Europe"),
	UTC_0200_Harare("(UTC+02:00) Harare, Pretoria", 7200, "Harare, Pretoria"),
	UTC_0200_Helsinki("(UTC+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius", 7200, "Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius"),
	UTC_0300_Istanbul("(UTC+03:00) Istanbul", 10800, "Istanbul"),
	UTC_0200_Jerusalem("(UTC+02:00) Jerusalem", 7200, "Jerusalem"),
	UTC_0200_Tripoli("(UTC+02:00) Tripoli", 7200, "Tripoli"),
	UTC_0300_Amman("(UTC+03:00) Amman", 10800, "Amman"),
	UTC_0300_Baghdad("(UTC+03:00) Baghdad", 10800, "Baghdad"),
	UTC_0300_Kaliningrad("(UTC+03:00) Kaliningrad, Minsk", 10800, "Kaliningrad, Minsk"),
	UTC_0300_Kuwait("(UTC+03:00) Kuwait, Riyadh", 10800, "Kuwait, Riyadh"),
	UTC_0300_Nairobi("(UTC+03:00) Nairobi", 10800, "Nairobi"),
	UTC_0300_Moscow("(UTC+03:00) Moscow, St. Petersburg, Volgograd", 10800, "Moscow, St. Petersburg, Volgograd"),
	UTC_0400_Samara("(UTC+04:00) Samara, Ulyanovsk, Saratov", 14400, "Samara, Ulyanovsk, Saratov"),
	UTC_0330_Tehran("(UTC+03:30) Tehran", 12600, "Tehran"),
	UTC_0400_Abu("(UTC+04:00) Abu Dhabi, Muscat", 14400, "Abu Dhabi, Muscat"),
	UTC_0400_Baku("(UTC+04:00) Baku", 14400, "Baku"),
	UTC_0400_Port("(UTC+04:00) Port Louis", 14400, "Port Louis"),
	UTC_0400_Tbilisi("(UTC+04:00) Tbilisi", 14400, "Tbilisi"),
	UTC_0400_Yerevan("(UTC+04:00) Yerevan", 14400, "Yerevan"),
	UTC_0430_Kabul("(UTC+04:30) Kabul", 16200, "Kabul"),
	UTC_0500_Ashgabat("(UTC+05:00) Ashgabat, Tashkent", 18000, "Ashgabat, Tashkent"),
	UTC_0500_Yekaterinburg("(UTC+05:00) Yekaterinburg", 18000, "Yekaterinburg"),
	UTC_0500_Islamabad("(UTC+05:00) Islamabad, Karachi", 18000, "Islamabad, Karachi"),
	UTC_0530_Chennai("(UTC+05:30) Chennai, Kolkata, Mumbai, New Delhi", 19800, "Chennai, Kolkata, Mumbai, New Delhi"),
	UTC_0530_Sri("(UTC+05:30) Sri Jayawardenepura", 19800, "Sri Jayawardenepura"),
	UTC_0545_Kathmandu("(UTC+05:45) Kathmandu", 20700, "Kathmandu"),
	UTC_0600_Astana("(UTC+06:00) Astana", 21600, "Astana"),
	UTC_0600_Dhaka("(UTC+06:00) Dhaka", 21600, "Dhaka"),
	UTC_0630_Yangon("(UTC+06:30) Yangon (Rangoon)", 23400, "Yangon (Rangoon)"),
	UTC_0700_Bangkok("(UTC+07:00) Bangkok, Hanoi, Jakarta", 25200, "Bangkok, Hanoi, Jakarta"),
	UTC_0700_Novosibirsk("(UTC+07:00) Novosibirsk", 25200, "Novosibirsk"),
	UTC_0800_Beijing("(UTC+08:00) Beijing, Chongqing, Hong Kong, Urumqi", 28800, "Beijing, Chongqing, Hong Kong, Urumqi"),
	UTC_0800_Krasnoyarsk("(UTC+08:00) Krasnoyarsk", 28800, "Krasnoyarsk"),
	UTC_0800_Kuala("(UTC+08:00) Kuala Lumpur, Singapore", 28800, "Kuala Lumpur, Singapore"),
	UTC_0800_Perth("(UTC+08:00) Perth", 28800, "Perth"),
	UTC_0800_Taipei("(UTC+08:00) Taipei", 28800, "Taipei"),
	UTC_0800_Ulaanbaatar("(UTC+08:00) Ulaanbaatar", 28800, "Ulaanbaatar"),
	UTC_0800_Irkutsk("(UTC+08:00) Irkutsk", 28800, "Irkutsk"),
	UTC_0900_Osaka("(UTC+09:00) Osaka, Sapporo, Tokyo", 32400, "Osaka, Sapporo, Tokyo"),
	UTC_0900_Seoul("(UTC+09:00) Seoul", 32400, "Seoul"),
	UTC_0930_Adelaide("(UTC+09:30) Adelaide", 34200, "Adelaide"),
	UTC_0930_Darwin("(UTC+09:30) Darwin", 34200, "Darwin"),
	UTC_1000_Brisbane("(UTC+10:00) Brisbane", 36000, "Brisbane"),
	UTC_1000_Canberra("(UTC+10:00) Canberra, Melbourne, Sydney", 36000, "Canberra, Melbourne, Sydney"),
	UTC_1000_Guam("(UTC+10:00) Guam, Port Moresby", 36000, "Guam, Port Moresby"),
	UTC_1000_Hobart("(UTC+10:00) Hobart", 36000, "Hobart"),
	UTC_0900_Yakutsk("(UTC+09:00) Yakutsk", 32400, "Yakutsk"),
	UTC_1100_Solomon("(UTC+11:00) Solomon Is., New Caledonia", 39600, "Solomon Is., New Caledonia"),
	UTC_1100_Vladivostok("(UTC+11:00) Vladivostok", 39600, "Vladivostok"),
	UTC_1200_Auckland("(UTC+12:00) Auckland, Wellington", 43200, "Auckland, Wellington"),
	UTC_1200_Coordinated("(UTC+12:00) Coordinated Universal Time+12", 43200, "Coordinated Universal Time+12"),
	UTC_1200_Fiji("(UTC+12:00) Fiji", 43200, "Fiji"),
	UTC_1200_Magadan("(UTC+12:00) Magadan", 43200, "Magadan"),
	UTC_1200_Petropavlovsk("(UTC+12:00) Petropavlovsk-Kamchatsky - Old", 43200, "Petropavlovsk-Kamchatsky - Old"),
	UTC_1300_Nuku("(UTC+13:00) Nuku'alofa", 46800, "Nuku'alofa"),
	UTC_1300_Samoa("(UTC+13:00) Samoa", 46800, "Samoa"),
	;

	@ApiModelProperty(value = "唯一标识", example = "(UTC+08:00) Beijing, Chongqing, Hong Kong, Urumqi")
	private final String id;

	@ApiModelProperty(value = "秒级别offset", example = "28800")
	private final Integer zoneOffsetSeconds;

	@ApiModelProperty(value = "地区名称", example = "Beijing, Chongqing, Hong Kong, Urumqi")
	private final String zoneRegion;

	private final static Map<String, TimeZone> ENUMS_CACHE;

	private final static Map<Integer, List<TimeZone>> OFFSET_ENUMS_CACHE;

	TimeZone(String id, Integer zoneOffsetSeconds, String zoneRegion) {
		this.id = id;
		this.zoneOffsetSeconds = zoneOffsetSeconds;
		this.zoneRegion = zoneRegion;
	}

	static {
		Map<String, TimeZone> enumsMap = new HashMap<>(TimeZone.values().length);
		for (TimeZone value : TimeZone.values()) {
			enumsMap.put(value.getId(), value);
		}
		ENUMS_CACHE = Collections.unmodifiableMap(enumsMap);

		HashMap<Integer, List<TimeZone>> offsetEnumsMap = new HashMap<>(TimeZone.values().length);
		for (TimeZone value : TimeZone.values()) {
			Integer newZoneOffsetSeconds = rewriteOffsetSeconds(value.getZoneOffsetSeconds());
			offsetEnumsMap.computeIfAbsent(newZoneOffsetSeconds, k -> new ArrayList<>()).add(value);
		}
		OFFSET_ENUMS_CACHE = Collections.unmodifiableMap(offsetEnumsMap);
	}

	/**
	 * 保持范围为 [-43200, 43200]
	 * @param zoneOffsetSeconds
	 * @return
	 */
	private static Integer rewriteOffsetSeconds(Integer zoneOffsetSeconds) {
		if (zoneOffsetSeconds > 43200) {
			zoneOffsetSeconds = zoneOffsetSeconds - 86400;
		} else if (zoneOffsetSeconds < -43200) {
			zoneOffsetSeconds = zoneOffsetSeconds + 86400;
		}
		return zoneOffsetSeconds;
	}

	public static TimeZone getById(String id) {
		TimeZone timeZone = ENUMS_CACHE.get(id);
		if (null == timeZone) {
			throw new RuntimeException(String.format("not found TimeZone by unknown id, id = %s", id));
		}
		return timeZone;
	}

	public static List<TimeZone> getByOffsetSeconds(Integer zoneOffsetSeconds) {
		zoneOffsetSeconds = rewriteOffsetSeconds(zoneOffsetSeconds);
		return OFFSET_ENUMS_CACHE.get(zoneOffsetSeconds);
	}

	public String getId() {
		return id;
	}

	public Integer getZoneOffsetSeconds() {
		return zoneOffsetSeconds;
	}

	public String getZoneRegion() {
		return zoneRegion;
	}
}
