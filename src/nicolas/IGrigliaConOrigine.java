package nicolas;

import java.util.stream.Stream;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IObstacle;
import processing.data.JSONArray;

public interface IGrigliaConOrigine extends IGriglia<ICellaConDistanze> {
	
	ICella2D getOrigine();
	
	Stream<ICellaConDistanze> getFrontiera();
	
	IObstacle convertiChiusuraInOstacolo();
	
	JSONArray toJSON();
	
}