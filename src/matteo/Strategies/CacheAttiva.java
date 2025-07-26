package matteo.Strategies;

import francesco.ICella2D;
import francesco.IGriglia;
import matteo.CamminoCache;
import matteo.ICammino;

public class CacheAttiva implements CacheStrategy {
	private CamminoCache cache;

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
