package matteo;

import java.util.ArrayList;
import java.util.List;

import nicolas.Utils;

public class Cammino implements ICammino{

	private final double lunghezza;
	private final int lunghezzaTorre;
	private final int lunghezzaAlfiere;
    private final List<ILandmark> landmarks;
    
    public Cammino(int lunghezzaTorre, int lunghezzaAlfiere, List<ILandmark> landMarks) {
        this.landmarks = new ArrayList<>(landMarks);
        this.lunghezzaTorre = lunghezzaTorre;
        this.lunghezzaAlfiere = lunghezzaAlfiere;
        this.lunghezza = this.calcolaLunghezzaComplessiva(lunghezzaTorre, lunghezzaAlfiere);
    }

    
    @Override
    public double lunghezza() {
        return lunghezza;
    }
    
    @Override
    public List<ILandmark> landmarks() {
        return landmarks;
    }

	@Override
	public int lunghezzaTorre() {
		return this.lunghezzaTorre;
	}

	@Override
	public int lunghezzaAlfiere() {
		return this.lunghezzaAlfiere;
	}
	
	private double calcolaLunghezzaComplessiva(int lunTorre, int lunAlfiere) {
		if(lunTorre == Integer.MAX_VALUE||
				lunAlfiere == Integer.MAX_VALUE) {
			return Double.POSITIVE_INFINITY;
		}
		return (lunghezzaTorre()) + (lunghezzaAlfiere())*Utils.sqrt2;
	}
}
