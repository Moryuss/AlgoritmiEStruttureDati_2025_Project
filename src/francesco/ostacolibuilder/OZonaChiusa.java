package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import nicolas.StatoCella;

public class OZonaChiusa implements CostruttoreOstacolo {

	private static final int MAX_RETRY = 10;
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
			
			// Ora viene controllato che tutti i punti della cornice siano navigabili.
			// Per fare ciò, controlliamo le 4 righe di cui è composta la cornice.
			// Per evitare iterazioni inutili, controlliamo se finito=false prima di iterare i for dopo il primo.
			finito = true;
			// Lato superiore della cornice
			for(int i = x; i < x + tempWidth; i++) {
				if(!griglia.isNavigabile(i, y)) {
					// Se non lo è, consuma un retry e ricomincia (x ed y vengono risorteggiati)
					retry++;
					finito = false;
					break;
				}
			}
			
			if(finito) {
				// Lato inferiore della cornice
				for(int i = x; i < x + tempWidth; i++) {
					if(!griglia.isNavigabile(i, y + tempHeight - 1)) {
						retry++;
						finito = false;
						break;
					}
				}
			}
			
			if(finito) {
				// Lato sinistro della cornice (gli offset sono perche' quelle celle sono gia' state controllate)
				for(int i = y + 1; i < y + tempHeight - 1; i++) {
					if(!griglia.isNavigabile(x, i)) {
						retry++;
						finito = false;
						break;
					}
				}
			}
			
			if(finito) {
				// Lato destro della cornice (gli offset sono perche' quelle celle sono gia' state controllate)
				for(int i = y + 1; i < y + tempHeight - 1; i++) {
					if(!griglia.isNavigabile(x + tempWidth - 1, i)) {
						retry++;
						finito = false;
						break;
					}
				}
			}
			
		}while(retry < MAX_RETRY && !finito); // Se il for non trova ostacoli, questo significa che posso smettere di cercare
		
		for(int i = x; i < x + tempWidth; i++) {
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y)); // Lato superiore
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), i, y + tempHeight - 1)); // Lato inferiore
		}
		
		for(int i = y + 1; i < y + tempHeight - 1; i++) {
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), x, i)); // Lato sinistro
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), x + tempWidth - 1, i)); // Lato destro
		}
		
		return celle;
	}

}
