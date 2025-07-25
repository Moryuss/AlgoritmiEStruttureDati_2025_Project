package test;

import static org.junit.jupiter.api.Assertions.*;
import static nicolas.StatoCella.*;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import francesco.GrigliaMatrix;
import processing.core.PApplet;
import utils.Utils;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.Ostacolo;
import nicolas.GrigliaConOrigineFactory;

public class TestGrigliaConOrigine {
	
	@Test
	void test6x6() {
		var griglia = GrigliaMatrix.from(6, 6, List.of(new Ostacolo(List.of(
				new Cella2D(OSTACOLO.value(), 4, 0),
				new Cella2D(OSTACOLO.value(), 3, 4),
				new Cella2D(OSTACOLO.value(), 4, 4)))));
		
		var grigliaO = GrigliaConOrigineFactory.creaV0(griglia, 2, 1);
		
		
		assertTrue(grigliaO.getCellaAt(4, 0).is(OSTACOLO));
		assertTrue(grigliaO.getCellaAt(3, 4).is(OSTACOLO));
		assertTrue(grigliaO.getCellaAt(4, 4).is(OSTACOLO));
		
		
		assertEquals(2, grigliaO.Ox());
		assertEquals(1, grigliaO.Oy());
		assertTrue(grigliaO.getCellaAt(2, 1).is(ORIGINE));
		
		assertTrue(grigliaO.getCellaAt(1, 0).is(REGINA));
		assertTrue(grigliaO.getCellaAt(1, 1).is(REGINA));
		assertTrue(grigliaO.getCellaAt(1, 2).is(REGINA));
		assertTrue(grigliaO.getCellaAt(2, 0).is(REGINA));
		assertTrue(grigliaO.getCellaAt(2, 1).is(REGINA));
		assertTrue(grigliaO.getCellaAt(2, 2).is(REGINA));
		assertTrue(grigliaO.getCellaAt(3, 0).is(REGINA));
		assertTrue(grigliaO.getCellaAt(3, 1).is(REGINA));
		assertTrue(grigliaO.getCellaAt(3, 2).is(REGINA));
		
		assertTrue(grigliaO.getCellaAt(0, 0).is(CONTESTO));
		
		assertTrue(grigliaO.getCellaAt(5, 0).is(COMPLEMENTO));
		
		assertFalse(grigliaO.getCellaAt(4, 5).is(CHIUSURA));
		
		
		
		assertEquals(0, grigliaO.getCellaAt(2,1).distanzaDaOrigine());
		
		assertEquals(1, grigliaO.getCellaAt(2,0).distanzaDaOrigine());
		assertEquals(1, grigliaO.getCellaAt(2,2).distanzaDaOrigine());
		assertEquals(1, grigliaO.getCellaAt(1,1).distanzaDaOrigine());
		assertEquals(1, grigliaO.getCellaAt(3,1).distanzaDaOrigine());
		
		assertEquals(Utils.sqrt2, grigliaO.getCellaAt(1,0).distanzaDaOrigine());
		assertEquals(Utils.sqrt2, grigliaO.getCellaAt(1,2).distanzaDaOrigine());
		assertEquals(Utils.sqrt2, grigliaO.getCellaAt(3,0).distanzaDaOrigine());
		assertEquals(Utils.sqrt2, grigliaO.getCellaAt(3,2).distanzaDaOrigine());
		
		
		grigliaO.forEach((x,y) -> {
			if (grigliaO.getCellaAt(x,y).is(OSTACOLO)) {
				assertTrue(grigliaO.getCellaAt(x,y).isUnreachable());
				assertEquals(Double.POSITIVE_INFINITY, grigliaO.getCellaAt(x,y).distanzaDaOrigine());
			} else if (grigliaO.getCellaAt(x,y).isUnreachable()==false) {
				assertEquals(Utils.distanzaLiberaTra(2, 1, x, y), grigliaO.getCellaAt(x,y).distanzaDaOrigine());
			}
		});
		
		
	}
	
	@Test
	void test6x6_frontiera() {
		var griglia = GrigliaMatrix.from(6, 6, List.of(new Ostacolo(List.of(
				new Cella2D(OSTACOLO.value(), 4, 0),
				new Cella2D(OSTACOLO.value(), 3, 1),
				new Cella2D(OSTACOLO.value(), 4, 1),
				new Cella2D(OSTACOLO.value(), 5, 3)))));
		
		var grigliaO = GrigliaConOrigineFactory.creaV0(griglia, 5, 2);
		var jsona = PApplet.loadJSONArray(new File("src/test/json/testFrontiera.json"));
		
		assertEquals(5, grigliaO.Ox());
		assertEquals(2, grigliaO.Oy());
		
		grigliaO.forEach((x,y)->{
			//System.out.println(x+" "+y);
			var json = jsona.getJSONArray(y).getJSONObject(x);
			assertEquals(json.getBoolean("isOstacolo"), grigliaO.getCellaAt(x, y).is(OSTACOLO), x+" "+y+" ostacolo");
			assertEquals(json.getBoolean("isChiusura"), grigliaO.getCellaAt(x, y).is(CHIUSURA), x+" "+y+" chiusura");
			assertEquals(json.getBoolean("isFrontiera"), grigliaO.getCellaAt(x, y).is(FRONTIERA), x+" "+y+" frontiera");
		});
		
	}
	
}
