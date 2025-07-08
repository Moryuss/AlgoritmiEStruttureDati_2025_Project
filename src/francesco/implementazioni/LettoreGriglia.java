package francesco.implementazioni;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import francesco.GrigliaMatrix;
import francesco.ICella;
import francesco.ICompitoUno;
import francesco.IGriglia;
import francesco.IObstacle;
import francesco.TipoOstacolo;
import francesco.ostacolibuilder.CentroCostruttore;
import processing.core.PApplet;
import processing.data.JSONObject;

public class LettoreGriglia extends PApplet implements ICompitoUno {
	
	
	@Override
	public IGriglia<?> crea(Path file) {
		File jsonFile = file.toFile();
		if (!jsonFile.exists()) {
			System.out.println("Errore nella lettura del file, ritorno una griglia vuota 50x50...");
			Cella[][] mat = GrigliaMatrix.inizializzaMatrice(50, 50);
			GrigliaMatrix griglia = new GrigliaMatrix(mat);
			return griglia;
		}
		
		// Carica il file JSON
		JSONObject json = loadJSONObject(jsonFile);
		int width = json.getInt("width");
		int height = json.getInt("height");
		
		// Ora crea la matrice vuota
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		
		// Inizializzo una IGriglia senza ostacoli per semplicita'
		IGriglia<ICella> griglia = new GrigliaMatrix(mat);
		
		// Infine aggiunge gli ostacoli
		int randomSeed = json.getInt("randomSeed");
		griglia = (IGriglia<ICella>) generaOstacoli(width, height, griglia, randomSeed, json.getJSONObject("maxOstacoli"));
		
//		for(IObstacle o : ostacoli) {
//			griglia = griglia.addObstacle(o);
//		}
		
		return griglia;
	}


	private IGriglia<? extends ICella> generaOstacoli(int width, int height, IGriglia<ICella> griglia, int randomSeed, JSONObject json) {
//		List<IObstacle> ostacoli = new ArrayList<>();
		IGriglia<? extends ICella> result = griglia;
		int ostCounter = 0;
		for(TipoOstacolo ost : TipoOstacolo.values()) {
			if(json.hasKey(ost.toString())) {
				int num = json.getInt(ost.toString());
				if(num < 0) {
					System.err.println("Numero di ostacoli " + ost.toString() + " non valido: " + num);
					continue;
				}
				for(int i = 0; i < num; i++) {
//					Ostacolo daAggiungere = CentroCostruttore.costruttoreCentrico(ost, width, height, griglia, randomSeed*(i+ostCounter));
//					if(daAggiungere != null) {
//						ostacoli.add(daAggiungere);
//					} 
					result = CentroCostruttore.costruttoreCentrico(ost, width, height, griglia, randomSeed*(i+ostCounter));
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
