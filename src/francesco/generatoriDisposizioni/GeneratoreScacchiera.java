package francesco.generatoriDisposizioni;

import java.util.ArrayList;
import java.util.List;

import francesco.ICella2D;
import francesco.IGriglia;
import francesco.TipoOstacolo;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.Ostacolo;
import nicolas.StatoCella;

public class GeneratoreScacchiera implements GeneratoreDisposizione {

	enum tipoLinea {
		INIZIO_PIENO,
		INIZIO_VUOTO,
		PIENA_SINISTRA,
		PIENA_DESTRA
	}
	
	@Override
	public IGriglia<?> generaGrigliaSpecifica(int width, int height, IGriglia<?> result) {
		List<ICella2D> celle = new ArrayList<>();
		List<tipoLinea> ordine = List.of(tipoLinea.INIZIO_VUOTO, tipoLinea.INIZIO_PIENO, tipoLinea.PIENA_SINISTRA,
				tipoLinea.INIZIO_PIENO, tipoLinea.INIZIO_VUOTO, tipoLinea.PIENA_DESTRA);	
		int indiceProgressoLinea = 0;
		// Si inizia scorrendo tutte le righe
		for(int i = 0; i < height; i++) {
			// Si genera la linea in base al tipo corrente
			celle.addAll(generazioneLineaScacchiera(width, i, ordine.get(indiceProgressoLinea)));
			// Si prosegue con il tipo successivo
			indiceProgressoLinea = indiceProgressoLinea < ordine.size() - 1 ? indiceProgressoLinea + 1 : 0;
		}
		
		result = result.addObstacle(new Ostacolo(celle), TipoOstacolo.PERSONALIZZATO.value());
		
		return result;
	}

	private static List<ICella2D> generazioneLineaScacchiera(int width, int y, tipoLinea tipo) {
		List<ICella2D> celle = new ArrayList<>();
		
		switch (tipo) {
			case INIZIO_PIENO:
				for(int i = 0; i < width; i+=2) {
					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
				}
				break;
			case INIZIO_VUOTO:
				for(int i = 1; i < width; i+=2) {
					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
				}
				break;
			case PIENA_SINISTRA:
				for(int i = 0; i < width - 1; i++) {
					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
				}
				break;
			case PIENA_DESTRA:
				for(int i = 1; i < width; i++) {
					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
				}
				break;
			default:
				System.err.println("Tipo di linea non riconosciuto: " + tipo);
		}
		
		return celle;
	}
}
