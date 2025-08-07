package nicolas;

import static nicolas.StatoCella.*;
import java.util.List;
import java.util.stream.Stream;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IObstacle;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.Ostacolo;
import processing.data.JSONArray;
import processing.data.JSONObject;
import utils.Utils;

public record GrigliaConOrigine(int[][] mat, int Ox, int Oy, int xmin, int ymin, List<ICella2D> chiusura, ICella2D[] frontiera, int tipoGriglia) implements IGrigliaConOrigine {
	
	@Override
	public ICella2D getCellaAt(int x, int y) {
		return new Cella2D(mat[y-ymin][x-xmin], x, y);
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
	public int getTipo() {
		return tipoGriglia;
	}
	
	@Override
	public ICella2D getOrigine() {
		return getCellaAt(Ox, Oy);
	}
	
	@Override
	public Stream<ICella2D> getFrontiera() {
		return Stream.of(frontiera);
	}
	
	@Override
	public IGriglia<ICella2D> addObstacle(IObstacle obstacle, int tipoOstacolo) {
		var mat = Utils.copy(this.mat);
		for (var c : obstacle.list()) {
			mat[c.y()][c.x()] = OSTACOLO.addTo(mat[c.y()][c.x()]);
		}
		return new GrigliaConOrigine(mat, Ox, Oy, xmin, ymin, chiusura, frontiera, tipoGriglia|tipoOstacolo);
	}
	
	@Override
	public IObstacle convertiChiusuraInOstacolo() {
		return new Ostacolo(chiusura);
	}
	
	@Override
	public JSONArray toJSON() {
		return toJSON(cella -> {
			var json = new JSONObject();
			json.put("stato", cella.stato())
			.put("isOstacolo", cella.is(OSTACOLO))
			.put("isChiusura", cella.is(CHIUSURA))
			.put("isFrontiera", cella.is(FRONTIERA));
			return json;
		});
	}
	
}
