package francesco.implementazioni;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import francesco.*;
import processing.core.PApplet;
import processing.data.JSONObject;

public class LettoreGriglia extends PApplet implements ICompitoUno {
	
	
	@Override
	public IGriglia<?> crea(Path file) {
		File jsonFile = file.toFile();
		if (!jsonFile.exists()) {
			System.out.println("Errore nella lettura del file, ritorno una griglia vuota 50x50...");
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
		griglia = generaOstacoli(width, height, griglia, randomSeed, json.getJSONObject("maxOstacoli"));
		
		return griglia;
	}


	private IGriglia<?> generaOstacoli(int width, int height, IGriglia<?> griglia, int randomSeed, JSONObject json) {
		IGriglia<?> result = griglia;
		int ostCounter = 0;
		for (TipoOstacolo ost : TipoOstacolo.values()) {
			
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
