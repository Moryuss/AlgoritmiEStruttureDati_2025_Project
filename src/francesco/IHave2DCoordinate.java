package francesco;

public interface IHave2DCoordinate {
	
	int x();
	int y();
	
	default boolean sameCoordinateAs(IHave2DCoordinate other) {
		return x()==other.x() && y()==other.y();
	}
	
	default String coordinateToString() {
		return "(%d,%d)".formatted(x(), y());
	}
	
}