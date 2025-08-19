package test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Objects;

import org.junit.jupiter.api.Test;

class Test_CollisioniHash {

	public void test_collisione_Manuale() {
		Point2D p1 = new Point2D.Double(-47, -33);
		Point2D p2 = new Point2D.Double(-48, -32);

		Point2D p3 = new Point2D.Double(-47, -32);
		Point2D p4 = new Point2D.Double(-48, -33);

		Point2D p5 = new Point2D.Double(-47, 32);
		Point2D p6 = new Point2D.Double(-48, 33);

		printCollision(p1, p2);
		printCollision(p3, p4);
		printCollision(p5, p6);
	}

	private static void printCollision(Point2D a, Point2D b) {
		int h1 = a.hashCode();
		int h2 = b.hashCode();

		System.out.printf("Punto %s -> hash = %d%n", a, h1);
		System.out.printf("Punto %s -> hash = %d%n", b, h2);

		if (h1 == h2) {
			System.out.println("⚠️ COLLISIONE TROVATA!\n");
		} else {
			System.out.println("Hash diversi.\n");
		}
	}

	@Test
	public void test_HashCode() {
		Point2D p1 = new Point(-47, -33);
		Point2D p2 = new Point(-48, -32);

		Point2D p3 = new Point(-47, -32);
		Point2D p4 = new Point(-48, -33);

		Point2D p5 = new Point(-47, 32);
		Point2D p6 = new Point(-48, 33);
		
		Point2D p7 = new Point(93, 66);
		Point2D p8 = new Point(30, 69);
		
		

		assertEquals(p1.hashCode(), p2.hashCode());
		assertEquals(p3.hashCode(), p4.hashCode());
		assertEquals(p5.hashCode(), p6.hashCode());
		assertEquals(p7.hashCode(), p8.hashCode());

		assertNotEquals(p1.hashCode(), p3.hashCode());
		assertNotEquals(p2.hashCode(), p4.hashCode());
		assertNotEquals(p5.hashCode(), p1.hashCode());

	}

	@Test
	public void test_Objects_Hash() {
		Point2D p1 = new Point(-47, -33);
		Point2D p2 = new Point(-48, -32);

		Point2D p3 = new Point(-47, -32);
		Point2D p4 = new Point(-48, -33);

		Point2D p5 = new Point(-47, 32);
		Point2D p6 = new Point(-48, 33);

		assertEquals(Objects.hash(p1,p3,p5), 
				Objects.hash(p2,p4,p6));
	}

	public void test_search_Hash() {

		for(int i=0; i< 100; i++) {
			for(int j=0; j< 100; j++) {
				for(int k=0; k< 100; k++) {
					for(int l=0; l< 100; l++) {
						Point2D p = new Point(i, j);
						Point2D q = new Point(k, l);
						int hash = p.hashCode();
						int hash2 = q.hashCode();
						if (hash == hash2 && !p.equals(q)) {
							System.out.printf("⚠️ Collisione trovata tra %s e %s -> hash = %d%n", p, q, hash);
						}
						
					}
				}
			}
		}
	}


}
