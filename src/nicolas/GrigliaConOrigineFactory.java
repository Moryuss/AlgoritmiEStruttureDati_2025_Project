package nicolas;

import static nicolas.StatoCella.*;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;

public final class GrigliaConOrigineFactory {
	
	private GrigliaConOrigineFactory() {}
	
	
	private static void paint(int[][] out, int[][] in, int[][] dist, List<ICella2D> chiusura, int x, int y, int col, int dx, int dy, BiConsumer<Integer,Integer> onPaint) {
		while (true) {
			x += dx;
			y += dy;
			if (x<0 || y<0 || x>=in[0].length || y>=in.length
					|| OSTACOLO.is(in[y][x])) return;
			out[y][x] = col;
			var d = dist[y-dy][x-dx]+ (1<<(((dx^~dy)&1)<<4));
			dist[y][x] = d;
			chiusura.addLast(new Cella2D(col, x, y));
			onPaint.accept(x, y);
		}
	}
	private static void paint(int[][] out, int[][] in, int[][] dist, List<ICella2D> chiusura, int x, int y, int col, int dx, int dy) {
		paint(out, in, dist, chiusura, x, y, col, dx, dy, (i,j)->{});
	}
	private static void paint0(int[][] out, int[][] in, int[][] dist, List<ICella2D> chiusura, int x, int y, int col, int dx, int dy, int dx1, int dy1, int dx2, int dy2) {
		paint(out, in, dist, chiusura, x, y, REGINA.value(), dx, dy, (j,i)->{
			paint(out, in, dist, chiusura, j, i, col, dx1, dy1);
			paint(out, in, dist, chiusura, j, i, col, dx2, dy2);
		});
	}
		
	public static GrigliaConOrigine creaV0(IGriglia<?> griglia, int Ox, int Oy) {
		var in = new int[griglia.height()][griglia.width()];
		for (int i = 0; i < in.length; i++) {
			for (int j = 0; j < in[0].length; j++) {
				in[i][j] = griglia.getCellaAt(j, i).stato();
			}
		}
		
		var res = Utils.sameSizeOf(in);
		var dist = Utils.sameSizeOf(in);
		
		griglia.forEach((j,i) -> {
			res[i][j] = OSTACOLO.is(in[i][j]) ? in[i][j] : 0;
			dist[i][j] = Integer.MAX_VALUE;
		});
		
		dist[Oy][Ox] = 0;
		var chiusura = new LinkedList<ICella2D>();
		var com = COMPLEMENTO.value();
		var cont = CONTESTO.value();
		
		paint0(res, in, dist, chiusura, Ox, Oy, com,   0, -1, -1, -1,  1,-1);
		paint0(res, in, dist, chiusura, Ox, Oy, com,   0,  1, -1,  1,  1, 1);
		paint0(res, in, dist, chiusura, Ox, Oy, com,   1,  0,  1, -1,  1, 1);
		paint0(res, in, dist, chiusura, Ox, Oy, com,  -1,  0, -1, -1, -1, 1);
		
		paint0(res, in, dist, chiusura, Ox, Oy, cont,  1, 1,  1,0, 0, 1);                  
		paint0(res, in, dist, chiusura, Ox, Oy, cont,  1,-1,  1,0, 0,-1);                  
		paint0(res, in, dist, chiusura, Ox, Oy, cont, -1,-1, -1,0, 0,-1);                  
		paint0(res, in, dist, chiusura, Ox, Oy, cont, -1, 1, -1,0, 0, 1);
		
		
		res[Oy][Ox] = ORIGINE.value();
		chiusura.add(new Cella2D(ORIGINE.value(), Ox, Oy));
		
		var frontiera = new ArrayDeque<ICella2>();
		
		
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
							frontiera.addLast(ICella2.of(j, i, res[i][j], dist[i][j]&0xffff, dist[i][j]>>>16));
							continue outer;
						}
					}
				}
				
			}
		}
		
		return new GrigliaConOrigine(res, dist, Ox, Oy, chiusura, frontiera.toArray(ICella2[]::new));
		
	}
	
}
