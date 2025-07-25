package matteo;

import java.util.List;

import utils.Utils;

public record Cammino(int lunghezzaTorre, int lunghezzaAlfiere, List<ILandmark> landmarks, double lunghezza) implements ICammino {
	
	public Cammino(int lunghezzaTorre, int lunghezzaAlfiere, List<ILandmark> landmarks) {
		this(lunghezzaTorre, lunghezzaAlfiere, List.copyOf(landmarks), calcolaLunghezzaComplessiva(lunghezzaTorre, lunghezzaAlfiere));
	}
	
	private static double calcolaLunghezzaComplessiva(int lunTorre, int lunAlfiere) {
		if(lunTorre==Integer.MAX_VALUE||lunAlfiere==Integer.MAX_VALUE) {
			return Double.POSITIVE_INFINITY;
		}
		return lunTorre + Utils.sqrt2*lunAlfiere;
	}
	
}