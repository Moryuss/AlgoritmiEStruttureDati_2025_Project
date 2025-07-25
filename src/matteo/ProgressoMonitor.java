package matteo;

import francesco.ICella2D;

public class ProgressoMonitor implements IProgressoMonitor {

	private ICammino cammino;
	private ICella2D origine;
	private ICella2D destinazione;
	
	public ProgressoMonitor(ICella2D origine, ICella2D destinazione) {
		super();
		this.origine = origine;
		this.destinazione = destinazione;
	}
	
	public ProgressoMonitor() {
		super();
	}
	
	@Override
	public ICammino getCammino() {
		return this.cammino;
	}

	@Override
	public ICella2D getOrigine() {
		return this.origine;
	}

	@Override
	public ICella2D getDestinazione() {
		return this.destinazione;
	}

	@Override
	public void setCammino(ICammino cammino) {
		this.cammino = cammino;	
	}

	@Override
	public void setOrigine(ICella2D origine) {
		this.origine = origine;
	}

	@Override
	public void setDestinazione(ICella2D destinazione) {
		this.destinazione = destinazione;
	}
	
	

}
