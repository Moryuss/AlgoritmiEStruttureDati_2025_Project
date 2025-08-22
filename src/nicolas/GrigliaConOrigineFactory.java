package nicolas;

import static java.lang.Math.*;
import static nicolas.StatoCella.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import francesco.*;
import francesco.implementazioni.Cella2D;
import processing.data.JSONArray;
import utils.BiHashmap;
import utils.Utils;

public final class GrigliaConOrigineFactory {
	
	private GrigliaConOrigineFactory() {}
	
	
	private static void paint(int xmin, int ymin, int[][] out, int[][] in, List<ICella2D> chiusura, int x, int y, int col, int dx, int dy, boolean recursion, int col2, int dx1, int dy1, int dx2, int dy2) {
		while (true) {
			x += dx;
			y += dy;
			if (x<0 || y<0 || x>=in[0].length || y>=in.length
					|| OSTACOLO.is(in[y][x])) return;
			out[y][x] = col;
			chiusura.addLast(new Cella2D(col, x+xmin, y+ymin));
			if (recursion) {
				paint(xmin, ymin, out, in, chiusura, x, y, col2, dx1, dy1, false, 0, 0, 0, 0, 0);
				paint(xmin, ymin, out, in, chiusura, x, y, col2, dx2, dy2, false, 0, 0, 0, 0, 0);
			}
		}
	}
	
	public static GrigliaConOrigine creaV0(IGriglia<?> griglia, int Ox, int Oy) {
		var in = new int[griglia.height()][griglia.width()];
		var res = Utils.sameSizeOf(in);
		
		for (int i = 0; i < in.length; i++) {
			for (int j = 0; j < in[0].length; j++) {
				in[i][j] = griglia.getCellaAt(j+griglia.xmin(), i+griglia.ymin()).stato();
				res[i][j] = OSTACOLO.is(in[i][j]) ? in[i][j] : 0;
			}
		}
		
		int xmin=griglia.xmin(), ymin=griglia.ymin();
		
		var chiusura = new LinkedList<ICella2D>();
		int reg = REGINA.value();
		var com = COMPLEMENTO.value();
		var cont = CONTESTO.value();
		
		paint(xmin, ymin, res, in, chiusura, Ox, Oy, reg,   0, -1, true, com,  -1,-1,  1,-1);
		paint(xmin, ymin, res, in, chiusura, Ox, Oy, reg,   0,  1, true, com,  -1, 1,  1, 1);
		paint(xmin, ymin, res, in, chiusura, Ox, Oy, reg,   1,  0, true, com,   1,-1,  1, 1);
		paint(xmin, ymin, res, in, chiusura, Ox, Oy, reg,  -1,  0, true, com,  -1,-1, -1, 1);
		paint(xmin, ymin, res, in, chiusura, Ox, Oy, reg,   1,  1, true, cont,  1, 0,  0, 1);                  
		paint(xmin, ymin, res, in, chiusura, Ox, Oy, reg,   1, -1, true, cont,  1, 0,  0,-1);                  
		paint(xmin, ymin, res, in, chiusura, Ox, Oy, reg,  -1, -1, true, cont, -1, 0,  0,-1);                  
		paint(xmin, ymin, res, in, chiusura, Ox, Oy, reg,  -1,  1, true, cont, -1, 0,  0, 1);
		
		
		res[Oy-ymin][Ox-xmin] = ORIGINE.value();
		chiusura.add(new Cella2D(ORIGINE.value(), Ox, Oy));
		
		var frontiera = creaFrontiera(xmin, ymin, res);
		
		return new GrigliaConOrigine(res, Ox, Oy, xmin, ymin, chiusura, frontiera.toArray(ICella2D[]::new), griglia.getTipo());
	}


	private static ArrayDeque<ICella2D> creaFrontiera(int xmin, int ymin, int[][] res) {
		var frontiera = new ArrayDeque<ICella2D>();
		
		for (int i = 0; i < res.length; i++) {
			outer:
			for (int j = 0; j < res[0].length; j++) {
				if (CHIUSURA.isNot(res[i][j])) continue outer;
				
				for (int ii = -1; ii < 2; ii++) {
					for (int jj = -1, iii, jjj; jj < 2; jj++) {
						iii=i+ii;
						jjj=j+jj;
						if (iii<0 || jjj<0 || iii>=res.length || jjj>=res[0].length) continue;
						if (CHIUSURA.isNot(res[iii][jjj])
							&& !OSTACOLO.is(res[iii][jjj])) {
							res[i][j] |= FRONTIERA.value();
							frontiera.addLast(ICella2D.of(j+xmin, i+ymin, res[i][j]));
							continue outer;
						}
					}
				}
				
			}
		}
		
		return frontiera;
	}
	
	
	
	private static void paint(IGriglia<?> griglia, BiHashmap<Integer,Integer> map, List<ICella2D> chiusura, int x, int y, int col, int dx, int dy, BiConsumer<Integer,Integer> onPaint) {
		while (true) {
			x += dx;
			y += dy;
			if (x<griglia.xmin() || y<griglia.ymin() || x>griglia.xmax() || y>griglia.ymax()) {
				return;
			}
			if (griglia.getCellaAt(x, y).is(OSTACOLO)) {
				return;
			}
			map.put(y, x, col);
			chiusura.addLast(new Cella2D(col, x, y));
			onPaint.accept(x, y);
		}
	}
	private static void paint(IGriglia<?> griglia, BiHashmap<Integer,Integer> map, List<ICella2D> chiusura, int x, int y, int col, int dx, int dy) {
		paint(griglia, map, chiusura, x, y, col, dx, dy, (i,j)->{});
	}
	private static void paint0(IGriglia<?> griglia, BiHashmap<Integer,Integer> map, List<ICella2D> chiusura, int x, int y, int col, int dx, int dy, int dx1, int dy1, int dx2, int dy2) {
		paint(griglia, map, chiusura, x, y, REGINA.value(), dx, dy, (j,i)->{
			paint(griglia, map, chiusura, j, i, col, dx1, dy1);
			paint(griglia, map, chiusura, j, i, col, dx2, dy2);
		});
	}	
	
	public static IGrigliaConOrigine creaV1(IGriglia<?> griglia, int Ox, int Oy) {
		var map = new BiHashmap<Integer,Integer>();
		var chiusura = new LinkedList<ICella2D>();
		var com = COMPLEMENTO.value();
		var cont = CONTESTO.value();
		
		griglia.forEach((j,i) -> {
			int s = griglia.getCellaAt(j, i).is(OSTACOLO) ? OSTACOLO.value() : 0;
			map.put(i, j, s);
		});
		
		
		paint0(griglia, map, chiusura, Ox, Oy, com,   0, -1, -1, -1,  1,-1);
		paint0(griglia, map, chiusura, Ox, Oy, com,   0,  1, -1,  1,  1, 1);
		paint0(griglia, map, chiusura, Ox, Oy, com,   1,  0,  1, -1,  1, 1);
		paint0(griglia, map, chiusura, Ox, Oy, com,  -1,  0, -1, -1, -1, 1);
		paint0(griglia, map, chiusura, Ox, Oy, cont,  1, 1,  1,0, 0, 1);                  
		paint0(griglia, map, chiusura, Ox, Oy, cont,  1,-1,  1,0, 0,-1);                  
		paint0(griglia, map, chiusura, Ox, Oy, cont, -1,-1, -1,0, 0,-1);                  
		paint0(griglia, map, chiusura, Ox, Oy, cont, -1, 1, -1,0, 0, 1);
		
		map.put(Oy, Ox, ORIGINE.value());
		chiusura.add(new Cella2D(ORIGINE.value(), Ox, Oy));
		
		var frontiera = new ArrayDeque<ICella2D>();
		
		griglia.forEach((j,i) -> {
			if (!CHIUSURA.is(map.get(i, j))) return;
			for (int ii = -1; ii < 2; ii++) {
				for (int jj = -1, iii, jjj; jj < 2; jj++) {
					iii=i+ii;
					jjj=j+jj;
					if (iii<griglia.ymin() || jjj<griglia.xmin() || iii>griglia.ymax() || jjj>griglia.xmax()) continue;
					var s = map.get(iii, jjj);
					if (CHIUSURA.isNot(s) && !OSTACOLO.is(s)) {
						map.compute(i, j, v->v|FRONTIERA.value());
						frontiera.addLast(ICella2D.of(j, i, s));
						return;
					}
				}
			}
		});
		
		return new IGrigliaConOrigine() {
			
			@Override
			public int width() {return griglia.width();}
			@Override
			public int height() {return griglia.height();}
			@Override
			public int getTipo() {return griglia.getTipo();}
			public int xmin() {return griglia.xmin();};
			public int ymin() {return griglia.ymin();};
			@Override
			public ICella2D getCellaAt(int x, int y) {
				return new Cella2D(map.get(y, x), x, y);
			}
			
			@Override
			public IGriglia<ICella2D> addObstacle(IObstacle obstacle, int tipoOstacolo) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public JSONArray toJSON() {
				return collect(c->c.stato(), Utils.collectToJSONArray(JSONArray::append), Utils.collectToJSONArray(JSONArray::append));
			}
			
			@Override
			public ICella2D getOrigine() {
				return ICella2D.of(Ox, Oy, StatoCella.ORIGINE.value());
			}
			
			@Override
			public Stream<ICella2D> getFrontiera() {
				return frontiera.stream();
			}
			
			@Override
			public IObstacle convertiChiusuraInOstacolo() {
				var celle = new ArrayList<ICella2D>();
				griglia.forEach((x,y) -> {
					if (getCellaAt(x, y).is(OSTACOLO)) {
						celle.add(getCellaAt(x, y));
					}
				});
				return ()->celle;
			}
		};
	}
	
	public static int calcDist(int x1, int y1, int x2, int y2) {
		var Dx = abs(x1-x2);
		var Dy = abs(y1-y2);
		var d2 = min(Dx, Dy);
		var d1 = max(Dx, Dy)-d2;
		return d2<<16|d1;
	}
	public static int calcDist(IHave2DCoordinate a, IHave2DCoordinate b) {
		return calcDist(a.x(), a.y(), b.x(), b.y());
	}
	
	
}
