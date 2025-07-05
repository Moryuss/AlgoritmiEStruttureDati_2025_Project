package francesco.implementazioni;

import francesco.ICella;

public class Cella implements ICella {

	private int stato;
	
	public Cella(int stato) {
		this.stato = stato;
	}
	
	@Override
	public int stato() {
		return stato;
	}

	@Override
	public void setStato(int stato) {
		this.stato = stato;
	}
}
