package francesco.generatoriDisposizioni;

import java.util.ArrayList;
import java.util.List;

import francesco.ICella2D;
import francesco.IGriglia;
import francesco.TipoOstacolo;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.Ostacolo;
import nicolas.StatoCella;

public class GeneratoreDoppiaLineaSpezzata implements GeneratoreDisposizione {

	@Override
	public IGriglia<?> generaGrigliaSpecifica(int width, int height, IGriglia<?> result) {
		// Anzitutto, viene piazzata la linea che divide a meta' la griglia
		int divisione = (int) Math.ceil(width/2);
		
		List<ICella2D> celle = new ArrayList<>();
		boolean rigaDaSinistra = true;
		
		for(int i = 0; i < height - 1; i++) {
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), divisione, i));
		}
		
		// Poi si scorre e si mettono le celle. Prima si scorre a destra e poi a sinistra
		// Dovendo lasciare uno spazio vuoto, si scorre di 2 in 2
		// La prima riga è vuota e il limite e' tale da non bloccare la cella centrale
		for(int i = 1; i < height - 2; i+=2) {
			// In questo for, si ha la coordinata y con i. Quindi si cerca le x
			// Prima a destra
			for(int j = rigaDaSinistra ? 0 : 1; 
						(rigaDaSinistra ? j < divisione - 1 : j < divisione);
						j++) {
				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), j, i));
			}
			// Poi a sinistra
			for(int j = rigaDaSinistra ? divisione + 1 : divisione + 2;
					(rigaDaSinistra ? j < width - 1 : j < width);
					j++) {
				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), j, i));
			}
			
			rigaDaSinistra = !rigaDaSinistra;
		}
		
		// Per l'ultima riga, si mette una linea piena tranne che per quella centrale
		for(int j = 0; j < width; j++) {
			if(j != divisione) {
				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), j, height - 1));
			}
		}
		
		result = result.addObstacle(new Ostacolo(celle), TipoOstacolo.PERSONALIZZATO.value());
		
		return result;
	}

}
