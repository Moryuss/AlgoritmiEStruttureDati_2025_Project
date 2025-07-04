package nicolas;

import java.util.stream.Stream;
import francesco.IGriglia;
import francesco.IObstacle;
import francesco.StatoCella;

public interface IGrigliaConOrigine extends IGriglia<ICella2> {
	
	ICella2 getOrigine();
	
	default boolean isInContesto(int x, int y) {
		return StatoCella.CONTESTO.is(getCellaAt(x, y).stato());
	}
	
	
	default boolean isInComplemento(int x, int y) {
		return StatoCella.COMPLEMENTO.is(getCellaAt(x, y).stato());
	}
	
	default boolean isInChiusura(int x, int y) {
		return isInContesto(x,y) || isInComplemento(x,y);
	}
	
	
	default boolean isInFrontiera(int x, int y) {
		return StatoCella.FRONTIERA.is(getCellaAt(x, y).stato());
	}
	
	// se (xd,yd) è nella chiusura restitiusco la distanza
	// altrimenti Double.infinity;
	double distanzaLiberaDa(int xd, int yd);
	
	Stream<ICella2> getFrontiera();
	
	IObstacle convertiChiusuraInOstacolo();
	
}
