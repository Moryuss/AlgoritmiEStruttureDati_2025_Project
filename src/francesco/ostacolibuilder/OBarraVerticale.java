package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.StatoCella;
import francesco.implementazioni.Cella2D;

public class OBarraVerticale implements CostruttoreOstacolo {

	private static final int MAX_RETRY = 3;
	private static final int MAX_HEIGHT = 7;
	
	@Override
	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		boolean finito = false;
		int retry = 0;
		Random rand = new Random(randomSeed);
		List<ICella2D> celle = new ArrayList<>();
		
		// Limitiamo height dell'ostacolo o rischierebbe di coprire tutta l'area
		int heightOstacolo = rand.nextInt(Math.min(height, MAX_HEIGHT));
		// La width invece è fissa ad 1 e non è necessario salvarla
		
		int tempHeight = heightOstacolo;
		
		int x;
		int y;
		
		do {
			// X ed Y sono le coordinate da cui poi si estende la barra. Di default, la barra si estende verso il basso
			x = rand.nextInt(width);
			y = rand.nextInt(height);
			
			tempHeight = Math.min(height - y, heightOstacolo); // Vediamo se la barra sfora (sempre verso il basso)
			
			// Ora viene controllato che tutti i punti all'interno dell'area siano navigabili
			finito = true;
			for(int i = y; i < y + tempHeight; i++) {
				if(!griglia.isNavigabile(x, i)) {
					// Se non lo è, consuma un retry e ricomincia (x ed y vengono risorteggiati)
					retry++;
					finito = false;
					break;
				}
			}
		}while(retry < MAX_RETRY && !finito); // Se il for non trova ostacoli, questo significa che posso smettere di cercare
		
		for(int i = y; i < y + tempHeight; i++) {
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), x, i));
		}
		
		return celle;
	}

}
