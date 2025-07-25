package francesco.ostacolibuilder;

import java.util.List;
import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;

public interface CostruttoreOstacolo {
	
	List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed);
	
}
