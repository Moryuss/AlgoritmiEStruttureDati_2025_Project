package matteo.Strategies;

import java.util.List;

import francesco.ICella2D;
import nicolas.ICellaConDistanze;
import nicolas.IGrigliaConOrigine;

public interface FrontieraStrategy {
    List<ICellaConDistanze> getFrontiera(IGrigliaConOrigine g, ICella2D D);

}
