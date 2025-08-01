package francesco;

import java.util.List;
import francesco.implementazioni.Cella;
import nicolas.StatoCella;

public class GrigliaMatrix implements IGriglia<ICella> {
	
	public static final int CUSTOM_TYPE = 999;
	private ICella[][] mat;
	private int tipo;
	
	public GrigliaMatrix(ICella[][] mat, int tipo){
		this.mat = mat;
		this.tipo = tipo;
	}
	
	public GrigliaMatrix(ICella[][] mat) {
		this(mat, 0);
	}
	
	
	@Override
	public ICella getCellaAt(int x, int y) {
		return mat[y][x];
	}
	
	@Override
	public void setStato(int x, int y, int s) {
		mat[y][x] = new Cella(s);
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
		return tipo();
	}
	
	@Override
	public void setStatoGriglia(int nuovoTipo) {
		tipo = nuovoTipo;
	}
	
	public ICella[][] mat(){
		return this.mat;
	}
	
	public int tipo() {
		return this.tipo;
	}
	
	@Override
	public IGriglia<ICella> addObstacle(IObstacle obstacle, int tipoOstacolo) {
		ICella[][] mat = inizializzaMatrice(width(), height());
		for(int i=0; i<height(); i++) {
			for(int j=0; j<width(); j++) {
				mat[i][j] = new Cella(this.mat[i][j].stato());
			}
		}
		obstacle.list().forEach(c -> {
			mat[c.y()][c.x()] = new Cella(mat[c.y()][c.x()].stato() | StatoCella.OSTACOLO.value());
		});
		return new GrigliaMatrix(mat, this.getTipo()|tipoOstacolo);
	}
	
	
	public static IGriglia<ICella> from(int width, int height, List<IObstacle> ostacoli) {
		ICella[][] mat = inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat, CUSTOM_TYPE);
		for(IObstacle o : ostacoli) {
			griglia = griglia.addObstacle(o, 0);
		}
		return griglia;
	}
	
	public static Cella[][] inizializzaMatrice(int width, int height) {
		Cella[][] mat = new Cella[height][width];
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				mat[i][j] = new Cella(0); 
			}
		}
		return mat;
	}
	
}
