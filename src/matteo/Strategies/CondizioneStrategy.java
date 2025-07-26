package matteo.Strategies;

import francesco.ICella2D;

public interface CondizioneStrategy {
    boolean isSoddisfatta(ICella2D corrente, ICella2D D, double IF, double lunghezzaMin);

}
