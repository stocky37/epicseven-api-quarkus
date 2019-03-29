package dev.stocky37.epic7.core;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import dev.stocky37.epic7.json.EquipInput;
import dev.stocky37.epic7.json.Hero;
import dev.stocky37.epic7.json.StatsJsonTransform;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApplicationScoped
public class HeroService {
	@Inject
	@Named("cache.lists")
	AsyncCache<String, JsonArray> listsCache;

	@Inject
	@Named("cache.hero")
	AsyncLoadingCache<String, JsonObject> heroCache;

	@Inject
	@Named("cache.heroes.lookup")
	Function<String, JsonArray> heroesLookup;

	public JsonArray getHeroes() {
		return listsCache.synchronous().get("heroes", heroesLookup);
	}

	public JsonObject getHero(final String id) {
		return heroCache.synchronous().get(id);
	}

	public Map<Stat, BigDecimal> getAwakenedStats(final String id, int stars, int level, int awakening) {
		return getLevelledHero(id, stars, level, awakening).getAwakenedBaseStats();
	}

	public JsonObject equipHero(String id, EquipInput in) {
		final LevelledHero hero = getLevelledHero(id, in.getStars(), in.getLevel(), in.getAwakening());
		return Json.createObjectBuilder()
			.add("stats", stats(hero.getAwakenedBaseStats().apply(in.getGearStats())))
			.add("gearSets", sets(in.getCompletedGearSets()))
			.build();
	}

	private LevelledHero getLevelledHero(final String id, int stars, int level, int awakening) {
		return new LevelledHero(new Hero(getHero(id)), level, stars, awakening);
	}

	private JsonObject stats(Map<Stat, BigDecimal> stats) {
		return StatsJsonTransform.instance().apply(stats);
	}

	private JsonArray sets(List<GearSet> sets) {
		final JsonArrayBuilder builder = Json.createArrayBuilder();
		sets.forEach(set -> builder.add(set.getId()));
		return builder.build();
	}
}