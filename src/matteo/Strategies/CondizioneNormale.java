package matteo.Strategies;

import francesco.ICella2D;

public class CondizioneNormale implements CondizioneStrategy {

	@Override
	  public boolean isSoddisfatta(ICella2D c, ICella2D D, double IF, double lunghezzaMin) {
        return IF < lunghezzaMin;
    }

}
