package nicolas;

import francesco.IGriglia;
import francesco.IHave2DCoordinate;

public interface ICompitoDue {
	
	IGrigliaConOrigine calcola(IGriglia<?> griglia, int x, int y);
	
	default IGrigliaConOrigine calcola(IGriglia<?> griglia, IHave2DCoordinate c) {
		return calcola(griglia, c.x(), c.y());
	}
	
}
