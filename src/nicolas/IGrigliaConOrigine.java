package nicolas;

import java.util.stream.Stream;
import francesco.IGriglia;
import francesco.IObstacle;
import processing.data.JSONArray;

public interface IGrigliaConOrigine extends IGriglia<ICellaConDistanze> {
	
	ICellaConDistanze getOrigine();
	
	
	default boolean isInContesto(int x, int y) {
		return getCellaAt(x, y).is(StatoCella.CONTESTO);
	}
	
	default boolean isInComplemento(int x, int y) {
		return getCellaAt(x, y).is(StatoCella.COMPLEMENTO);
	}
	
	default boolean isInChiusura(int x, int y) {
		return StatoCella.CHIUSURA.check(getCellaAt(x, y).stato());
	}
	
	default boolean isInFrontiera(int x, int y) {
		return getCellaAt(x, y).is(StatoCella.FRONTIERA);
	}
	
	
	double distanzaLiberaDa(int xd, int yd);
	
	Stream<ICellaConDistanze> getFrontiera();
	
	IObstacle convertiChiusuraInOstacolo();
	
	JSONArray toJSON();
	
}
