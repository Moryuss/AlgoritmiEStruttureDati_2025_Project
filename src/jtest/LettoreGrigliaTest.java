package jtest;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import francesco.GrigliaMatrix;
import francesco.ICella;
import francesco.ICella2D;
import francesco.ICompitoUno;
import francesco.IGriglia;
import francesco.IObstacle;
import francesco.StatoCella;
import francesco.TipoOstacolo;
import francesco.implementazioni.Cella;
import francesco.implementazioni.LettoreGriglia;
import processing.core.PApplet;
import processing.data.JSONObject;

class LettoreGrigliaTest extends PApplet{

	@Test
	void testNumeroDiOstacoli() {
		LettoreGriglia lettore = new LettoreGriglia();
		int width = 10;
		int height = 10;
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat);
//		IGriglia<?> griglia = lettore.crea(Path.of("config.json"));
		JSONObject json = loadJSONObject(Path.of("config.json").toFile());
		List<IObstacle> ostacoli = lettore.generaOstacoli(width, height, griglia, json);
		
		int somma = 0;
		for(TipoOstacolo ost : TipoOstacolo.values()) {
			if(json.hasKey(ost.toString())) {
				somma += json.getInt(ost.toString());
			}
		}
		
		assertEquals(ostacoli.size(), somma); 
	}
	
	@Test
	void testGrigliaPiccolaSenzaErrori() {
		assertDoesNotThrow(() -> {
			LettoreGriglia lettore = new LettoreGriglia();
			int width = 1;
			int height = 1;
			IGriglia<?> griglia = lettore.creaConDim(Path.of("config.json"), width, height);
		});
	}
	
	@Test
	void testGrigliaGrandeSenzaErrori() {
		assertDoesNotThrow(() -> {
			LettoreGriglia lettore = new LettoreGriglia();
			int width = 1200;
			int height = 1200;
			IGriglia<?> griglia = lettore.creaConDim(Path.of("config.json"), width, height);
		});
	}
	
	@Test
	void testOstacoliEffettivamentePresenti() {
		LettoreGriglia lettore = new LettoreGriglia();
		int width = 100;
		int height = 100;
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat);
		JSONObject json = loadJSONObject(Path.of("config.json").toFile());
		List<IObstacle> ostacoli = lettore.generaOstacoli(width, height, griglia, json);
		
		assertFalse(ostacoli.isEmpty());
		
		IGriglia<?> grigliaDaConfrontare = lettore.creaConDim(Path.of("config.json"), width, height);
		

		for(int i = 0; i < height; i++) {
			for(int j =0; j < width; j++) {
				int stato = grigliaDaConfrontare.getCellaAt(j, i).stato();
				System.out.println("Controllo lo stato: " + stato);
				boolean isOstacolo = false;
				
				for(IObstacle ostacolo : ostacoli) {
					for(ICella2D ostacoloCella : ostacolo.list()) {
	                	if(ostacoloCella.x() == j && ostacoloCella.y() == i) {
	                		System.out.println("Ostacolo trovato in: (" + i + ", " + j + ")");
	                    	isOstacolo = true;
	                        break;
	                	}
					}
				}

	            // Verifica lo stato della cella
				if (isOstacolo) {
					assertTrue(StatoCella.OSTACOLO.value() == stato, "Cella: (" + j + ", " + i + ") non è un ostacolo ma dovrebbe esserlo.");
				} else {
					assertTrue(StatoCella.VUOTA.value() == stato, "Cella: (" + j + ", " + i + ") è un ostacolo ma dovrebbe essere vuota.");
				}
			}
		}
	}

}
