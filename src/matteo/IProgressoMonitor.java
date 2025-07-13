package matteo;

import nicolas.ICella2;

public interface IProgressoMonitor {

	public ICammino getCammino();
	
	public ICella2 getOrigine();

	public ICella2 getDestinazione();
	
	public void setCammino(ICammino cammino);

	public void setOrigine(ICella2 origine);
	
	public void setDestinazione(ICella2 destinazione);
}
