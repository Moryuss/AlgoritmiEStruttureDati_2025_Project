package francesco.ostacolibuilder;

import java.util.ArrayList;
import java.util.List;

import francesco.ICella;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import nicolas.StatoCella;

public class OPersonalizzato implements CostruttoreOstacolo {

	@Override
	public List<ICella2D> generaCelle(int width, int height, IGriglia<? extends ICella> griglia, int randomSeed) {
		List<ICella2D> risultato = new ArrayList<>();
		risultato.add(new Cella2D(StatoCella.OSTACOLO.value(), width/2, height/2));
		return risultato;
	}

}
