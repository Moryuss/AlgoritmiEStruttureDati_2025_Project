package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import francesco.GrigliaMatrix;
import francesco.IGriglia;
import francesco.generatoriDisposizioni.GeneratoreDoppiaLineaSpezzata;
import francesco.generatoriDisposizioni.GeneratoreLineaSpezzata;
import francesco.generatoriDisposizioni.GeneratoreScacchiera;
import francesco.generatoriDisposizioni.GeneratoreSpirale;
import francesco.generatoriDisposizioni.GeneratoreVariazioneDimensioni;
import francesco.implementazioni.Cella;
import processing.core.PApplet;
import utils.Utils;

class TestGenerazioniSpecifiche {
	
	private static final String PATH = "src/test/json/GriglieSpecifiche/";
	
	@Test
	void testGenerazioneVariazioneDimensioni() {
		IGriglia<?> grigliaEsatta = generaGrigliaCorretta(PATH + "VariazioneDimensioni.int.json");
		int width = grigliaEsatta.width();
		int height = grigliaEsatta.height();
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<?> grigliaDiPartenza = new GrigliaMatrix(mat, 0);
		IGriglia<?> griglia = 
				new GeneratoreVariazioneDimensioni().generaGrigliaSpecifica(width, height, grigliaDiPartenza);
		
		assertGriglieEqual(griglia, grigliaEsatta);
	}
	
	@Test
	void testGenerazioneSpirale() {
		IGriglia<?> grigliaEsatta = generaGrigliaCorretta(PATH + "Spirale.int.json");
		int width = grigliaEsatta.width();
		int height = grigliaEsatta.height();
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<?> grigliaDiPartenza = new GrigliaMatrix(mat, 0);
		IGriglia<?> griglia = 
				new GeneratoreSpirale().generaGrigliaSpecifica(width, height, grigliaDiPartenza);
		
		assertGriglieEqual(griglia, grigliaEsatta);
	}
	
	@Test
	void testGenerazioneLineaSpezzata() {
		IGriglia<?> grigliaEsatta = generaGrigliaCorretta(PATH + "LineaSpezzata.int.json");
		int width = grigliaEsatta.width();
		int height = grigliaEsatta.height();
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<?> grigliaDiPartenza = new GrigliaMatrix(mat, 0);
		IGriglia<?> griglia = 
				new GeneratoreLineaSpezzata().generaGrigliaSpecifica(width, height, grigliaDiPartenza);
		
		assertGriglieEqual(griglia, grigliaEsatta);
	}
	
	@Test
	void testGenerazioneDoppiaLineaSpezzata() {
		IGriglia<?> grigliaEsatta = generaGrigliaCorretta(PATH + "DoppiaLineaSpezzata.int.json");
		int width = grigliaEsatta.width();
		int height = grigliaEsatta.height();
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<?> grigliaDiPartenza = new GrigliaMatrix(mat, 0);
		IGriglia<?> griglia = 
				new GeneratoreDoppiaLineaSpezzata().generaGrigliaSpecifica(width, height, grigliaDiPartenza);
		
		assertGriglieEqual(griglia, grigliaEsatta);
	}
	
	@Test
	void testGenerazioneScacchiera() {
		IGriglia<?> grigliaEsatta = generaGrigliaCorretta(PATH + "Scacchiera.int.json");
		int width = grigliaEsatta.width();
		int height = grigliaEsatta.height();
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		IGriglia<?> grigliaDiPartenza = new GrigliaMatrix(mat, 0);
		IGriglia<?> griglia = 
				new GeneratoreScacchiera().generaGrigliaSpecifica(width, height, grigliaDiPartenza);
		
		assertGriglieEqual(griglia, grigliaEsatta);
	}
	
	
	
	IGriglia<?> generaGrigliaIniziale(int width, int height) {
		Cella[][] mat = GrigliaMatrix.inizializzaMatrice(width, height);
		return new GrigliaMatrix(mat, 0);
	}
	
	
	IGriglia<?> generaGrigliaCorretta(String path){
		var src = PApplet.loadJSONObject(new File(path));
		var toLoad = src.getJSONArray("test");
		
		if (toLoad==null) {
			System.err.println("Non è stata trovata la griglia test ");
			return null;
		}
		
		return Utils.loadSimple(toLoad);
	}
	
	
	void assertGriglieEqual(IGriglia<?> prima, IGriglia<?> seconda) {
		if(prima.width() != seconda.width() || 
				prima.height() != seconda.height()) {
			fail("Le griglie hanno dimensioni diverse");
		}
		for(int i = 0; i < prima.height(); i++) {
			for(int j = 0; j < prima.width(); j++) {
				assertEquals(prima.getCellaAt(i, j), seconda.getCellaAt(i, j), 
						"Le celle in posizione (" + i + ", " + j + ") sono diverse");
			}
		}
	}

}
