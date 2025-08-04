package matteo.Strategies.Frontiera;

import java.util.Comparator;
import java.util.stream.Stream;
import francesco.IHave2DCoordinate;
import nicolas.ICellaConDistanze;
import utils.Utils;

public sealed interface FrontieraStrategy {
	
    Stream<ICellaConDistanze> getFrontiera(Stream<ICellaConDistanze> src, IHave2DCoordinate D);
    
    
    public static record FrontieraNormale() implements FrontieraStrategy {
    	public Stream<ICellaConDistanze> getFrontiera(Stream<ICellaConDistanze> src, IHave2DCoordinate D) {
    		return src;
    	}
    }
    
    public static record FrontieraOrdinata() implements FrontieraStrategy {
    	@Override
    	public Stream<ICellaConDistanze> getFrontiera(Stream<ICellaConDistanze> src, IHave2DCoordinate D) {
    		return src.sorted(Comparator.comparingDouble(c -> Utils.distanzaLiberaTra(c, D)));
    	}
    }
    
}
