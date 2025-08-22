package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import francesco.*;
import nicolas.*;
import utils.Utils;
import matteo.*;
import matteo.Riassunto.Riassunto;
import matteo.Riassunto.TipoRiassunto;

public class TestCompitoTreImpl {

	private boolean debug = false;
	private boolean riassunto = false;
	private boolean monitorON = false;
	private boolean debugMonitor = false;	//stampa il monitor nei metodi specific
	private boolean bit = false;	//stampa lo stato in bit

	IProgressoMonitor monitor = new ProgressoMonitor();
	IProgressoMonitor monitorMin = new ProgressoMonitor();
	IGriglia<?> griglia = null;
	ICompitoTre c;

	@BeforeEach
	public void startingSetup() {
		try {
			c = new CompitoTreImplementation();
			griglia = Utils.loadSimple(new File("src/test/json/testCompitoTre.int.json"));
			//System.out.println("Griglia caricata con successo! Dimensioni: " + griglia.width() + "x" + griglia.height());
			// Stampa la griglia per visualizzare ostacoli e celle navigabili
			//griglia.print();

		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}

	}

	@AfterEach
	public void stampaRiassunto() {
		if (riassunto) {
			Riassunto riassunto = c.getStatisticheEsecuzione()
					.generaRiassunto(TipoRiassunto.VERBOSE);
			riassunto.stampa();
		}

		if (c instanceof CompitoTreImplementation c2 && monitorON && debugMonitor) {
			monitor = c2.getProgress();
			monitorMin = c2.getProgressMin();
			System.out.println("\nOrigine: (" + monitor.getOrigine().x() + "," +monitor.getOrigine().y() + ")");
			System.out.println("Destinazione: (" + monitor.getDestinazione().x() + "," +monitor.getDestinazione().y() + ")");
			System.out.println("Cammino:");
			monitor.getCammino().landmarks()
			.forEach(x->System.out.print("("+ x.x() +","+ x.y()+")"));
			System.out.println("\nDistanza monitor:");
			System.out.println(monitor.getCammino().lunghezza());
			System.out.println("\nCammino minimo:");
			monitorMin.getCammino().landmarks()
			.forEach(x->System.out.print("("+ x.x() +","+ x.y()+")"));
			System.out.println("\nDistanza monitorMin:");
			System.out.println(monitorMin.getCammino().lunghezza());
		}
	}

	private void bitPrint(int numero) {
		String bit = String.format("%8s", Integer.toBinaryString(numero)).replace(' ', '0');
		System.out.println(bit);
	}

	@Test
	void testCasoBaseStartEqualsEnd() throws Exception {

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(0,0);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.ORIGINE.is(end));
		assertNotNull(cammino);

		assertEquals(0.0, cammino.lunghezza());
		assertEquals(0, cammino.lunghezzaTorre(), 0.001);
		assertEquals(0, cammino.lunghezzaAlfiere(), 0.001);


		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());
	}

	@Test
	void testCasoBase_Start_spostato() throws Exception {

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 1,0);
		ICella2D start = g.getCellaAt(1,0);
		ICella2D end = g.getCellaAt(0,0);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.REGINA.is(end));
		assertNotNull(cammino);

		assertEquals(1, cammino.lunghezza());
		assertEquals(1, cammino.lunghezzaTorre(), 0.001);
		assertEquals(0, cammino.lunghezzaAlfiere(), 0.001);


		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());
	}

	@Test
	void testCelleAdiacenti() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(0,1); // adiacente in basso, REGINA

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.REGINA.is(end));
		assertNotNull(cammino);


		assertEquals(1.0, cammino.lunghezza(), 0.001);
		assertEquals(1, cammino.lunghezzaTorre(), 0.001);
		assertEquals(0, cammino.lunghezzaAlfiere(), 0.001);


		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

	}

	@Test
	void testCamminoPercorsoMinimoNoto() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(2,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.CONTESTO.is(end));
		assertNotNull(cammino);


		assertEquals(1+2*Math.sqrt(2), cammino.lunghezza(), 0.001);
		assertEquals(1, cammino.lunghezzaTorre(), 0.001);
		assertEquals(2, cammino.lunghezzaAlfiere(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

	}

	@Test
	void testTipo1_Alfiere() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(3,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.REGINA.is(end));
		assertTrue(StatoCella.CONTESTO.is(end));	//sicuro che sia corretto?
		assertNotNull(cammino);


		assertEquals(3*Math.sqrt(2), cammino.lunghezza(), 0.001);
		assertEquals(3, cammino.lunghezzaAlfiere(), 0.001);
		assertEquals(0, cammino.lunghezzaTorre(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

	}


	@Test
	void testTipo1_Torre_Orizzontale() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(10,0);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.REGINA.is(end));
		assertTrue(StatoCella.CONTESTO.is(end));
		assertNotNull(cammino);

		assertEquals(10, cammino.lunghezza(), 0.001);
		assertEquals(10, cammino.lunghezzaTorre(), 0.001);
		assertEquals(0, cammino.lunghezzaAlfiere(), 0.001);


		assertTrue(StatoCella.CONTESTO.is(end));	//dato che é raggiunto da REGINA

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

	}

	@Test
	void testTipo1_Torre_Verticale() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(0,4);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.CONTESTO.is(end));
		assertNotNull(cammino);

		assertEquals(4, cammino.lunghezza(), 0.001);

		assertTrue(StatoCella.CONTESTO.is(end));	//dato che é raggiunto da REGINA

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

	}


	@Test
	void testTipo1_Alfiere_Torre_Contesto() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(8,2);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.CONTESTO.is(end));
		assertTrue(StatoCella.REGINA.isNot(end));
		assertNotNull(cammino);

		assertTrue(StatoCella.CONTESTO.is(end));	//dato che é raggiunto da Alfiere-torre

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());
	}

	@Test
	void testTipo2_Torre_Alfiere_Complemento() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(5,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.COMPLEMENTO.is(end));
		assertTrue(StatoCella.REGINA.isNot(end));
		assertNotNull(cammino);

		assertTrue(StatoCella.COMPLEMENTO.is(end));	//dato che é raggiunto solo da torre-alfiere

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());
	}


	@Test
	void test_distanzaCome_alfiere_e_torre() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(5,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		assertTrue(StatoCella.COMPLEMENTO.is(end));
		assertTrue(StatoCella.REGINA.isNot(end));
		assertNotNull(cammino);

		assertEquals(cammino.lunghezzaTorre(), 2);
		assertEquals(cammino.lunghezzaAlfiere(), 3);


		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());
	}

	@Test
	void testRicorsione_Cella_Raggiungibile_con_1_Frontiera() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(11,3);
		
		ICammino cammino = c.camminoMin(griglia, start, end);

		if (c instanceof CompitoTreImplementation) {
			monitor = ((CompitoTreImplementation) c).getProgress();
			monitorMin = ((CompitoTreImplementation) c).getProgressMin();
		}
		end = monitorMin.getDestinazione();

		if(bit) {
			System.out.print("\nStart: ");
			bitPrint(start.stato());
			System.out.print("\nEnd: ");
			bitPrint(end.stato());
		}
		assertTrue(StatoCella.OSTACOLO.isNot(end));
		assertTrue(StatoCella.ORIGINE.is(start));
		//assertTrue(StatoCella.DESTINAZIONE.is(end), "Not dest");
		assertTrue(StatoCella.COMPLEMENTO.isNot(end));
		assertTrue(StatoCella.REGINA.isNot(end));
		assertNotNull(cammino);

		assertEquals(12.242640687119286, cammino.lunghezza(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

		if(debug) {
			System.out.println("PERCORSO MINIMO TROVATO 1 FRONTIERA/LANDMARK");
			System.out.println("lunghezza totale: " + cammino.lunghezza());
			System.out.println("Celle Torre: " + cammino.lunghezzaTorre());
			System.out.println("Celle Alfiere: " + cammino.lunghezzaAlfiere());

			cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
			System.out.println("#############################");
		} 
	}


	//Griglia a forma di SPIRALE per testare molti landmark in ricorsione
	@Test
	void testRicorsione_Spirale() throws Exception {

		try {

			griglia = Utils.loadSimple(new File("src/test/json/spirale_ostacoli.int.json"));
			//System.out.println("Griglia caricata con successo! Dimensioni: " + griglia.width() + "x" + griglia.height());
			// Stampa la griglia per visualizzare ostacoli e celle navigabili
			//griglia.print();

		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}


		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(10,5);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);

		assertEquals(44.89949493661167, cammino.lunghezza(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

		if(debug) {
			System.out.println("PERCORSO MINIMO TROVATO SPIRALE");

			System.out.println("lunghezza totale: " + cammino.lunghezza());
			System.out.println("Celle Torre: " + cammino.lunghezzaTorre());
			System.out.println("Celle Alfiere: " + cammino.lunghezzaAlfiere());

			System.out.println(cammino.lunghezza());
			cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
			System.out.println("#############################");
		}
	}

	@Test
	void test_Infinito() throws Exception {


		try {

			griglia = Utils.loadSimple(new File("src/test/json/passaggio_bloccato.int.json"));
			//System.out.println("Griglia caricata con successo! Dimensioni: " + griglia.width() + "x" + griglia.height());
			// Stampa la griglia per visualizzare ostacoli e celle navigabili
			//griglia.print();

		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}


		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(1,1);
		ICella2D end = g.getCellaAt(39,19);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);

		assertEquals(Double.POSITIVE_INFINITY, cammino.lunghezza(), 0.001);

		assertEquals(0, cammino.landmarks().size());

		if(debug) {
			System.out.println("PERCORSO MINIMO TROVATO INFINITO");
			System.out.println("lunghezza totale: " + cammino.lunghezza());
			System.out.println("Celle Torre: " + cammino.lunghezzaTorre());
			System.out.println("Celle Alfiere: " + cammino.lunghezzaAlfiere());

			System.out.println(cammino.lunghezza());
			cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
			System.out.println("#############################");
		}
	}
	@Test
	void testRicorsione_ZigZag() throws Exception {

		try {

			griglia = Utils.loadSimple(new File("src/test/json/zigZag_ostacoli.int.json"));
			//System.out.println("Griglia caricata con successo! Dimensioni: " + griglia.width() + "x" + griglia.height());
			// Stampa la griglia per visualizzare ostacoli e celle navigabili
			//griglia.print();

		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}


		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(0,6);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);

		assertEquals(86.56854249492376, cammino.lunghezza(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

		if(debug) {
			System.out.println("PERCORSO MINIMO TROVATO: ZIGZAG");
			System.out.println("lunghezza totale: " + cammino.lunghezza());
			System.out.println("Celle Torre: " + cammino.lunghezzaTorre());
			System.out.println("Celle Alfiere: " + cammino.lunghezzaAlfiere());

			System.out.println(cammino.lunghezza());
			cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
			System.out.println("#############################");
		}
	}

	@Test
	void testProgressoMonitor() throws Exception {

		try {

			griglia = Utils.loadSimple(new File("src/test/json/zigZag_ostacoli.int.json"));
			//System.out.println("Griglia caricata con successo! Dimensioni: " + griglia.width() + "x" + griglia.height());
			// Stampa la griglia per visualizzare ostacoli e celle navigabili
			//griglia.print();

		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}


		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0,0);
		ICella2D end = g.getCellaAt(0,6);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);

		assertEquals(86.56854249492376, cammino.lunghezza(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

		if(debug) {
			System.out.println("PERCORSO MINIMO TROVATO: ZIGZAG");
			System.out.println("lunghezza totale: " + cammino.lunghezza());
			System.out.println("Celle Torre: " + cammino.lunghezzaTorre());
			System.out.println("Celle Alfiere: " + cammino.lunghezzaAlfiere());

			System.out.println(cammino.lunghezza());
			cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
			System.out.println("#############################");
		}

		IProgressoMonitor monitor = new ProgressoMonitor();
		IProgressoMonitor monitorMin = new ProgressoMonitor();

		if (c instanceof CompitoTreImplementation) {
			monitor = ((CompitoTreImplementation) c).getProgress();
			monitorMin = ((CompitoTreImplementation) c).getProgressMin();
		}

		assertEquals(cammino.landmarks().size(), monitorMin.getCammino().landmarks().size());
		for(int i = 0; i < cammino.landmarks().size(); i++) {
			assertEquals(cammino.landmarks().get(i).x(), monitorMin.getCammino().landmarks().get(i).x());
			assertEquals(cammino.landmarks().get(i).y(), monitorMin.getCammino().landmarks().get(i).y());
		}

		if(monitorON && debugMonitor) {
			System.out.println("----------------------------------------------------");
			System.out.println("MONITOR");
			System.out.println("Origine: (" + monitor.getOrigine().x() + "," +monitor.getOrigine().y() + ")");
			System.out.println("Destinazione: (" + monitor.getDestinazione().x() + "," +monitor.getDestinazione().y() + ")");
			System.out.println("LandMarks");
			monitor.getCammino().landmarks()
			.forEach(x->System.out.print("("+ x.x() +","+ x.y()+")"));
			System.out.println("\n ----------------------------------------------------");
			System.out.println("MONITOR MINIMO ");
			System.out.println("Origine: (" + monitorMin.getOrigine().x() + "," +monitorMin.getOrigine().y() + ")");
			System.out.println("Destinazione: (" + monitorMin.getDestinazione().x() + "," +monitorMin.getDestinazione().y() + ")");
			System.out.println("LandMarks cammino minimo");
			monitorMin.getCammino().landmarks()
			.forEach(x->System.out.print("("+ x.x() +","+ x.y()+")"));
			System.out.println("\n ----------------------------------------------------");

		}
	}



	@Test
	void testInterruzioneSuRichiesta() throws Exception {
		// Carica la griglia
		try {
			griglia = Utils.loadSimple(new File("src/test/json/zigZag_ostacoli.int.json"));
		} catch (Exception e) {
			fail("Errore durante il caricamento della griglia: " + e.getMessage());
		}

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0, 0);
		ICella2D start = g.getCellaAt(0, 0);
		ICella2D end = g.getCellaAt(0, 6);


		// Crea un thread separato per l'esecuzione
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<ICammino> future = executor.submit(() -> c.camminoMin(griglia, start, end));

		// Interrompi dopo un breve ritardo
		Thread.sleep(100);
		((IInterrompibile)c).interrupt();

		try {
			ICammino cammino = future.get(1, TimeUnit.SECONDS);


			if (debug) {
				System.out.println("TEST INTERRUZIONE SU RICHIESTA");
				System.out.println("Cammino dopo interruzione:");
				cammino.landmarks().forEach(x -> System.out.print("(" + x.x() + "," + x.y() + ") "));
				System.out.println("\n#############################");
			}

		} catch (TimeoutException e) {
			fail("L'esecuzione non è stata interrotta tempestivamente");
		} finally {
			executor.shutdownNow();
		}
	}
	@Test
	void testInterruzioneSuTimeout() throws Exception {

		// Carica una griglia più complessa per testare il timeout
		try {
			griglia = Utils.loadSimple(new File("src/test/json/zigZag_ostacoli.int.json"));
		} catch (Exception e) {
			fail("Errore durante il caricamento della griglia: " + e.getMessage());
		}

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0, 0);
		ICella2D start = g.getCellaAt(0, 0);
		ICella2D end = g.getCellaAt(0,6);
		
		((CompitoTreImplementation)c).setTimeout(500, TimeUnit.MILLISECONDS);

		long startTime = System.currentTimeMillis();
		ICammino cammino = c.camminoMin(griglia, start, end);
		long duration = System.currentTimeMillis() - startTime;

		// Verifica che l'esecuzione sia stata interrotta
		assertTrue(duration >= 500 && duration < 1000, 
				"L'esecuzione dovrebbe essere interrotta dopo circa 500ms");

		if (debug) {
			System.out.println("TEST INTERRUZIONE SU TIMEOUT");
			System.out.println("Tempo di esecuzione: " + duration + "ms");
			System.out.println("Cammino dopo timeout:");
			cammino.landmarks().forEach(x -> System.out.print("(" + x.x() + "," + x.y() + ") "));
			System.out.println("\n#############################");
		}
	}
	@Test
	void test_Cache_zigZag() throws Exception {

		// Carica una griglia più complessa per testare il timeout
		try {
			griglia = Utils.loadSimple(new File("src/test/json/zigZag_ostacoli.int.json"));

		} catch (Exception e) {
			fail("Errore durante il caricamento della griglia: " + e.getMessage());
		}

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0, 0);
		ICella2D start = g.getCellaAt(0, 0);
		ICella2D end = g.getCellaAt(0,6);

		var c2 = (CompitoTreImplementation)c;
		c2.setConfiguration(ConfigurationMode.PERFORMANCE_NO_CACHE);
		ICammino cammino = c.camminoMin(griglia, start, end);

		c2.setConfiguration(ConfigurationMode.PERFORMANCE);
		ICammino camminoCache = c.camminoMin(griglia, start, end);

		// Verifica che i cammini siano uguali
		assertNotNull(cammino);
		assertNotNull(camminoCache);
		assertEquals(cammino.lunghezza(), camminoCache.lunghezza(), 0.001);
		assertEquals(cammino.lunghezzaTorre(), camminoCache.lunghezzaTorre(), 0.001);
		assertEquals(cammino.lunghezzaAlfiere(), camminoCache.lunghezzaAlfiere(), 0.001);
		assertEquals(cammino.landmarks().size(), camminoCache.landmarks().size());
		for (int i = 0; i < cammino.landmarks().size(); i++) {
			assertEquals(cammino.landmarks().get(i).x(), camminoCache.landmarks().get(i).x());
			assertEquals(cammino.landmarks().get(i).y(), camminoCache.landmarks().get(i).y());
		}

		if (debug) {
			System.out.println("TEST Cache");
			System.out.println("Cammino ");
			cammino.landmarks().forEach(x -> System.out.print("(" + x.x() + "," + x.y() + ") "));
			System.out.println("\n#############################");
		}
	}
	@Test
	void test_Cache_spirale() throws Exception {

		// Carica una griglia più complessa per testare il timeout
		try {
			griglia = Utils.loadSimple(new File("src/test/json/spirale_ostacoli.int.json"));

		} catch (Exception e) {
			fail("Errore durante il caricamento della griglia: " + e.getMessage());
		}

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0, 0);
		ICella2D start = g.getCellaAt(0, 0);
		ICella2D end = g.getCellaAt(10,5);

		var c2 = (CompitoTreImplementation)c;
		c2.setConfiguration(ConfigurationMode.PERFORMANCE_NO_CACHE);
		ICammino cammino = c.camminoMin(griglia, start, end);

		c2.setConfiguration(ConfigurationMode.PERFORMANCE);
		ICammino camminoCache = c.camminoMin(griglia, start, end);

		// Verifica che i cammini siano uguali
		assertNotNull(cammino);
		assertNotNull(camminoCache);
		assertEquals(cammino.lunghezza(), camminoCache.lunghezza(), 0.001);
		assertEquals(cammino.lunghezzaTorre(), camminoCache.lunghezzaTorre(), 0.001);
		assertEquals(cammino.lunghezzaAlfiere(), camminoCache.lunghezzaAlfiere(), 0.001);
		assertEquals(cammino.landmarks().size(), camminoCache.landmarks().size());
		for (int i = 0; i < cammino.landmarks().size(); i++) {
			assertEquals(cammino.landmarks().get(i).x(), camminoCache.landmarks().get(i).x());
			assertEquals(cammino.landmarks().get(i).y(), camminoCache.landmarks().get(i).y());
		}

		if (debug) {
			System.out.println("TEST Cache");
			System.out.println("Cammino ");
			cammino.landmarks().forEach(x -> System.out.print("(" + x.x() + "," + x.y() + ") "));
			System.out.println("\n#############################");
		}
	}

	@Test
	void test_conPassaggioDiCompitoDue() throws Exception {

		try {

			griglia = Utils.loadSimple(new File("src/test/json/spirale_ostacoli.int.json"));
			//System.out.println("Griglia caricata con successo! Dimensioni: " + griglia.width() + "x" + griglia.height());
			// Stampa la griglia per visualizzare ostacoli e celle navigabili
			//griglia.print();

		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}


		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0, 0);
		ICella2D end = g.getCellaAt(10,5);

		ICammino cammino = c.camminoMin(griglia, start, end, CompitoDueImpl.V0);	//Differenza rispetto a testRicorsione_Spirale

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);

		assertEquals(44.89949493661167, cammino.lunghezza(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

		if(debug) {
			System.out.println("PERCORSO MINIMO TROVATO: ZIGZAG");
			System.out.println("lunghezza totale: " + cammino.lunghezza());
			System.out.println("Celle Torre: " + cammino.lunghezzaTorre());
			System.out.println("Celle Alfiere: " + cammino.lunghezzaAlfiere());

			System.out.println(cammino.lunghezza());
			cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
			System.out.println("#############################");
		}
	}

	@Test
	public void test_performanceConSvuotaFrontiera() throws Exception {

		try {
			griglia = Utils.loadSimple(new File("src/test/json/zigZag_ostacoli.int.json"));

		} catch (Exception e) {
			fail("Errore durante il caricamento della griglia: " + e.getMessage());
		}

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0, 0);
		ICella2D start = g.getCellaAt(0, 0);
		ICella2D end = g.getCellaAt(0,6);

		if(c instanceof CompitoTreImplementation){
			((CompitoTreImplementation) c).setConfiguration(ConfigurationMode.PERFORMANCE_SVUOTA_FRONTIERA);
			//uso di svuotaFrontiera
		}
		else {
			fail("Compito non implementa CompitoTreImplementation");
		}


		ICammino cammino = c.camminoMin(griglia, start, end, CompitoDueImpl.V0);	//Differenza rispetto a testRicorsione_Spirale

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);

		assertEquals(86.56854249492376, cammino.lunghezza(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

		if(debug) {
			System.out.println("PERCORSO MINIMO TROVATO: ZIGZAG");
			System.out.println("lunghezza totale: " + cammino.lunghezza());
			System.out.println("Celle Torre: " + cammino.lunghezzaTorre());
			System.out.println("Celle Alfiere: " + cammino.lunghezzaAlfiere());

			System.out.println(cammino.lunghezza());
			cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
			System.out.println("#############################");
		}

	}

	@Test
	public void test_modalitaInseritaManualmente() throws Exception {

		try {

			griglia = Utils.loadSimple(new File("src/test/json/spirale_ostacoli.int.json"));
			//System.out.println("Griglia caricata con successo! Dimensioni: " + griglia.width() + "x" + griglia.height());
			// Stampa la griglia per visualizzare ostacoli e celle navigabili
			//griglia.print();

		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}
		if(c instanceof CompitoTreImplementation){
			//CUSTOM MODE
			((CompitoTreImplementation) c).setConfiguration(
					CamminoConfiguration.custom(
							ConfigurationFlag.CONDIZIONE_RAFFORZATA,
							ConfigurationFlag.SVUOTA_FRONTIERA,
							ConfigurationFlag.STATE_CHECK));
			
			assertTrue(((CompitoTreImplementation) c).
					getConfiguration().hasAllFlags(
							ConfigurationFlag.CONDIZIONE_RAFFORZATA,
							ConfigurationFlag.SVUOTA_FRONTIERA));
//							ConfigurationFlag.STATE_CHECK));
			
			assertFalse(((CompitoTreImplementation) c).
					getConfiguration().hasFlag(
							ConfigurationFlag.MONITOR_ENABLED));

		}
		else {
			fail("Compito non implementa CompitoTreImplementation");
		}

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2D start = g.getCellaAt(0, 0);
		ICella2D end = g.getCellaAt(10,5);

		ICammino cammino = c.camminoMin(griglia, start, end, CompitoDueImpl.V0);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);

		assertEquals(44.89949493661167, cammino.lunghezza(), 0.001);

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

		if(debug) {
			System.out.println("PERCORSO MINIMO TROVATO: ZIGZAG");
			System.out.println("lunghezza totale: " + cammino.lunghezza());
			System.out.println("Celle Torre: " + cammino.lunghezzaTorre());
			System.out.println("Celle Alfiere: " + cammino.lunghezzaAlfiere());

			System.out.println(cammino.lunghezza());
			cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
			System.out.println("#############################");
		}

	}
}
