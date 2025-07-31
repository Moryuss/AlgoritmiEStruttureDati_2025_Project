package matteo;

import francesco.ICella2D;
import francesco.IGriglia;
import nicolas.CompitoDueImpl;
import nicolas.ICompitoDue;

public interface ICompitoTre extends IHasReport, IInterrompibile{
	
	ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D, ICompitoDue compitoDue);
	
	
	default ICammino camminoMin(IGriglia<?> griglia, ICella2D O, ICella2D D) {
		return camminoMin(griglia, O, D, CompitoDueImpl.V0);
	}
	
}
