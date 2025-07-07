package nicolas;

import java.util.stream.Stream;
import francesco.IGriglia;
import francesco.IObstacle;
import processing.data.JSONArray;

public interface IGrigliaConOrigine extends IGriglia<ICella2> {
	
	ICella2 getOrigine();
	
	default boolean isInContesto(int x, int y) {
		return getCellaAt(x, y).is(StatoCella.CONTESTO);
	}
	
	
	default boolean isInComplemento(int x, int y) {
		return getCellaAt(x, y).is(StatoCella.COMPLEMENTO);
	}
	
	default boolean isInChiusura(int x, int y) {
		return getCellaAt(x, y).is(StatoCella.CHIUSURA);
	}
	
	
	default boolean isInFrontiera(int x, int y) {
		return getCellaAt(x, y).is(StatoCella.FRONTIERA);
	}
	
	// se (xd,yd) è nella chiusura restitiusco la distanza
	// altrimenti Double.infinity;
	double distanzaLiberaDa(int xd, int yd);
	
	Stream<ICella2> getFrontiera();
	
	IObstacle convertiChiusuraInOstacolo();
	
	JSONArray toJSON();
	
}
