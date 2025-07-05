package francesco.implementazioni;

import java.util.List;

import francesco.ICella2D;
import francesco.IObstacle;

public class Ostacolo implements IObstacle {

	List<ICella2D> celle;
	
	public Ostacolo(List<ICella2D> celle) {
		this.celle = celle;
	}
	
	@Override
	public List<ICella2D> list() {
		return celle;
	}

}
