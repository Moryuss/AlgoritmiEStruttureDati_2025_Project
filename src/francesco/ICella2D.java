package francesco;

import francesco.implementazioni.Cella2D;

public interface ICella2D extends ICella,IHave2DCoordinate {
	
	public static ICella2D of(int x, int y, int stato) {
		return new Cella2D(stato, x, y);
	}
	
}