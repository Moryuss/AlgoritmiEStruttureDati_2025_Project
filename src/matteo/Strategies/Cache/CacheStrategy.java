package matteo.Strategies.Cache;

import francesco.ICella2D;
import francesco.IGriglia;
import matteo.CamminoCache;
import matteo.ICammino;

public sealed interface CacheStrategy {
	ICammino get(IGriglia<?> griglia, ICella2D O, ICella2D D);
	void put(IGriglia<?> griglia, ICella2D O, ICella2D D, ICammino cammino);


	public static record CacheNull() implements CacheStrategy {
		@Override
		public ICammino get(IGriglia<?> griglia, ICella2D O, ICella2D D) {
			// no-op
			return null;
		}

		@Override
		public void put(IGriglia<?> griglia, ICella2D O, ICella2D D, ICammino cammino) {
			// no-op
		}
	}

	public static record CacheAttiva(CamminoCache cache) implements CacheStrategy {

		public CacheAttiva(CamminoCache cache) {
			this.cache = cache;
		}

		@Override
		public ICammino get(IGriglia<?> griglia, ICella2D O, ICella2D D) {
			return cache.getCammino(griglia, O, D);
		}
		@Override
		public void put(IGriglia<?> griglia, ICella2D O, ICella2D D, ICammino cammino) {
			cache.putCammino(griglia, O, D, cammino);
		}
	}
}

