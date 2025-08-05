package matteo.Strategies.Condizione;

import francesco.ICella2D;
import utils.Utils;


public sealed interface CondizioneStrategy {
    boolean isSoddisfatta(ICella2D corrente, ICella2D D, double IF, double lunghezzaMin);

    public static record CondizioneNormale() implements CondizioneStrategy {
		@Override
		public boolean isSoddisfatta(ICella2D c, ICella2D D, double IF, double lunghezzaMin) {
			return IF < lunghezzaMin;
		}
	}
    public static record CondizioneRafforzata() implements CondizioneStrategy {
		@Override
		public boolean isSoddisfatta(ICella2D c, ICella2D D, double IF, double lunghezzaMin) {
			return IF + Utils.distanzaLiberaTra(c,D) < lunghezzaMin;
		}
	}
    
}
