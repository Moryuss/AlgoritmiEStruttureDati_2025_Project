package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.StatoCella;
import francesco.implementazioni.Cella2D;

public class OAgglomerato implements CostruttoreOstacolo {

	private static final int MAX_RETRY = 3;
	private static final int MAX_WIDTH = 4;
	private static final int MAX_HEIGHT = 7;
	
	@Override
	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		boolean finito = false;
		int retry = 0;
		Random rand = new Random(randomSeed);
		List<ICella2D> celle = new ArrayList<>();
		
		// Limitiamo width ed height dell'ostacolo o rischierebbe di coprire tutta l'area
		int widthOstacolo = rand.nextInt(Math.min(width, MAX_WIDTH));
		int heightOstacolo = rand.nextInt(Math.min(height, MAX_HEIGHT));
		
		int tempWidth = widthOstacolo;
		int tempHeight = heightOstacolo;
		
		int x;
		int y;
		
		do {
			// Questi x ed y sono l'origine inteso come l'angolo in alto a sinistra
			x = rand.nextInt(width);
			y = rand.nextInt(height);
			
			tempWidth = Math.min(width - x, widthOstacolo); // Serve per evitare sforamenti ma mantenendo la coerenza con le dimensioni estratte
			tempHeight = Math.min(height - y, heightOstacolo); // Come sopra
			
			// Ora viene controllato che tutti i punti all'interno dell'area siano navigabili
			finito = true;
			for(int i = y; i < y + tempHeight; i++) {
				for(int j = x; j < x + tempWidth; j++) {
					if(!griglia.isNavigabile(j, i)) {
						// Se non lo è, consuma un retry e ricomincia (x ed y vengono risorteggiati)
						retry++;
						finito = false;
						break;
					}
				}
			}
		}while(retry < MAX_RETRY && !finito); // Se il for non trova ostacoli, questo significa che posso smettere di cercare
		
		for(int i = y; i < y + tempHeight; i++) {
			for(int j = x; j < x + tempWidth; j++) {
				celle.add(new Cella2D(StatoCella.OSTACOLO.value(), j, i));
			}
		}
		
		return celle;
	}


}
