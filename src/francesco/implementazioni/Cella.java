package francesco.implementazioni;

import francesco.ICella;

public record Cella(int stato) implements ICella {
	
	public Cella() {
		this(0);
	}
	
}
