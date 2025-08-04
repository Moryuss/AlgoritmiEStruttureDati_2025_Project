package francesco;

public interface IGrigliaMutabile<C extends ICella> extends IGriglia<C> {
	
	void setStato(int x, int y, int s);
	
	IGrigliaMutabile<C> addObstacle(IObstacle obstacle, int tipoOstacolo);
	
}