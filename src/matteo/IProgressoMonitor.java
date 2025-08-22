package matteo;

import java.util.Optional;

import francesco.ICella2D;

public interface IProgressoMonitor {

	ICammino getCammino();
	
	ICella2D getOrigine();

	ICella2D getDestinazione();
	
	void setCammino(ICammino cammino);

	void setOrigine(ICella2D origine);
	
	void setDestinazione(ICella2D destinazione);
	
	
	default Optional<ICammino> safeGetCammino() {
		return Optional.of(getCammino());
	}
	
}
