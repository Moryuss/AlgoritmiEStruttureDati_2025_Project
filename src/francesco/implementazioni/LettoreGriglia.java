package francesco.implementazioni;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import francesco.*;
import francesco.generatoriDisposizioni.GeneratoreDoppiaLineaSpezzata;
import francesco.generatoriDisposizioni.GeneratoreLineaSpezzata;
import francesco.generatoriDisposizioni.GeneratoreScacchiera;
import francesco.generatoriDisposizioni.GeneratoreSpirale;
import francesco.generatoriDisposizioni.GeneratoreVariazioneDimensioni;
import processing.core.PApplet;
import processing.data.JSONObject;

public class LettoreGriglia extends PApplet implements ICompitoUno {
	
	
	@Override
	public IGriglia<?> crea(Path file) {
		File jsonFile = file.toFile();
		if (!jsonFile.exists()) {
			System.err.println("Errore nella lettura del file, ritorno una griglia vuota 50x50...");
			Cella[][] mat = GrigliaMatrix.inizializzaMatrice(50, 50);
			GrigliaMatrix griglia = new GrigliaMatrix(mat, 0);
			return griglia;
		}
		
		// Carica il file JSON
		JSONObject json = loadJSONObject(jsonFile);
		int width = json.getInt("width");
		int height = json.getInt("height");
		
		// Ora crea la matrice vuota
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		
		// Inizializzo una IGriglia senza ostacoli per semplicita'
		IGriglia<?> griglia = new GrigliaMatrix(mat, 0);
		
		// Infine aggiunge gli ostacoli
		int randomSeed = json.getInt("randomSeed");
		griglia = generaOstacoli(width, height, griglia, randomSeed, json.getJSONObject("maxOstacoli"),
				json.getJSONObject("disposizione"));
		
		return griglia;
	}
	
	/**
	 * Metodo per la generazione di griglie come specificato nel file JSON.
	 * @Precondizioni griglia rappresenta una griglia vuota 
	 * @Postcondizioni griglia viene riempita con gli ostacoli specificati nel file JSON.
	 * @param width
	 * @param height
	 * @param griglia
	 * @param randomSeed
	 * @param json
	 * @param disposizioni
	 * @return
	 */
	private static IGriglia<?> generaOstacoli(int width, int height, IGriglia<?> griglia, int randomSeed, JSONObject json, JSONObject disposizioni) {
		IGriglia<?> result = griglia;
		int ostCounter = 0;
		
		if(disposizioni == null) {
			result = generazioneStandard(width, height, randomSeed, json, result, ostCounter);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.STANDARD)) {
			result = generazioneStandard(width, height, randomSeed, json, result, ostCounter);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.VARAZIONE_DIMENSIONI)) {
			result = new GeneratoreVariazioneDimensioni().generaGrigliaSpecifica(width, height, result);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.SPIRALE)) {
			result = new GeneratoreSpirale().generaGrigliaSpecifica(width, height, result);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.LINEA_SPEZZATA)) {
			result = new GeneratoreLineaSpezzata().generaGrigliaSpecifica(width, height, result);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.DOPPIA_LINEA_SPEZZATA)) {
			result = new GeneratoreDoppiaLineaSpezzata().generaGrigliaSpecifica(width, height, result);
		}
		else if(isDisposizioneInJSONAndTrue(disposizioni, DisposizioneOstacoli.SCACCHIERA)) {
			result = new GeneratoreScacchiera().generaGrigliaSpecifica(width, height, result);
		}
		else {
			result = generazioneStandard(width, height, randomSeed, json, result, ostCounter);
		}
		
		return result;
	}


	/** Metodo ausiliario per controllare quale disposizione viene richiesta
	 * @param disposizioni
	 * @return
	 */
	private static boolean isDisposizioneInJSONAndTrue(JSONObject disposizioni, DisposizioneOstacoli disp) {
		return disposizioni.hasKey(disp.toString()) && disposizioni.getBoolean(disp.toString());
	}


	/**
	 * @param width
	 * @param height
	 * @param randomSeed
	 * @param json
	 * @param result
	 * @param ostCounter
	 * @return
	 */
	public static IGriglia<?> generazioneStandard(int width, int height, int randomSeed, JSONObject json,
			IGriglia<?> result, int ostCounter) {
		for (TipoOstacolo ost : TipoOstacolo.values()) {
			
			if(ost == TipoOstacolo.PERSONALIZZATO) {
				continue; // Il tipo PERSONALIZZATO non è un vero ostacolo
			}
			
			if (json.hasKey(ost.toString())) {
				int num = json.getInt(ost.toString());
				if (num < 0) {
					System.err.println("Numero di ostacoli " + ost.toString() + " non valido: " + num);
					continue;
				}
				for (int i=0; i<num; i++) {
					List<ICella2D> celle = ost.generaCelle(width, height, result, randomSeed*(i+ostCounter));
					Ostacolo ostacolo = new Ostacolo(celle);
					result = result.addObstacle(ostacolo, ost.value());
				}
				randomSeed += num;
			}
			else {
				System.err.println("Ostacolo non trovato: " + ost.toString());
			}
			ostCounter++;
		}
		return result;
	}

}
