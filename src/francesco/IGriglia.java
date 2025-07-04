package francesco;

public interface IGriglia<C extends ICella> {
	// true se in (x,y) c'è un'ostacolo 
	boolean isNavigabile(int x, int y);
	
	C getCellaAt(int x, int y);
	
	// larghezza
	int width();
	// altezza
	int height();
	
	IGriglia<C> addObstacle(IObstacle obstacle);
	
}