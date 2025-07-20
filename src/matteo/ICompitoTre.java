package matteo;

import francesco.IGriglia;
import nicolas.ICella2;

public interface ICompitoTre extends IHasReport{
	
	ICammino camminoMin(IGriglia<?> griglia, ICella2 O, ICella2 D);
	
}
