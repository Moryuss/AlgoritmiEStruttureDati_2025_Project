package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import francesco.*;
import nicolas.*;
import matteo.*;

public class TestCompitoTreImpl {
	
	IGriglia<ICella> griglia = null;
	ICompitoTre c = new CompitoTreImpl_NoRequisitiFunzionali();

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

	@Test
	void testCasoBaseStartEqualsEnd() throws Exception {

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(0,0);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);
		assertEquals(0.0, cammino.lunghezza());
		
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
	
	
	/**
	 * Importante: tutte le ricorsioni perora hanno un limite, altimento sono infinite
	 * Per ora faccio i test di frontiere che sono raggiunte in un piccolo numero di ricorsioni
	 */
	
	@Test
	void testRicorsione_Cella_Raggiungibile_con_1_Frontiera() throws Exception {
		startingSetup();
		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0,0);
		ICella2 start = g.getCellaAt(0,0);
		ICella2 end = g.getCellaAt(11,3);

		ICammino cammino = c.camminoMin(griglia, start, end);

		assertTrue(StatoCella.OSTACOLO.isNot(end.stato()));
		assertNotNull(cammino);
		
		assertEquals(start.x(), cammino.landmarks().get(0).x());
		assertEquals(start.y(), cammino.landmarks().get(0).y());
		assertEquals(end.x(), cammino.landmarks().get(cammino.landmarks().size()-1).x());
		assertEquals(end.y(), cammino.landmarks().get(cammino.landmarks().size()-1).y());
	
		System.out.println("PERCORSO MINIMO TROVATO 1 FRONTIERA/LANDMARK");
		System.out.println(cammino.lunghezza());
		cammino.landmarks().forEach(x->System.out.println("("+ x.x() +","+ x.y()+")"+"==>"));
		System.out.println("#############################");
	}
}
