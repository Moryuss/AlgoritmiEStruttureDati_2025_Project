package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import francesco.*;
import nicolas.*;
import matteo.*;

public class TestCompitoTreImpl {

	private boolean debug = false;
	private boolean riassunto = false;
	private boolean monitorToggle = false;

	IGriglia<ICella> griglia = null;
	ICompitoTre c = new CompitoTreImplementation();

	@BeforeEach
	public void startingSetup() {
		try {

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
		if (c instanceof CompitoTreImplementation && riassunto) {
			System.out.println(((CompitoTreImplementation) c).getReport());
		}
		IProgressoMonitor monitor = new ProgressoMonitor();

		if (c instanceof CompitoTreImplementation) {
			monitor = ((CompitoTreImplementation) c).getProgress();
		}

		if(monitorToggle) {
			System.out.println("Origine: (" + monitor.getOrigine().x() + "," +monitor.getOrigine().y() + ")");
			System.out.println("Destinazione: (" + monitor.getDestinazione().x() + "," +monitor.getDestinazione().y() + ")");
			monitor.getCammino().landmarks()
			.forEach(x->System.out.print("("+ x.x() +","+ x.y()+")"));
		}
	}

	@Test
	void testCasoBaseStartEqualsEnd() throws Exception {

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(0,0);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2 start = g.getCellaAt(1,0);
		ICella2 end = g.getCellaAt(0,0);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(0,1); // adiacente in basso, REGINA

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(2,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(3,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);

		assertEquals(3*Math.sqrt(2), cammino.lunghezza(), 0.001);
		assertEquals(3, cammino.lunghezzaAlfiere(), 0.001);
		assertEquals(0, cammino.lunghezzaTorre(), 0.001);


		assertTrue(StatoCella.CONTESTO.is(end));	//dato che é raggiunto da REGINA

		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());

	}


	@Test
	void testTipo1_Torre_Orizzontale() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(10,0);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(0,4);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(8,2);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(5,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(5,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(11,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(10,5);

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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(0,6);

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
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(0,6);

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

		if (c instanceof CompitoTreImplementation) {
			monitor = ((CompitoTreImplementation) c).getProgress();
		}

		if(monitorToggle) {
			System.out.println("Origine: (" + monitor.getOrigine().x() + "," +monitor.getOrigine().y() + ")");
			System.out.println("Destinazione: (" + monitor.getDestinazione().x() + "," +monitor.getDestinazione().y() + ")");
			monitor.getCammino().landmarks()
			.forEach(x->System.out.print("("+ x.x() +","+ x.y()+")"));
		}
	}
}
