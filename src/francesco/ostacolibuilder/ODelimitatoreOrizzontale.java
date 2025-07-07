package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import nicolas.StatoCella;

public class ODelimitatoreOrizzontale implements CostruttoreOstacolo {

	private static final int MAX_RETRY = 10;
	
	@Override
	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		boolean finito = false;
		int retry = 0;
		Random rand = new Random(randomSeed);
		List<ICella2D> celle = new ArrayList<>();
		
		// La width dell'ostacolo è tutta la width della griglia che è il parametro passato
		int widthOstacolo = width;
		// La height invece è fissa ad 1 e non è necessario salvarla
				
		int y;
		// La x non ha rilevanza
		
		do {
			// Y e' la coordinate da cui poi si estende la barra.
			y = rand.nextInt(height);
			
			// Ora viene controllato che tutti i punti del delimitatore siano navigabili
			finito = true;
			for(int i = 0; i < widthOstacolo; i++) {
				if(!griglia.isNavigabile(i, y)) {
					// Se non lo è, consuma un retry e ricomincia (x viene risorteggiato)
					retry++;
					finito = false;
					break;
				}
			}
		}while(retry < MAX_RETRY && !finito); // Se il for non trova ostacoli, questo significa che posso smettere di cercare
		
		for(int i = 0; i < widthOstacolo; i++) {
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
		}
		
		return celle;
	}
}
