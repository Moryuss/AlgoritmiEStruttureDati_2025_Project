package test;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import francesco.GrigliaMatrix;
import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.IObstacle;
import francesco.TipoOstacolo;
import francesco.implementazioni.Cella;
import francesco.implementazioni.LettoreGriglia;
import nicolas.StatoCella;
import processing.core.PApplet;
import processing.data.JSONObject;

class TestLettoreGriglia {

	@Test
	void testNumeroDiOstacoli() {
		LettoreGriglia lettore = new LettoreGriglia();
		JSONObject json = PApplet.loadJSONObject(Path.of("src/test/json/testContaOstacoli.json").toFile());
		int width = json.getInt("width");
		int height = json.getInt("height");
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat);
		int randomSeed = json.getInt("randomSeed");
		List<IObstacle> ostacoli = lettore.generaOstacoli(width, height, griglia, randomSeed, json.getJSONObject("maxOstacoli"));
		
		int somma = 0;
		for(TipoOstacolo ost : TipoOstacolo.values()) {
			if(json.getJSONObject("maxOstacoli").hasKey(ost.toString())) {
				somma += json.getJSONObject("maxOstacoli").getInt(ost.toString());
			}
		}
		
		assertEquals(ostacoli.size(), somma); 
	}
	
	@Test
	void contaCelleOstacolo() {
		var path = Path.of("src/test/json/testContaCelleSemplici.json");
		var json = PApplet.loadJSONObject(path.toFile());
		IGriglia<?> griglia = new LettoreGriglia().crea(path);
		
		int count = 0;
		for (int i=0; i<griglia.height(); i++) {
			for (int j=0; j<griglia.width(); j++) {
				if (griglia.getCellaAt(j, i).is(StatoCella.OSTACOLO)) count++;
			}
		}
		
		json = json.getJSONObject("maxOstacoli");
		assertEquals(json.getInt(TipoOstacolo.SEMPLICE.toString()), count);
	}

	@Test
	void testGrigliaPiccolaSenzaErrori() {
		assertDoesNotThrow(() -> {
			LettoreGriglia lettore = new LettoreGriglia();
			IGriglia<?> griglia = lettore.crea(Path.of("src/test/json/testGrigliaPiccola.json"));
			
			JSONObject json = PApplet.loadJSONObject(Path.of("src/test/json/testGrigliaPiccola.json").toFile());
			int width = json.getInt("width");
			int height = json.getInt("height");
			
			assertEquals(width, griglia.width());
			assertEquals(height, griglia.height());
		});
	}
	
	@Test
	void testGrigliaGrandeSenzaErrori() {
		assertDoesNotThrow(() -> {
			LettoreGriglia lettore = new LettoreGriglia();
			IGriglia<?> griglia = lettore.crea(Path.of("src/test/json/testGrigliaGrande.json"));
			
			JSONObject json = PApplet.loadJSONObject(Path.of("src/test/json/testGrigliaGrande.json").toFile());
			int width = json.getInt("width");
			int height = json.getInt("height");
			
			assertEquals(width, griglia.width());
			assertEquals(height, griglia.height());
		});
	}
	
	@Test
	void testOstacoliEffettivamentePresenti() {
		LettoreGriglia lettore = new LettoreGriglia();
		JSONObject json = PApplet.loadJSONObject(Path.of("src/test/json/testContaOstacoli.json").toFile());
		int width = json.getInt("width");
		int height = json.getInt("height");
		int randomSeed = json.getInt("randomSeed");
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat);
		List<IObstacle> ostacoli = lettore.generaOstacoli(width, height, griglia, randomSeed, json.getJSONObject("maxOstacoli"));
		
		assertFalse(ostacoli.isEmpty());
		
		IGriglia<?> grigliaDaConfrontare = lettore.crea(Path.of("src/test/json/testContaOstacoli.json"));
		
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

	@Test
	void testDimensioniCorrette() {
		LettoreGriglia lettore = new LettoreGriglia();
		JSONObject json = PApplet.loadJSONObject(Path.of("src/test/json/testContaOstacoli.json").toFile());
		int width = json.getInt("width");
		int height = json.getInt("height");
		
		IGriglia<?> griglia = lettore.crea(Path.of("src/test/json/testContaOstacoli.json"));
		
		assertEquals(width, griglia.width());
		assertEquals(height, griglia.height());
	}
}
