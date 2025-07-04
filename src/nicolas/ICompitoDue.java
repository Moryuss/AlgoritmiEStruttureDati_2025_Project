package nicolas;

import francesco.ICella;
import francesco.IGriglia;

public interface ICompitoDue {
	
	IGrigliaConOrigine calcola(IGriglia<?> griglia, ICella O);
	
}
