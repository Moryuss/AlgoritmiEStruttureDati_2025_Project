package nicolas;

import java.util.stream.Stream;
import francesco.ICella2D;
import francesco.IGriglia;

public record GrigliaFrontieraPair(IGriglia<?> griglia, Stream<ICella2D> frontiera) {
	
}