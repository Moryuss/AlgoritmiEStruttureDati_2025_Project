package francesco;

public interface ICella {
	
	int stato();
	
	
	default boolean is(StatoCella stato) {
		return stato.is(stato());
	}
	
}
