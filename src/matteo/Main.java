package matteo;

import java.nio.file.Path;
import java.nio.file.Paths;

import francesco.ICella;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import nicolas.GrigliaConOrigineFactory;
import nicolas.ICella2;
import nicolas.IGrigliaConOrigine;
import nicolas.StatoCella;

public class Main  {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		LettoreGriglia lettore = new LettoreGriglia();
		Path configFile = Paths.get("config.json"); // Assicurati che config.json sia nella directory principale del progetto

		IGriglia<ICella> griglia = null;
		try {
			griglia = (IGriglia<ICella>) lettore.crea(configFile);
			System.out.println("Griglia caricata con successo! Dimensioni: " + griglia.width() + "x" + griglia.height());
			// Stampa la griglia per visualizzare ostacoli e celle navigabili
			griglia.print();
		} catch (Exception e) {
			System.err.println("Errore durante il caricamento della griglia: " + e.getMessage());
			e.printStackTrace();
			return; // Esci se la griglia non può essere caricata
		}

		ICompitoTre c3 = new CompitoTreImpl_NoRequisitiFunzionali();

		IGrigliaConOrigine g = GrigliaConOrigineFactory.creaV0(griglia, 0, 0); //impl nicolas
		
		
		ICella2 start = g.getCellaAt(0, 0);
//		ICella2 end =  g.getCellaAt(g.width()-1, g.height()-1); // esplode 
		ICella2 end =  g.getCellaAt(11,3); 
	
		if(StatoCella.OSTACOLO.is(end.stato())) {
			System.out.println("Cella è ostacolo");
			return;
		}

		ICammino cammino = null;
		
		
		try {
			cammino = c3.camminoMin(griglia, start, end);
		} catch (Exception e2) {
			System.err.println(e2);
		}

		System.out.println("Risultati finali");
		System.out.println(cammino.lunghezza());
		cammino.landmarks().forEach(
				x->System.out.println("[" + x.x() + ", " + x.y() + "]"));

	}
}
