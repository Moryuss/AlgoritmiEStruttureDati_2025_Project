package matteo;

import francesco.ICella2D;

public record Landmark(int stato, int x, int y) implements ILandmark {
	
	public Landmark(ICella2D cella) {
		this(cella.stato(), cella.x(), cella.y());
	}
	
}
