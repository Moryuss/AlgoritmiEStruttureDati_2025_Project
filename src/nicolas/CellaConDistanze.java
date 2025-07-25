package nicolas;

public record CellaConDistanze(int x, int y, int stato, int distanzaTorre, int distanzaAlfiere, double distanzaDaOrigine, boolean isUnreachable) implements ICellaConDistanze {
	
}
