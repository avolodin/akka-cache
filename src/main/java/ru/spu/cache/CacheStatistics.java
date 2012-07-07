package ru.spu.cache;

import java.util.Date;

public class CacheStatistics implements javax.cache.CacheStatistics {

	protected long cachePuts = 0L;
	protected long cacheGets = 0L;
	protected long cacheHits = 0L;
	protected long cacheMisses = 0L;
	protected long cacheRemovals = 0L;
	protected long cacheEvictions = 0L;

	protected long totalGetMillis = 0L;
	protected long totalPutMillis = 0L;
	protected long totalRemovalsMillis = 0L;

	Date accumulationDate = new Date();

	@Override
	public void clearStatistics() {
		cachePuts = 0L;
		cacheGets = 0L;
		cacheHits = 0L;
		cacheMisses = 0L;
		cacheRemovals = 0L;
		cacheEvictions = 0L;
		totalGetMillis = 0L;
		totalPutMillis = 0L;
		totalRemovalsMillis = 0L;
		accumulationDate = new Date();
	}

	@Override
	public Date getStartAccumulationDate() {
		return accumulationDate;
	}

	@Override
	public long getCacheHits() {
		return cacheHits;
	}

	@Override
	public float getCacheHitPercentage() {
		if (cacheGets == 0) {
			return 0;
		}
		return (cacheHits / cacheGets) * 100;
	}

	@Override
	public long getCacheMisses() {
		return cacheMisses;
	}

	@Override
	public float getCacheMissPercentage() {
		if (cacheGets == 0) {
			return 0;
		}
		return (cacheMisses / cacheGets) * 100;
	}

	@Override
	public long getCacheGets() {
		return cacheGets;
	}

	@Override
	public long getCachePuts() {
		return cachePuts;
	}

	@Override
	public long getCacheRemovals() {
		return cacheRemovals;
	}

	@Override
	public long getCacheEvictions() {
		return cacheEvictions;
	}

	@Override
	public float getAverageGetMillis() {
		if (cacheGets == 0) {
			return 0;
		}
		return (totalGetMillis / cacheGets);
	}

	@Override
	public float getAveragePutMillis() {
		if (cachePuts == 0) {
			return 0;
		}
		return (totalPutMillis / cachePuts);
	}

	@Override
	public float getAverageRemoveMillis() {
		if (cacheRemovals == 0) {
			return 0;
		}
		return (totalRemovalsMillis / cacheRemovals);
	}

	public long getTotalGetMillis() {
		return totalGetMillis;
	}

	public long getTotalPutMillis() {
		return totalPutMillis;
	}

	public long getTotalRemovalsMillis() {
		return totalRemovalsMillis;
	}

	public void aggregate(CacheStatistics statistics) {
		cachePuts += statistics.getCachePuts();
		cacheGets += statistics.getCacheGets();
		cacheHits += statistics.getCacheHits();
		cacheMisses += statistics.getCacheMisses();
		cacheRemovals += statistics.getCacheRemovals();
		cacheEvictions += statistics.getCacheEvictions();

		totalGetMillis += statistics.getTotalGetMillis();
		totalPutMillis += statistics.getTotalPutMillis();
		totalRemovalsMillis += statistics.getTotalRemovalsMillis();
	}

}
