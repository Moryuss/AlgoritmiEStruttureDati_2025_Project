package matteo;

import java.util.ArrayList;
import java.util.List;

public class Cammino implements ICammino{

	private final double lunghezza;
    private final List<ILandmark> landmarks;
    
    public Cammino(double lunghezza, List<ILandmark> landMarks) {
        this.lunghezza = lunghezza;
        this.landmarks = new ArrayList<>(landMarks);
    }
    
    @Override
    public double lunghezza() {
        return lunghezza;
    }
    
    @Override
    public List<ILandmark> landmarks() {
        return landmarks;
    }
}
