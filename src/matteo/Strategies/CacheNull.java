package matteo.Strategies;

import francesco.ICella2D;
import francesco.IGriglia;
import matteo.ICammino;

public class CacheNull implements CacheStrategy {

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
