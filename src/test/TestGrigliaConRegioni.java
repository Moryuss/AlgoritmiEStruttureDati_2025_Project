package test;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import org.junit.jupiter.api.Test;
import nicolas.GrigliaConOrigineFactory;
import nicolas.RegioneFactory;
import processing.core.PApplet;
import utils.Utils;

public class TestGrigliaConRegioni {
	
	@Test
	void test() {
		var json = PApplet.loadJSONObject(new File("src/test/json/testregioni.int.json"));
		var griglia = Utils.loadSimple(json.getJSONArray("griglia"));
		var grigliaRegione = RegioneFactory.from(GrigliaConOrigineFactory.creaV0(griglia, 2, 19));
		assertEquals(6, grigliaRegione.regioni().length);
		
		var regioni = json.getJSONArray("regioni");
		
		for (int i=0; i<griglia.height(); i++) {
			for (int j=0; j<griglia.width(); j++) {
				int n = regioni.getJSONArray(i).getInt(j, -1);
				var reg = grigliaRegione.getRegioneContenente(j, i);
				if (n==-1) {
					assertFalse(reg.isPresent(), "(%d,%d)=%d".formatted(j, i, n));
				} else {
					assertTrue(reg.isPresent(), "(%d,%d)=%d".formatted(j, i, n));
				}
			}
		}
		
		
		for (int i=0; i<grigliaRegione.regioni().length; i++) {
			for (var c : grigliaRegione.regioni()[i].celle()) {
				assertEquals(i, regioni.getJSONArray(c.y()).getInt(c.x()), "(%d,%d)".formatted(c.x(), c.y()));
			}
		}
		
	}
	
}
