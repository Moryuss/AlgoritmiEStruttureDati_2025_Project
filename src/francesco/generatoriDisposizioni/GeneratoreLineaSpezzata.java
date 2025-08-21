package francesco.generatoriDisposizioni;

import java.util.ArrayList;
import java.util.List;

import francesco.DisposizioneOstacoli;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.Ostacolo;
import nicolas.StatoCella;

public class GeneratoreLineaSpezzata implements GeneratoreDisposizione{

	@Override
	public IGriglia<?> generaGrigliaSpecifica(int width, int height, IGriglia<?> result) {
		int partenza = 1;
		int offsetFinale = 1;
		// Se la larghezza è divisible per 4, allora le due linee di spazio vuote vanno lasciate all'inizio
		if(width % 4 == 0) {
			partenza = 2;
		}
		// Se non è così, allora l'offset finale va a 2
		else {
			offsetFinale = 2;
		}
		
		boolean daSopra = true;
		
		for(int i = partenza; i < width - offsetFinale; i+=2) {
			List<ICella2D> celle = new ArrayList<>();
			// Si crea un'intera linea
			for(int j = 0; j < height; j++) {
				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, j));
			}
			// Se si va da sopra, si rimuove la cella alla fine
			if(daSopra) {
				celle.removeLast();
			}
			// Altrimenti si rimuove la cella sopra
			else {
				celle.remove(0);
			}
			daSopra = !daSopra;
			result = result.addObstacle(new Ostacolo(celle), DisposizioneOstacoli.LINEA_SPEZZATA.value());
		}
		
		return result;
	}

}
