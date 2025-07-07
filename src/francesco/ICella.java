package francesco;

import nicolas.StatoCella;

public interface ICella {
	
	int stato();
	
	default boolean is(StatoCella stato) {
		return stato.is(stato());
	}
	
	void setStato(int stato);
	
}
