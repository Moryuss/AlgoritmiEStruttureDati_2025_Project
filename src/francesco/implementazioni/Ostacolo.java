package francesco.implementazioni;

import java.util.List;
import francesco.ICella2D;
import francesco.IObstacle;

public record Ostacolo(List<ICella2D> list) implements IObstacle {
	
}