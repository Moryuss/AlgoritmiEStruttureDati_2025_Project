package nicolas;

import francesco.ICella2D;

public interface ICella2 extends ICella2D {
	
	int distanzaTorre();
	int distanzaAlfiere();
	
	default double distanzaDaOrigine() {
		return distanzaTorre() + Utils.sqrt2*distanzaAlfiere();
	}
	
	default boolean isUnreachable() {
		return Double.isInfinite(distanzaDaOrigine());
	}
	
	
	public static ICella2 of(int x, int y, int stato, int d1, int d2) {
		return new Cella2(x, y, stato, d1, d2);
	}
	
}
