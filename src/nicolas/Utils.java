package nicolas;

import static java.lang.Math.*;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public final class Utils {
	
	private Utils() {}

	
	public static final double sqrt2 = Math.sqrt(2);
	
	
	
	
	
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
	
}
