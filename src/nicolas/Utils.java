package nicolas;

import static java.lang.Math.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import francesco.GrigliaMatrix;
import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IHave2DCoordinate;
import francesco.implementazioni.Cella;
import francesco.implementazioni.Cella2D;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

public final class Utils {
	
	private Utils() {}

	
	public static final double sqrt2 = Math.sqrt(2);
	
	
	
	
	
	public static Optional<Integer> parseHex(String str) {
		if (str.startsWith("0x")) str = str.substring(2);
		
		try {
			return Optional.of(Integer.parseUnsignedInt(str, 16));
		} catch(Exception ex) {
			return Optional.empty();
		}
		
	}
	
	
	
	public static int[][] sameSizeOf(int[][] m) {
		return new int[m.length][m[0].length];
	}
	
	public static int[][] copy(int[][] m) {
		var r = sameSizeOf(m);
		for (int i = 0; i < r.length; i++) {
			for (int j = 0; j < r[0].length; j++) {
				r[i][j] = m[i][j];
			}
		}
		return r;
	}
	
	
	public static double distanzaLiberaTra(int x1, int y1, int x2, int y2) {
		var Dx = abs(x1-x2);
		var Dy = abs(y1-y2);
		var d2 = min(Dx, Dy);
		var d1 = max(Dx, Dy)-d2;
		return d1 + d2*sqrt2;
	}
	
	public static double distanzaLiberaTra(IHave2DCoordinate a, IHave2DCoordinate b) {
		return distanzaLiberaTra(a.x(), a.y(), b.x(), b.y());
	}
	
	
	
	public static <T> Collector<T,?,JSONArray> collectToJSONArray(BiFunction<JSONArray,T,JSONArray> appender) {
		class C {
			JSONArray jsona = new JSONArray();
			C accept(T t) {
				jsona = appender.apply(jsona, t);
				return this;
			}
			C merge(C other) {
				throw new UnsupportedOperationException();
			}
			JSONArray finish() {
				return jsona;
			}
		}
		return Collector.of(C::new, C::accept, C::merge, C::finish);
	}
	
	public static Collector<JSONObject,?,JSONArray> collectToJSONArray() {
		return collectToJSONArray(JSONArray::append);
	}
	
	
	public static IGriglia<ICella> loadSimple(File file) {
		var jsona = PApplet.loadJSONArray(file);
		int height = jsona.size();
		int width = jsona.getJSONArray(0).size();
		var list = new ArrayList<ICella2D>();
		
		for (int i=0; i<height; i++) {
			var row = jsona.getJSONArray(i);
			for (int j=0; j<width; j++) {
				if (row.getInt(j,0)>0) {
					list.add(new Cella2D(StatoCella.OSTACOLO.value(), j, i));
				}
			}
		}
		
		return GrigliaMatrix.from(width, height, List.of(()->list));
	}
	
	public static IGriglia<ICella> loadIntJSON(File file, IntFunction<ICella> deserialzier) {
		var jsona = PApplet.loadJSONArray(file);
		int height = jsona.size();
		int width = jsona.getJSONArray(0).size();
		ICella[][] mat = new Cella[height][width];
		
		for (int i=0; i<height; i++) {
			var row = jsona.getJSONArray(i);
			for (int j=0; j<width; j++) {
				mat[i][j] = deserialzier.apply(row.getInt(j,0));
			}
		}
		
		return new GrigliaMatrix(mat);
	}

	public static IGriglia<ICella> loadJSON(File file, Function<JSONObject,ICella> deserialzier) {
		var jsona = PApplet.loadJSONArray(file);
		int height = jsona.size();
		int width = jsona.getJSONArray(0).size();
		ICella[][] mat = new Cella[height][width];
		
		for (int i=0; i<height; i++) {
			var row = jsona.getJSONArray(i);
			for (int j=0; j<width; j++) {
				mat[i][j] = deserialzier.apply(row.getJSONObject(j));
			}
		}
		
		return new GrigliaMatrix(mat);
	}
	
}
