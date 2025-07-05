package matteo;

import francesco.ICella2D;
import francesco.IGriglia;

public interface ICompitoTre {
	
	ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D);
	
}
