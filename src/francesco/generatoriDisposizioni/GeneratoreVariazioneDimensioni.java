package francesco.generatoriDisposizioni;

import java.util.ArrayList;
import java.util.List;

import francesco.DisposizioneOstacoli;
import francesco.ICella2D;
import francesco.IGriglia;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.Ostacolo;
import nicolas.StatoCella;

public class GeneratoreVariazioneDimensioni implements GeneratoreDisposizione {

	@Override
	public IGriglia<?> generaGrigliaSpecifica(int width, int height, IGriglia<?> result) {
		List<ICella2D> celle = new ArrayList<>();
		int posizioneX = (int) Math.ceil(width / 2);
		for(int i = 0; i < height - 1; i++) {
			celle.add(new Cella2D(StatoCella.OSTACOLO.value(), posizioneX, i));
		}
		result = result.addObstacle(new Ostacolo(celle), DisposizioneOstacoli.VARAZIONE_DIMENSIONI.value());
		return result;
	}

}
