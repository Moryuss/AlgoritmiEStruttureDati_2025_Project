package matteo.Strategies.Cache;

import java.util.Optional;

import francesco.ICella2D;
import francesco.IGriglia;
import matteo.CamminoCache;
import matteo.ICammino;

public sealed interface CacheStrategy {
	Optional<ICammino> get(IGriglia<?> griglia, ICella2D O, ICella2D D);
	void put(IGriglia<?> griglia, ICella2D O, ICella2D D, ICammino cammino);


	public static record CacheNull() implements CacheStrategy {
		@Override
		public Optional<ICammino> get(IGriglia<?> griglia, ICella2D O, ICella2D D) {
			// no-op
			return Optional.empty();
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
		public Optional<ICammino> get(IGriglia<?> griglia, ICella2D O, ICella2D D) {
			return cache.getCammino(griglia, O, D);
		}
		@Override
		public void put(IGriglia<?> griglia, ICella2D O, ICella2D D, ICammino cammino) {
			cache.putCammino(griglia, O, D, cammino);
		}
	}
}

