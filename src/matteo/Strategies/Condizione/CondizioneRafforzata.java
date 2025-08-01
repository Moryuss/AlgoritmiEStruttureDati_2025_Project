package matteo.Strategies.Condizione;

import francesco.ICella2D;
import utils.Utils;

public class CondizioneRafforzata implements CondizioneStrategy {

	@Override
	public boolean isSoddisfatta(ICella2D c, ICella2D D, double IF, double lunghezzaMin) {
        return IF + Utils.distanzaLiberaTra(c, D) < lunghezzaMin;
    }

}
