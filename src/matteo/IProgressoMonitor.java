package matteo;

import francesco.ICella2D;

public interface IProgressoMonitor {

	public ICammino getCammino();
	
	public ICella2D getOrigine();

	public ICella2D getDestinazione();
	
	public void setCammino(ICammino cammino);

	public void setOrigine(ICella2D origine);
	
	public void setDestinazione(ICella2D destinazione);
}
