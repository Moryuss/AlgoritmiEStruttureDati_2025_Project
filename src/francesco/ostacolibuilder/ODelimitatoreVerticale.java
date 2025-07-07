package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import nicolas.StatoCella;

public class ODelimitatoreVerticale implements CostruttoreOstacolo {

	private static final int MAX_RETRY = 10;
	
	@Override
	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		boolean finito = false;
		int retry = 0;
		Random rand = new Random(randomSeed);
		List<ICella2D> celle = new ArrayList<>();
		
		// La height dell'ostacolo è tutta la height della griglia che è il parametro passato
		int heightOstacolo = height;
		// La width invece è fissa ad 1 e non è necessario salvarla
				
		int x;
		// La y non ha rilevanza
		
		do {
			// X e' la coordinate da cui poi si estende la barra.
			x = rand.nextInt(width);
			
			// Ora viene controllato che tutti i punti del delimitatore siano navigabili
			finito = true;
			for(int i = 0; i < heightOstacolo; i++) {
				if(!griglia.isNavigabile(x, i)) {
					// Se non lo è, consuma un retry e ricomincia (x viene risorteggiato)
					retry++;
					finito = false;
					break;
				}
			}
		}while(retry < MAX_RETRY && !finito); // Se il for non trova ostacoli, questo significa che posso smettere di cercare
		
		for(int i = 0; i < heightOstacolo; i++) {
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), x, i));
		}
		
		return celle;
	}

}
