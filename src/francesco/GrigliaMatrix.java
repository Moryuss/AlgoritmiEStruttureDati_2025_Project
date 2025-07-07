package francesco;

import java.util.List;

import francesco.implementazioni.Cella;
import nicolas.StatoCella;

public record GrigliaMatrix(ICella[][] mat) implements IGriglia<ICella> {
	
	@Override
	public boolean isNavigabile(int x, int y) {
		return StatoCella.OSTACOLO.isNot(mat[y][x]);
	}
	
	@Override
	public ICella getCellaAt(int x, int y) {
		return mat[y][x];
	}
	
	@Override
	public int width() {
		return mat[0].length;
	}
	
	@Override
	public int height() {
		return mat.length;
	}
	
	@Override
	public IGriglia<ICella> addObstacle(IObstacle obstacle) {
		obstacle.list().forEach(c -> {
			mat()[c.y()][c.x()].setStato(mat()[c.y()][c.x()].stato() | c.stato());
		});
		return new GrigliaMatrix(mat);
	}
	
	
	public static IGriglia<ICella> from(int width, int height, List<IObstacle> ostacoli) {
		ICella[][] mat = inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat);
		for(IObstacle o : ostacoli) {
			griglia = griglia.addObstacle(o);
		}
		return griglia;
	}
	
	public static Cella[][] inizializzaMatrice(int width, int height) {
		Cella[][] mat = new Cella[height][width];
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
//				mat[i][j] = new Cella(StatoCella.VUOTA.value()); 
				mat[i][j] = new Cella(0); 
			}
		}
		return mat;
	}
}
