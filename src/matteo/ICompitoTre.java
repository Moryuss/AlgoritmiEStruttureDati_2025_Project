package matteo;

import francesco.ICella2D;
import francesco.IGriglia;
import nicolas.ICompitoDue;

public interface ICompitoTre extends IHasReport{
	
	ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D);
	ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D, ICompitoDue compitoDue);
}
