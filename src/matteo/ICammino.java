package matteo;

import java.util.Collections;
import java.util.List;
import nicolas.DistanzaLibera;

public interface ICammino extends Comparable<ICammino> {
	
	public static final ICammino INFINITY = new Cammino(DistanzaLibera.INFINITY, Collections.emptyList());
	
	DistanzaLibera distanzaLibera();
	
	List<ILandmark> landmarks();
	
	
	@Override
	default int compareTo(ICammino o) {
		return distanzaLibera().compareTo(o.distanzaLibera());
	}
	
	default int lunghezzaTorre() {
		return distanzaLibera().distanzaTorre();
	}
	default int lunghezzaAlfiere() {
		return distanzaLibera().distanzaAlfiere();
	}
	default double lunghezza() {
		return distanzaLibera().distanza();
	}
	
}
