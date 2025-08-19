package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import francesco.GrigliaMatrix;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.generatoriDisposizioni.GeneratoreDoppiaLineaSpezzata;
import francesco.generatoriDisposizioni.GeneratoreLineaSpezzata;
import francesco.generatoriDisposizioni.GeneratoreScacchiera;
import francesco.generatoriDisposizioni.GeneratoreSpirale;
import francesco.generatoriDisposizioni.GeneratoreVariazioneDimensioni;
import francesco.implementazioni.Cella;
import francesco.implementazioni.LettoreGriglia;
import matteo.CamminoConfiguration;
import matteo.CompitoTreImplementation;
import matteo.ConfigurationFlag;
import matteo.ConfigurationMode;
import matteo.ICammino;
import matteo.Riassunto.IStatisticheEsecuzione;
import nicolas.CompitoDueImpl;
import nicolas.ICompitoDue;
import nicolas.IGrigliaConOrigine;
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
	
	@Test 
	void testStackoverflow() {
		IGriglia<?> griglia = new LettoreGriglia().crea(Path.of(PATH + "StackOF.json"));
//		ICella2D start = griglia.getCellaAt(0,0);
//		CoordinateCella origine = new CoordinateCella(0, 0);
		ICompitoDue due = CompitoDueImpl.V0;
//		ConfigurationMode tre = ConfigurationMode.PERFORMANCE_FULL;
		CamminoConfiguration tre = CamminoConfiguration.custom(ConfigurationFlag.CONDIZIONE_RAFFORZATA, ConfigurationFlag.MONITOR_ENABLED,
				ConfigurationFlag.SORTED_FRONTIERA, ConfigurationFlag.SVUOTA_FRONTIERA);
		
		IGrigliaConOrigine grigliaOrigine = due.calcola(griglia, 0, 0);
		
		ICella2D start = grigliaOrigine.getCellaAt(0,0);
		ICella2D destinazione = grigliaOrigine.getCellaAt(grigliaOrigine.width()-1, grigliaOrigine.height()-1);
		
		CompitoTreImplementation treImplementazione = new CompitoTreImplementation(tre);
		ICammino cammino = treImplementazione.camminoMin(grigliaOrigine, start, destinazione);
		
		System.out.println(treImplementazione.getReport());
		IStatisticheEsecuzione statistiche = treImplementazione.getStatisticheEsecuzione();
		if(!statistiche.isCalcoloInterrotto()) {
			fail("Il calcolo non e' stato interrotto.");
		}
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
				assertEquals(prima.getCellaAt(j, i), seconda.getCellaAt(j, i), 
						"Le celle in posizione (" + i + ", " + j + ") sono diverse");
			}
		}
	}

}
