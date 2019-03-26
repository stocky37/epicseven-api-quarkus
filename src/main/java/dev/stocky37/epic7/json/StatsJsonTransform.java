package dev.stocky37.epic7.json;

import dev.stocky37.epic7.core.Stat;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.function.Function;

public class StatsJsonTransform implements Function<Map<Stat, BigDecimal>, JsonObject> {
	private static StatsJsonTransform instance = new StatsJsonTransform();

	public static StatsJsonTransform instance() {
		return instance;
	}

	private StatsJsonTransform() {}

	@Override
	public JsonObject apply(Map<Stat, BigDecimal> m) {
		final JsonObjectBuilder builder = Json.createObjectBuilder();
		m.forEach((key, value) -> builder.add(key.getId(), value.setScale(2, RoundingMode.HALF_UP)));
		return builder.build();
	}
}
