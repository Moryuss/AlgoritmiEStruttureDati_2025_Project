package francesco;

import java.util.List;
import java.util.function.BiConsumer;

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
		// TODO
		ICella[][] mat = new ICella[height()][width()];
		obstacle.list().forEach(c -> {
			//mat[c.y()][c.x()] |= ostacolo;
		});
		//return new GrigliaMatrix(mat);
		throw new UnsupportedOperationException();
	}
	
	
	public static IGriglia<ICella> from(int width, int height, List<IObstacle> ostacoli) {
		throw new UnsupportedOperationException();
	}
	
}
