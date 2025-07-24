package nicolas;

import static nicolas.StatoCella.*;
import java.util.List;
import java.util.stream.Stream;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IObstacle;
import processing.data.JSONArray;
import processing.data.JSONObject;

public record GrigliaConOrigine(int[][] mat, int[][] dist, int Ox, int Oy, List<ICella2D> chiusura, ICella2[] frontiera, int tipoGriglia) implements IGrigliaConOrigine {
	
	@Override
	public boolean isNavigabile(int x, int y) {
		return getCellaAt(x, y).isNot(OSTACOLO);
	}
	
	@Override
	public ICella2 getCellaAt(int x, int y) {
		return new ICella2(){
			int d = dist[y][x];
			
			@Override
			public int stato() {
				return mat[y][x];
			}
			@Override
			public int x() {
				return x;
			}
			@Override
			public int y() {
				return y;
			}
			@Override
			public int distanzaTorre() {
				return d&0xffff;
			}
			@Override
			public int distanzaAlfiere() {
				return d>>>16;
			}
			@Override
			public double distanzaDaOrigine() {
				if (d==Integer.MAX_VALUE) return Double.POSITIVE_INFINITY;
				return (distanzaTorre()) + (distanzaAlfiere())*Utils.sqrt2;
			}
			@Override
			public void setStato(int stato) {
				mat[y][x] = stato;
			}
		};
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
	public ICella2 getOrigine() {
		return getCellaAt(Ox, Oy);
	}
	
	@Override
	public double distanzaLiberaDa(int xd, int yd) {
		return getCellaAt(xd, yd).distanzaDaOrigine();
	}
	
	@Override
	public Stream<ICella2> getFrontiera() {
		return Stream.of(frontiera);
	}
	
	@Override
	public IGriglia<ICella2> addObstacle(IObstacle obstacle) {
		var mat = Utils.copy(this.mat);
		for (var c : obstacle.list()) {
			mat[c.y()][c.x()] = OSTACOLO.addTo(mat[c.y()][c.x()]);
		}
		return new GrigliaConOrigine(mat, dist, Ox, Oy, chiusura, frontiera, tipoGriglia);
	}
	
	@Override
	public IObstacle convertiChiusuraInOstacolo() {
		return IObstacle.of(chiusura);
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
