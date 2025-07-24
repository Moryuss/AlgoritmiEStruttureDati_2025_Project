package francesco;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import nicolas.StatoCella;
import nicolas.Utils;
import processing.data.JSONArray;
import processing.data.JSONObject;

public interface IGriglia<C extends ICella> {
	
	default boolean isNavigabile(int x, int y) {
		return getCellaAt(x,y).isNot(StatoCella.OSTACOLO);
	}
	
	C getCellaAt(int x, int y);
	
	default C getCellaAt(IHave2DCoordinate c) {
		return getCellaAt(c.x(), c.y());
	}
	
	int width();
	
	int height();
	
	int getTipo();
	
	default IGriglia<ICella> aggiungiTipo(int statoDaAggiungere){
		return null;
	}
	
	IGriglia<C> addObstacle(IObstacle obstacle);
	
	
	default void forEach(BiConsumer<Integer,Integer> action) {
		for (int i=0, h=height(), w=width(); i<h; i++) {
			for (int j=0; j<w; j++) {
				action.accept(j, i);
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
		return IntStream.range(0, height())
		.mapToObj(i -> IntStream.range(0, width())
			.mapToObj(j -> mapper.apply(getCellaAt(j, i)))
			.collect(rowCollector)
		).collect(collector);
	}
	
	default JSONArray toJSON(Function<C,JSONObject> serializer) {
		return collect(serializer, 
			Utils.collectToJSONArray(JSONArray::append), 
			Utils.collectToJSONArray(JSONArray::append));
	}
	
}
