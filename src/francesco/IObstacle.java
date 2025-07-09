package francesco;

import java.util.List;

public interface IObstacle {
	
	List<ICella2D> list();
	
	public static IObstacle of(List<ICella2D> celle) {
		return ()->celle;
	}
	
}
