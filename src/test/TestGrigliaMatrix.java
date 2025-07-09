package test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import francesco.GrigliaMatrix;
import francesco.IObstacle;
import francesco.implementazioni.Cella2D;
import nicolas.StatoCella;

public class TestGrigliaMatrix {
	
	@Test
	void testAddOstacolo() {
		var g = GrigliaMatrix.from(1, 1, List.of());
		
		assertTrue(g.getCellaAt(0, 0).isNot(StatoCella.OSTACOLO));
		
		var g2 = g.addObstacle(IObstacle.of(List.of(new Cella2D(StatoCella.OSTACOLO.value(), 0, 0))));
		
		assertTrue(g2.getCellaAt(0, 0).is(StatoCella.OSTACOLO));
		assertFalse(StatoCella.OSTACOLO.check(g.getCellaAt(0, 0).stato()), "La griglia originale deve rimanere inalterata");
		
	}
	
}
