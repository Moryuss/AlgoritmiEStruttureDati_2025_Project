package francesco;

import nicolas.StatoCella;

public interface ICella {
	
	int stato();
	
	
	default boolean is(StatoCella stato) {
		return stato.is(stato());
	}
	
	default boolean isNot(StatoCella stato) {
		return stato.isNot(stato());
	}
	
}
