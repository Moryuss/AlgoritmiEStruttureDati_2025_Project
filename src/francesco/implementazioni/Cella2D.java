package francesco.implementazioni;

import francesco.ICella2D;

public class Cella2D implements ICella2D {

	private int stato;
	private int x;
	private int y;
	
	public Cella2D(int stato, int x, int y) {
		this.stato = stato;
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public int stato() {
		return stato;
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public void setStato(int stato) {
		this.stato = stato;
	}

}
