package matteo;

import nicolas.ICella2;

public class ProgressoMonitor implements IProgressoMonitor {

	private ICammino cammino;
	private ICella2 origine;
	private ICella2 destinazione;
	
	public ProgressoMonitor(ICella2 origine, ICella2 destinazione) {
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
	public ICella2 getOrigine() {
		return this.origine;
	}

	@Override
	public ICella2 getDestinazione() {
		return this.destinazione;
	}

	@Override
	public void setCammino(ICammino cammino) {
		this.cammino = cammino;	
	}

	@Override
	public void setOrigine(ICella2 origine) {
		this.origine = origine;
	}

	@Override
	public void setDestinazione(ICella2 destinazione) {
		this.destinazione = destinazione;
	}
	
	

}
