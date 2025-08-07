package nicolas;

import java.util.LinkedList;
import java.util.function.BiFunction;
import francesco.GrigliaMatrix;
import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella;
import francesco.implementazioni.Cella2D;
import utils.BiHashmap;

public class RegioneFactory {
	
	public static Regione regioneContenente2(IGriglia<?> griglia, int x, int y, BiFunction<Integer,Integer,ICella2D> func) {
		var indexer = new BiHashmap<Integer,Integer>();
		griglia.forEach((j,i) -> {
			var stato = griglia.getCellaAt(j, i).stato();
			indexer.put(i, j, StatoCella.OSTACOLO.is(stato) ? stato : -1);
		});
		
		var regione = new Regione(func.apply(x, y));
		bucketPaint(griglia, indexer, 1, regione, x, y, func);
		return regione;
	}
	
	
	
	private static void bucketPaint(IGriglia<?> griglia, BiHashmap<Integer,Integer> indexer, int regioneIndex, Regione regione, int x, int y, BiFunction<Integer,Integer,ICella2D> func) {
		if (indexer.getOrDefault(y, x,-1)>=0) return;
		var c = griglia.getCellaAt(x, y);
		if (StatoCella.DESTINAZIONE.removeTo(c.stato())!=0) {
			var c2 = func.apply(x, y);
			if (c2.is(StatoCella.FRONTIERA) && !regione.frontiera.contains(c2)) {
				regione.frontiera.add(c2);
			}
			return;
		}
		
		indexer.put(y, x, regioneIndex);
		regione.addCella(new Cella2D(c.stato(), x, y));
		
		if (y>griglia.ymin()) {
			if (x>griglia.xmin()) bucketPaint(griglia, indexer, regioneIndex, regione, x-1, y-1, func);
			bucketPaint(griglia, indexer, regioneIndex, regione, x, y-1, func);
			if (x<griglia.xmax()) bucketPaint(griglia, indexer, regioneIndex, regione, x+1, y-1, func);
		}
		{
			if (x>griglia.xmin()) bucketPaint(griglia, indexer, regioneIndex, regione, x-1, y, func);
			bucketPaint(griglia, indexer, regioneIndex, regione, x, y, func);
			if (x<griglia.xmax()) bucketPaint(griglia, indexer, regioneIndex, regione, x+1, y, func);
		}
		if (y<griglia.ymax()) {
			if (x>griglia.xmin()) bucketPaint(griglia, indexer, regioneIndex, regione, x-1, y+1, func);
			bucketPaint(griglia, indexer, regioneIndex, regione, x, y+1, func);
			if (x<griglia.xmax()) bucketPaint(griglia, indexer, regioneIndex, regione, x+1, y+1, func);
		}
	}
	
	
	
	public static Regione regioneContenente(IGriglia<?> griglia, int x, int y, BiFunction<Integer,Integer,ICella2D> func) {
		int[] array = new int[griglia.width()*griglia.height()];
		griglia.forEach((j,i) -> {
			var stato = griglia.getCellaAt(j, i).stato();
			if (StatoCella.OSTACOLO.is(stato))
				array[j+i*griglia.width()] = stato;
			else {
				array[j+i*griglia.width()] = -1;
			}
		});
		return regioneContenente(griglia, x, y, array, 1, func);
	}
	
	public static Regione regioneContenente(IGriglia<?> griglia, int x, int y, int[]array, int regioneIndex, BiFunction<Integer,Integer,ICella2D> func) {
		var regione = new Regione(func.apply(x, y));
		bucketPaint(griglia, array, regioneIndex, regione, x, y, func);
		return regione;
	}
	
	
	private static void bucketPaint(IGriglia<?> griglia, int[] mapper, int regioneIndex, Regione regione, int x, int y, BiFunction<Integer,Integer,ICella2D> func) {
		var w = griglia.width();
		if (mapper[x+y*w]>=0) return;
		var c = griglia.getCellaAt(x, y);
		if (StatoCella.DESTINAZIONE.removeTo(c.stato())!=0) {
			var c2 = func.apply(x, y);
			if (c2.is(StatoCella.FRONTIERA) && !regione.frontiera.contains(c2)) {
				regione.frontiera.add(c2);
			}
			return;
		}
		mapper[x+y*w] = regioneIndex;
		regione.addCella(new Cella2D(c.stato(), x, y));
		
		if (y>0) {
			if (x>0) bucketPaint(griglia, mapper, regioneIndex, regione, x-1, y-1, func);
			bucketPaint(griglia, mapper, regioneIndex, regione, x, y-1, func);
			if (x+1<w) bucketPaint(griglia, mapper, regioneIndex, regione, x+1, y-1, func);
		}
		{
			if (x>0) bucketPaint(griglia, mapper, regioneIndex, regione, x-1, y, func);
			bucketPaint(griglia, mapper, regioneIndex, regione, x, y, func);
			if (x+1<w) bucketPaint(griglia, mapper, regioneIndex, regione, x+1, y, func);
		}
		if (y+1<griglia.height()) {
			if (x>0) bucketPaint(griglia, mapper, regioneIndex, regione, x-1, y+1, func);
			bucketPaint(griglia, mapper, regioneIndex, regione, x, y+1, func);
			if (x+1<griglia.width()) bucketPaint(griglia, mapper, regioneIndex, regione, x+1, y+1, func);
		}
	}
	
	
	
	public static GrigliaConRegioni<ICella2D> from(IGrigliaConOrigine griglia) {
		return from(griglia, griglia::getCellaAt);
	}
	public static <C extends ICella> GrigliaConRegioni<C> from(IGriglia<C> griglia, BiFunction<Integer,Integer,ICella2D> func) {
		int[] mapper = new int[griglia.width()*griglia.height()];
		var regioni = new LinkedList<Regione>();
		for (int i = 0; i < mapper.length; i++) {
			mapper[i] = -1;
		}
		
		
		int w=griglia.width(), h=griglia.height();
		
		for (int i=0; i<h; i++) {
			for (int j=0; j<w; j++) {
				if (mapper[j+i*w]>=0) continue;
				var c = griglia.getCellaAt(j, i);
				if (c.is(StatoCella.OSTACOLO) || c.stato()!=0) continue;
				regioni.add(regioneContenente(griglia, j, i, mapper, regioni.size(), func));
			}
		}
		
		return new GrigliaConRegioni<>(griglia, mapper, regioni.toArray(Regione[]::new));
	}
	
	
	
	public static IGriglia<?> creaSottoGriglia(IGriglia<?> griglia, Regione regione) {
		int width=regione.maxWidth(), height=regione.maxHeight();
		int xmin=regione.xmin, ymin=regione.ymin;
		var mat = GrigliaMatrix.inizializzaMatrice(width, height);
		
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				mat[i][j] = new Cella(griglia.getCellaAt(j+xmin, i+ymin).stato());
			}
		}
		
		return new GrigliaMatrix(mat, griglia.getTipo(), xmin, ymin);
	}
	
	public static IGriglia<?> creaSottoGriglia2(IGriglia<?> griglia, Regione regione) {
		int width=regione.maxWidth(), height=regione.maxHeight();
		int xmin=regione.xmin, ymin=regione.ymin;
		var map = new BiHashmap<Integer,Integer>();
		
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				map.put(i+ymin, j+xmin, griglia.getCellaAt(j+xmin, i+ymin).stato());
			}
		}
		
		return new GrigliaBiHashmap(map, width, height, xmin, ymin, griglia.getTipo());
	}
	
}
