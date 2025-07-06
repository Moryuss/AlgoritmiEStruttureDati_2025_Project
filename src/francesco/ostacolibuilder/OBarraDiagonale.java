package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.StatoCella;
import francesco.implementazioni.Cella2D;

public class OBarraDiagonale implements CostruttoreOstacolo {

	private static final int MAX_RETRY = 10;
	private static final int MAX_HEIGHT = 7;
	private static final int MAX_WIDTH = 7;
	
	@Override
	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		boolean finito = false;
		int retry = 0;
		Random rand = new Random(randomSeed);
		List<ICella2D> celle = new ArrayList<>();
		
		// Limitiamo width ed height dell'ostacolo o rischierebbe di coprire tutta l'area
		int widthOstacolo = rand.nextInt(Math.min(width, MAX_WIDTH));
		int heightOstacolo = rand.nextInt(Math.min(height, MAX_HEIGHT));
		
		// Essendo la diagonale di un quadrato, basta una sola dimensione
		int tempDim = widthOstacolo;
		
		int x;
		int y;
		
		do {
			// Questi x ed y sono l'origine inteso come il primo punto della barra che si sviluppa
			// in direzione Nord-Est (come bisetrice di I e III quadrante)
			x = rand.nextInt(width);
			y = rand.nextInt(height);
			
			int tempWidth = Math.min(width - x, widthOstacolo); // Serve per evitare sforamenti ma mantenendo la coerenza con le dimensioni estratte
			int tempHeight = Math.min(height - y, heightOstacolo); // Come sopra
			
			tempDim = Math.min(tempWidth, tempHeight); // La dimensione è la più piccola tra width ed height
			
			// Ora viene controllato che tutti i punti della diagonale siano navigabili
			finito = true;
			for(int i = 0; i < tempDim; i++) {
				int tempX = x + i;
				int tempY = y + i;
				if(!griglia.isNavigabile(tempX, tempY)) {
					// Se non lo è, consuma un retry e ricomincia (x ed y vengono risorteggiati)
					retry++;
					finito = false;
					break;
				}
			}
		}while(retry < MAX_RETRY && !finito); // Se il for non trova ostacoli, questo significa che posso smettere di cercare
		
		for(int i = 0; i < tempDim; i++) {
			int tempX = x + i;
			int tempY = y + i;
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), tempX, tempY));
		}
		
		return celle;
	}

}
