package nicolas;

public record Cella2(int x, int y, int[] s, int distanzaTorre, int distanzaAlfiere) implements ICella2 {
	
	public Cella2(int x, int y, int stato, int distanzaTorre, int distanzaAlfiere) {
		this(x, y, new int[] {stato}, distanzaTorre, distanzaAlfiere);
	}
	
	
	@Override
	public int stato() {
		return s[0];
	}
	
	@Override
	public void setStato(int stato) {
		s[0] = stato;
		
	}
	
}