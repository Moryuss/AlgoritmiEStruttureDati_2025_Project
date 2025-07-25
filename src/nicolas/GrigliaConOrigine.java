package nicolas;

import static nicolas.StatoCella.*;
import java.util.List;
import java.util.stream.Stream;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IObstacle;
import francesco.implementazioni.Ostacolo;
import processing.data.JSONArray;
import processing.data.JSONObject;
import utils.Utils;

public record GrigliaConOrigine(int[][] mat, int[][] dist, int Ox, int Oy, List<ICella2D> chiusura, ICellaConDistanze[] frontiera, int tipoGriglia) implements IGrigliaConOrigine {
	
	@Override
	public ICellaConDistanze getCellaAt(int x, int y) {
		return ICellaConDistanze.of(x, y, mat[y][x], dist[y][x]);
	}
	
	@Override
	public void setStato(int x, int y, int s) {
		mat[y][x] = s;
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
	public ICellaConDistanze getOrigine() {
		return getCellaAt(Ox, Oy);
	}
	
	@Override
	public double distanzaLiberaDa(int xd, int yd) {
		return getCellaAt(xd, yd).distanzaDaOrigine();
	}
	
	@Override
	public Stream<ICellaConDistanze> getFrontiera() {
		return Stream.of(frontiera);
	}
	
	@Override
	public IGriglia<ICellaConDistanze> addObstacle(IObstacle obstacle, int tipoOstacolo) {
		var mat = Utils.copy(this.mat);
		for (var c : obstacle.list()) {
			mat[c.y()][c.x()] = OSTACOLO.addTo(mat[c.y()][c.x()]);
		}
		return new GrigliaConOrigine(mat, dist, Ox, Oy, chiusura, frontiera, tipoGriglia|tipoOstacolo);
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
			.put("distanzaTorre", cella.distanzaTorre())
			.put("distanzaAlfiere", cella.distanzaAlfiere())
			.put("isOstacolo", cella.is(OSTACOLO))
			.put("isChiusura", cella.is(CHIUSURA))
			.put("isFrontiera", cella.is(FRONTIERA));
			return json;
		});
	}
	
}
