package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import francesco.GrigliaMatrix;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.Ostacolo;
import nicolas.StatoCella;
import utils.Utils;

public class TestGrigliaMatrix {
	
	@Test
	void testAddOstacolo() {
		var g = GrigliaMatrix.from(1, 1, List.of());
		
		assertTrue(g.getCellaAt(0, 0).isNot(StatoCella.OSTACOLO));
		
		var g2 = g.addObstacle(new Ostacolo(List.of(new Cella2D(StatoCella.OSTACOLO.value(), 0, 0))), 0);
		
		assertTrue(g2.getCellaAt(0, 0).is(StatoCella.OSTACOLO));
		assertFalse(StatoCella.OSTACOLO.check(g.getCellaAt(0, 0).stato()), "La griglia originale deve rimanere inalterata");
		
	}
	
	@Test
	void testTipoGrigliaCustom(){
		var g = GrigliaMatrix.from(1, 1, List.of());
		assertEquals(GrigliaMatrix.CUSTOM_TYPE, g.getTipo());
		g = Utils.loadSimple(new File("src/test/json/zigZag_ostacoli.int.json"));
		assertEquals(GrigliaMatrix.CUSTOM_TYPE, g.getTipo());
	}
	
}
