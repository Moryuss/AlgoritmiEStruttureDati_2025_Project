package matteo;

import francesco.implementazioni.Cella2D;

public class Landmark extends Cella2D implements ILandmark{

	//Per sapere l'ordine dei Landmark
	private final int index;
	public Landmark(int stato, int x, int y, int index) {
		super(stato, x, y);
		this.index = index;
	}

    @Override
    public int index() {
        return index;
    }
}
