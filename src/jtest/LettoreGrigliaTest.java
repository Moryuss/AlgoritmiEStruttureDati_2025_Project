package jtest;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import francesco.GrigliaMatrix;
import francesco.ICella;
import francesco.ICella2D;
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
		JSONObject json = loadJSONObject(Path.of("letturaTest.json").toFile());
		List<IObstacle> ostacoli = lettore.generaOstacoli(width, height, griglia, json);
		
		int somma = 0;
		for(TipoOstacolo ost : TipoOstacolo.values()) {
			if(json.hasKey(ost.toString())) {
				somma += json.getInt(ost.toString());
			}
		}
		
		assertEquals(ostacoli.size(), somma); 
	}
	
	// Attenzione: per questo test servono SOLO ostacoli semplici, altrimenti il controllo non torna
	@Test
	void contaCelleOstacolo() {
		var path = Path.of("letturaTest.json");
		var json = PApplet.loadJSONObject(path.toFile());
		IGriglia<?> griglia = new LettoreGriglia().crea(path);
		
		int count = 0;
		for (int i=0; i<griglia.height(); i++) {
			for (int j=0; j<griglia.width(); j++) {
				if (griglia.getCellaAt(j, i).is(StatoCella.OSTACOLO)) count++;
			}
		}
		assertEquals(json.getInt(TipoOstacolo.SEMPLICE.toString()), count);
	}

	@Test
	void testGrigliaPiccolaSenzaErrori() {
		assertDoesNotThrow(() -> {
			LettoreGriglia lettore = new LettoreGriglia();
			int width = 1;
			int height = 1;
			IGriglia<?> griglia = lettore.crea(Path.of("letturaTest.json"));
		});
	}
	
	@Test
	void testGrigliaGrandeSenzaErrori() {
		assertDoesNotThrow(() -> {
			LettoreGriglia lettore = new LettoreGriglia();
			int width = 1200;
			int height = 1200;
			IGriglia<?> griglia = lettore.crea(Path.of("letturaTest.json"));
		});
	}
	
	@Test
	void testOstacoliEffettivamentePresenti() {
		LettoreGriglia lettore = new LettoreGriglia();
		JSONObject json = loadJSONObject(Path.of("letturaTest.json").toFile());
		int width = json.getInt("width");
		int height = json.getInt("height");
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat);
		List<IObstacle> ostacoli = lettore.generaOstacoli(width, height, griglia, json);
		
		assertFalse(ostacoli.isEmpty());
		
		IGriglia<?> grigliaDaConfrontare = lettore.crea(Path.of("letturaTest.json"));
		
		for(int i = 0; i < height; i++) {
			for(int j =0; j < width; j++) {
				int stato = grigliaDaConfrontare.getCellaAt(j, i).stato();
				boolean isOstacolo = false;
				
				for(IObstacle ostacolo : ostacoli) {
					for(ICella2D ostacoloCella : ostacolo.list()) {
	                	if(ostacoloCella.x() == j && ostacoloCella.y() == i) {
	                    	isOstacolo = true;
	                        break;
	                	}
					}
				}
				
	            // Verifica lo stato della cella
				if (isOstacolo) {
					assertTrue(StatoCella.OSTACOLO.is(stato), "Cella: (" + j + ", " + i + ") non è un ostacolo ma dovrebbe esserlo.");
				} else {
					assertTrue(StatoCella.OSTACOLO.isNot(stato), "Cella: (" + j + ", " + i + ") è un ostacolo ma dovrebbe essere vuota.");
				}
			}
		}
	}

}
