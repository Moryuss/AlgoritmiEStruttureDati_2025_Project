package nicolas;

import java.util.Optional;
import francesco.GrigliaMatrix;
import francesco.ICella;
import francesco.IGriglia;
import francesco.IObstacle;
import francesco.implementazioni.Cella;
import utils.BiHashmap;

public record GrigliaBiHashmap(BiHashmap<Integer,Integer> map, int width, int height, int xmin, int ymin, int tipoGriglia) implements IGriglia<ICella> {
	
	@Override
	public ICella getCellaAt(int x, int y) {
		try {
			return new Cella(map.get(y, x));
		} catch(Exception ex) {
			throw new IllegalArgumentException("x%dy%d".formatted(x, y));
		}
	}
	
	public Optional<ICella> mayGetCellaAt(int x, int y) {
		return map.safeGet(y, x).map(Cella::new);
	}
	
	
	@Override
	public int getTipo() {
		return tipoGriglia;
	}

	@Override
	public IGriglia<ICella> addObstacle(IObstacle obstacle, int tipoOstacolo) {
		var copy = new BiHashmap<Integer,Integer>();
		map.forEach((y,m) -> {
			m.forEach((x,s) -> map.put(y, x, s));
		});
		obstacle.list().forEach(c -> {
			copy.put(c.y(), c.x(), StatoCella.OSTACOLO.value());
		});
		return new GrigliaBiHashmap(copy, width, height, xmin, ymin, this.tipoGriglia|tipoOstacolo);
	}
	
	
	
	public static IGriglia<?> from(GrigliaMatrix griglia) {
		var map = new BiHashmap<Integer,Integer>();
		griglia.forEach((x,y) -> {
			map.put(y, x, griglia.getCellaAt(x, y).stato());
		});
		return new GrigliaBiHashmap(map, griglia.width(), griglia.height(), 0, 0, griglia.getTipo());
	}
	
	
	public static IGriglia<?> creaSottoGriglia(IGriglia<?> griglia, Regione regione) {
		int width=regione.maxWidth(), height=regione.maxHeight();
		int xmin=regione.xmin, ymin=regione.ymin;
		var map = new BiHashmap<Integer,Integer>();
		
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				map.put(i, j, griglia.getCellaAt(j+xmin, i+ymin).stato());
			}
		}
		
		return new GrigliaBiHashmap(map, width, height, xmin, ymin, griglia.getTipo());
	}
	
}
