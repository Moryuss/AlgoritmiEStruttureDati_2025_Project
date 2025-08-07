package matteo;

import java.util.List;
import nicolas.DistanzaLibera;
import utils.Utils;

public record Cammino(DistanzaLibera distanzaLibera, List<ILandmark> landmarks) implements ICammino {
	
	public Cammino {
		assert !(landmarks.isEmpty() && !distanzaLibera.isInfinity());
	}
	
	
	public static Cammino from(List<ILandmark> landmarks) {
		var distanzaLibera = landmarks.stream()
		.collect(Utils.collectPairs(DistanzaLibera::from))
		.reduce(DistanzaLibera.ZERO, DistanzaLibera::add);
		return new Cammino(distanzaLibera, landmarks);
	}
	
}