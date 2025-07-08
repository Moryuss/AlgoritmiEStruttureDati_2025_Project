package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import nicolas.StatoCella;

public class OBarraOrizzontale implements CostruttoreOstacolo{

	
	private static final int MAX_RETRY = 10;
	private static final int MAX_WIDTH = 7;
	
	@Override
	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		boolean finito = false;
		int retry = 0;
		Random rand = new Random(randomSeed);
		List<ICella2D> celle = new ArrayList<>();
		
		// Limitiamo width dell'ostacolo o rischierebbe di coprire tutta l'area
		int widthOstacolo = rand.nextInt(Math.min(width, MAX_WIDTH));
		// La height invece è fissa ad 1 e non è necessario salvarla
		
		int tempWidth = widthOstacolo;
		
		int x;
		int y;
		
		do {
			// X ed Y sono le coordinate da cui poi si estende la barra. Di default, la barra si estende verso il basso
			x = rand.nextInt(width);
			y = rand.nextInt(height);
			
			tempWidth = Math.min(width - x, widthOstacolo); // Vediamo se la barra sfora (sempre verso il basso)
			
			// Ora viene controllato che tutti i punti all'interno dell'area siano navigabili
			finito = true;
			for(int i = x; i < x + tempWidth; i++) {
				if(!griglia.isNavigabile(i, y)) {
					// Se non lo è, consuma un retry e ricomincia (x ed y vengono risorteggiati)
					retry++;
					finito = false;
					break;
				}
			}
		}while(retry < MAX_RETRY && !finito); // Se il for non trova ostacoli, questo significa che posso smettere di cercare
		
		for(int i = x; i < x + tempWidth; i++) {
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y));
		}
		
		return celle;
	}

}
