package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Ostacolo;

public interface CostruttoreOstacolo {

	default IGriglia<? extends ICella> costruisciOstacolo(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		List<ICella2D> celle = new ArrayList<>();
		
		celle = generaCelle(width, height, griglia, randomSeed);
		
		Ostacolo result = new Ostacolo(celle);
		
		griglia = griglia.addObstacle(result);
		
		return griglia;
	}
	
	List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed);
}
