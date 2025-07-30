package test;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import francesco.GrigliaMatrix;
import francesco.ICella;
import francesco.IGriglia;
import francesco.TipoOstacolo;
import francesco.implementazioni.Cella;
import francesco.implementazioni.LettoreGriglia;
import nicolas.StatoCella;
import processing.core.PApplet;
import processing.data.JSONObject;

class TestLettoreGriglia {

	@Deprecated
	void testNumeroDiOstacoli() {
		LettoreGriglia lettore = new LettoreGriglia();
		JSONObject json = PApplet.loadJSONObject(Path.of("src/test/json/testContaOstacoli.json").toFile());
		int width = json.getInt("width");
		int height = json.getInt("height");
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat, 0);
		int randomSeed = json.getInt("randomSeed");
//		List<IObstacle> ostacoli = lettore.generaOstacoli(width, height, griglia, randomSeed, json.getJSONObject("maxOstacoli"));
		
		int somma = 0;
		for(TipoOstacolo ost : TipoOstacolo.values()) {
			if(json.getJSONObject("maxOstacoli").hasKey(ost.toString())) {
				somma += json.getJSONObject("maxOstacoli").getInt(ost.toString());
			}
		}
		
//		assertEquals(ostacoli.size(), somma); 
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
		assertEquals(1, TipoOstacolo.SEMPLICE.value());
		assertEquals(TipoOstacolo.SEMPLICE.value(), griglia.getTipo());
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
	
	@Deprecated
	void testOstacoliEffettivamentePresenti() {
		LettoreGriglia lettore = new LettoreGriglia();
		JSONObject json = PApplet.loadJSONObject(Path.of("src/test/json/testContaOstacoli.json").toFile());
		int width = json.getInt("width");
		int height = json.getInt("height");
		int randomSeed = json.getInt("randomSeed");
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<ICella> griglia = new GrigliaMatrix(mat, 0);
		
		IGriglia<?> grigliaDaConfrontare = lettore.crea(Path.of("src/test/json/testContaOstacoli.json"));
		
		for(int i = 0; i < height; i++) {
			for(int j =0; j < width; j++) {
				int stato = grigliaDaConfrontare.getCellaAt(j, i).stato();
				boolean isOstacolo = false;
				
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
	
	@Test
	void testTipoCorretto() {
		LettoreGriglia lettore = new LettoreGriglia();
		IGriglia<?> griglia = lettore.crea(Path.of("src/test/json/stati/noOstacoli.json"));
		
		// Test senza ostacoli
		assertEquals(0, griglia.getTipo());
		
		griglia = lettore.crea(Path.of("src/test/json/stati/soloSemplici.json"));
		
		// Test con solo ostacoli semplici
		assertEquals(0b00000001, griglia.getTipo());
		
		griglia = lettore.crea(Path.of("src/test/json/stati/tuttiOstacoli.json"));
		
		// Test con tutti i tipi di ostacoli
		assertEquals(0b11111111, griglia.getTipo());
		
		griglia = lettore.crea(Path.of("src/test/json/stati/barreEDelimitatori.json"));
		
		// Test con tutte le barre ed entrambi i delimitatori
		int expected = 
				TipoOstacolo.sommaTipi(
						TipoOstacolo.sommaTipi(
								TipoOstacolo.sommaTipi(
										TipoOstacolo.sommaTipi(TipoOstacolo.BARRA_VERTICALE.value(),
									TipoOstacolo.BARRA_ORIZZONTALE.value()),
								TipoOstacolo.BARRA_DIAGONALE.value()),
						TipoOstacolo.DELIMITATORE_ORIZZONTALE.value()),
				TipoOstacolo.DELIMITATORE_VERTICALE.value());
		assertEquals(expected, griglia.getTipo());
	}
}
