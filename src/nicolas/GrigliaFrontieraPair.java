package nicolas;

import java.util.stream.Stream;

import francesco.IGriglia;

public record GrigliaFrontieraPair(IGriglia<?> griglia, Stream<ICellaConDistanze> frontiera) {
	
}