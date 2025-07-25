package nicolas;

import francesco.ICella2D;
import utils.Utils;

public interface ICellaConDistanze extends ICella2D {
	
	int distanzaTorre();
	
	int distanzaAlfiere();
	
	double distanzaDaOrigine();
	
	boolean isUnreachable();
	
	
	public static ICellaConDistanze of(int x, int y, int stato, int dist) {
		var isUnreachable = dist==Integer.MAX_VALUE;
		int dt = dist&0xffff, da=dist>>>16;
		return new CellaConDistanze(x, y, stato, dt, da,
			isUnreachable ? Double.POSITIVE_INFINITY : (dt+da*Utils.sqrt2), isUnreachable);
	}
	
}
