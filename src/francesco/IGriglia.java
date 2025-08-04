package francesco;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import nicolas.StatoCella;
import processing.data.JSONArray;
import processing.data.JSONObject;
import utils.Utils;

public interface IGriglia<C extends ICella> {
	
	
	C getCellaAt(int x, int y);
	
	default C getCellaAt(IHave2DCoordinate c) {
		return getCellaAt(c.x(), c.y());
	}
	
	int width();
	int height();
	int getTipo();
	
	
	default int xmin() {return 0;}
	default int ymin() {return 0;}
	default int xmax() {return xmin()+width()-1;}
	default int ymax() {return ymin()+height()-1;}
	
	
	IGriglia<C> addObstacle(IObstacle obstacle, int tipoOstacolo);
	
	
	
	default boolean isNavigabile(int x, int y) {
		return getCellaAt(x,y).isNot(StatoCella.OSTACOLO);
	}
	
	
	default void forEach(BiConsumer<Integer,Integer> action) {
		for (int i=0, h=height(), w=width(), xmin=xmin(), ymin=ymin(); i<h; i++) {
			for (int j=0; j<w; j++) {
				action.accept(j+xmin, i+ymin);
			}
		}
	}
	
	default String matriceDiStatiToString() {
		var sb = new StringBuilder();
		int w = width()-1;
		forEach((x,y) -> {
			sb.append(String.format("%2x|", getCellaAt(x, y).stato()));
			if (x==w) sb.append('\n');
		});
		return sb.toString();
	}
	
	default void print() {
		System.out.println(matriceDiStatiToString());
		System.out.println();
	}
	
	default <T,A,R> R collect(Function<? super C,? extends T> mapper,
			Collector<? super T,?,? extends A> rowCollector,
			Collector<? super A,?,? extends R> collector) {
		int xmin=xmin(), ymin=ymin();
		return IntStream.range(0, height())
		.mapToObj(i -> IntStream.range(0, width())
			.mapToObj(j -> mapper.apply(getCellaAt(j+xmin, i+ymin)))
			.collect(rowCollector)
		).collect(collector);
	}
	
	default JSONArray toJSON(Function<C,JSONObject> serializer) {
		return collect(serializer, 
			Utils.collectToJSONArray(JSONArray::append), 
			Utils.collectToJSONArray(JSONArray::append));
	}
	
	default IGrigliaMutabile<?> toGrigliaMutabile() {
		return GrigliaMatrix.from(this);
	}
	
}
