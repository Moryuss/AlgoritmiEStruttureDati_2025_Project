package francesco;

import java.util.List;
import francesco.implementazioni.Cella;
import nicolas.StatoCella;

public record GrigliaMatrix(ICella[][] mat, int tipo, int xmin, int ymin) implements IGrigliaMutabile<ICella> {
	
	public static final int CUSTOM_TYPE = 999;
	
	
	public GrigliaMatrix(ICella[][] mat, int tipo){
		this(mat, tipo, 0, 0);
	}
	
	
	@Override
	public ICella getCellaAt(int x, int y) {return mat[y-ymin][x-xmin];}
	@Override
	public void setStato(int x, int y, int s) {mat[y-ymin][x-xmin] = new Cella(s);}
	@Override
	public int width() {return mat[0].length;}
	@Override
	public int height() {return mat.length;}
	@Override
	public int getTipo() {return tipo;}
	
	
	
	@Override
	public IGrigliaMutabile<ICella> addObstacle(IObstacle obstacle, int tipoOstacolo) {
		ICella[][] mat = inizializzaMatrice(width(), height());
		for(int i=0; i<height(); i++) {
			for(int j=0; j<width(); j++) {
				mat[i][j] = new Cella(this.mat[i][j].stato());
			}
		}
		obstacle.list().forEach(c -> {
			mat[c.y()-ymin][c.x()-xmin] = new Cella(mat[c.y()-ymin][c.x()-xmin].stato() | StatoCella.OSTACOLO.value());
		});
		return new GrigliaMatrix(mat, this.getTipo()|tipoOstacolo);
	}
	
	
	public static IGriglia<?> from(int width, int height, List<IObstacle> ostacoli) {
		ICella[][] mat = inizializzaMatrice(width, height);
		IGriglia<?> griglia = new GrigliaMatrix(mat, CUSTOM_TYPE);
		for(IObstacle o : ostacoli) {
			griglia = griglia.addObstacle(o, 0);
		}
		return griglia;
	}
	
	public static Cella[][] inizializzaMatrice(int width, int height) {
		var mat = new Cella[height][width];
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				mat[i][j] = new Cella(0); 
			}
		}
		return mat;
	}
	
	
	public static IGrigliaMutabile<?> from(IGriglia<?> griglia) {
		return from(griglia, 0);
	}
	
	public static IGrigliaMutabile<?> from(IGriglia<?> griglia, int tipo) {
		int width=griglia.width(), height=griglia.height();
		ICella[][] mat = new ICella[height][width];
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				mat[i][j] = new Cella(griglia.getCellaAt(j, i).stato());
			}
		}
		return new GrigliaMatrix(mat, griglia.getTipo()|tipo);
	}
	
}
