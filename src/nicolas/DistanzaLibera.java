package nicolas;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import francesco.IHave2DCoordinate;
import utils.Utils;

public record DistanzaLibera(int distanzaTorre, int distanzaAlfiere, double distanza) implements Comparable<DistanzaLibera> {
	
	public static final DistanzaLibera ZERO = new DistanzaLibera(0, 0, 0);
	public static final DistanzaLibera INFINITY = new DistanzaLibera(Integer.MAX_VALUE, Integer.MAX_VALUE, Double.POSITIVE_INFINITY);
	
	
	public DistanzaLibera(int distanzaTorre, int distanzaAlfiere) {
		this(distanzaTorre, distanzaAlfiere, distanzaAlfiere*Utils.sqrt2+distanzaTorre);
	}
	
	
	@Override
	public final boolean equals(Object obj) {
		return obj instanceof DistanzaLibera other
		&& distanzaTorre == other.distanzaTorre
		&& distanzaAlfiere == other.distanzaAlfiere;
	}
	
	@Override
	public final String toString() {
		return isInfinity() ? "+∞" : "%d+%d√2=%f".formatted(distanzaTorre, distanzaAlfiere, distanza);
	}
	
	@Override
	public int compareTo(DistanzaLibera o) {
		return Double.compare(distanza, o.distanza);
	}
	
	
	public boolean isInfinity() {
		return Double.isInfinite(distanza);
	}
	
	
	public DistanzaLibera add(DistanzaLibera other) {
		return new DistanzaLibera(
				distanzaTorre+other.distanzaTorre,
				distanzaAlfiere+other.distanzaAlfiere,
				distanza+other.distanza
		);
	}
	
	
	
	
	public static DistanzaLibera from(IHave2DCoordinate a, IHave2DCoordinate b) {
		return from(a.x(), a.y(), b.x(), b.y());
	}
	public static DistanzaLibera from(int x1, int y1, int x2, int y2) {
		var Dx = abs(x1-x2);
		var Dy = abs(y1-y2);
		var d2 = min(Dx, Dy);
		var d1 = max(Dx, Dy)-d2;
		return new DistanzaLibera(d1, d2);
	}
	
}
