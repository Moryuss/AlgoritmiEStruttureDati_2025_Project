package nicolas;

import java.util.stream.Stream;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IObstacle;
import processing.data.JSONArray;

public interface IGrigliaConOrigine extends IGriglia<ICella2D> {
	
	ICella2D getOrigine();
	
	Stream<ICella2D> getFrontiera();
	
	IObstacle convertiChiusuraInOstacolo();
	
	JSONArray toJSON();
	
}