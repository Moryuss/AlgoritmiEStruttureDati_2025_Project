package matteo.Strategies.Cache;

import francesco.ICella2D;
import francesco.IGriglia;
import matteo.ICammino;

public interface CacheStrategy {
	 ICammino get(IGriglia<?> griglia, ICella2D O, ICella2D D);
	    void put(IGriglia<?> griglia, ICella2D O, ICella2D D, ICammino cammino);
}
