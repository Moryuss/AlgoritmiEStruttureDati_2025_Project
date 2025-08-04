package nicolas;

import java.util.Optional;
import francesco.ICella;
import francesco.IGriglia;
import francesco.IHave2DCoordinate;
import francesco.IObstacle;

public record GrigliaConRegioni<C extends ICella>(IGriglia<C> oldGriglia, int[] mapper, Regione[] regioni) implements IGriglia<C> {
	
	@Override
	public C getCellaAt(int x, int y) {return oldGriglia.getCellaAt(x, y);}
	@Override
	public int width() {return oldGriglia.width();}
	@Override
	public int height() {return oldGriglia.height();}
	@Override
	public int getTipo() {return oldGriglia.getTipo();}
	@Override
	public IGriglia<C> addObstacle(IObstacle obstacle, int tipoOstacolo) {return oldGriglia.addObstacle(obstacle, tipoOstacolo);}
	
	public Optional<Integer> getRegioneIndexContenente(IHave2DCoordinate c) {
		return getRegioneIndexContenente(c.x(), c.y());
	}
	
	public Optional<Integer> getRegioneIndexContenente(int x, int y) {
		var index = mapper[y*width()+x];
		if (index<0) return Optional.empty();
		return Optional.of(index);
	}
	public Optional<Regione> getRegioneContenente(IHave2DCoordinate x) {
		return getRegioneContenente(x.x(), x.y());
	}
	
	public Optional<Regione> getRegioneContenente(int x, int y) {
		var index = mapper[y*width()+x];
		if (index<0) return Optional.empty();
		return Optional.of(regioni[index]);
	}
	
	
}
