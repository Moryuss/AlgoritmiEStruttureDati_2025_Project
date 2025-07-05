package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.StatoCella;
import francesco.implementazioni.Cella2D;

public class OSemplice implements CostruttoreOstacolo{

	private static final int MAX_RETRY = 4;

	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed){
		boolean finito = false;
		int retry = 0;
		Random rand = new Random(randomSeed);
		List<ICella2D> celle = new ArrayList<>();
		
		while(!finito) {
			if(retry >= MAX_RETRY) {
				finito = true;
			}
			else {
				int x = rand.nextInt(width);
				int y = rand.nextInt(height);
				if(griglia.isNavigabile(x, y)) {
					celle.add(new Cella2D(StatoCella.OSTACOLO.value(), x, y));
					finito = true;
				}
				else {
					retry++;
				}
			}
		}
		
		return celle;
	}
}
