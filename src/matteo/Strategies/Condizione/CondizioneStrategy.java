package matteo.Strategies.Condizione;

import francesco.ICella2D;
import utils.Utils;


public sealed interface CondizioneStrategy {
    boolean isSoddisfatta(ICella2D corrente, ICella2D D, double lF, double lunghezzaMin);

    public static record CondizioneNormale() implements CondizioneStrategy {
		@Override
		public boolean isSoddisfatta(ICella2D c, ICella2D D, double lF, double lunghezzaMin) {
			return lF < lunghezzaMin;
		}
	}
    public static record CondizioneRafforzata() implements CondizioneStrategy {
		@Override
		public boolean isSoddisfatta(ICella2D c, ICella2D D, double lF, double lunghezzaMin) {
			return lF + Utils.distanzaLiberaTra(c,D) < lunghezzaMin;
		}
	}
    
}
