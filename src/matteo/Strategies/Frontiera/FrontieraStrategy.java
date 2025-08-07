package matteo.Strategies.Frontiera;

import java.util.Comparator;
import java.util.stream.Stream;
import francesco.ICella2D;
import francesco.IHave2DCoordinate;
import utils.Utils;

public sealed interface FrontieraStrategy {
	
    Stream<ICella2D> getFrontiera(Stream<ICella2D> src, IHave2DCoordinate D);
    
    
    public static record FrontieraNormale() implements FrontieraStrategy {
    	public Stream<ICella2D> getFrontiera(Stream<ICella2D> src, IHave2DCoordinate D) {
    		return src;
    	}
    }
    
    public static record FrontieraOrdinata() implements FrontieraStrategy {
    	@Override
    	public Stream<ICella2D> getFrontiera(Stream<ICella2D> src, IHave2DCoordinate D) {
    		return src.sorted(Comparator.comparingDouble(c -> Utils.distanzaLiberaTra(c, D)));
    	}
    }
    
}
