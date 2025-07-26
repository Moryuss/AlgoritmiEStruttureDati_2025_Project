package matteo.Strategies;

import java.util.List;

import nicolas.ICellaConDistanze;
import nicolas.IGrigliaConOrigine;

public interface FrontieraStrategy {
    List<ICellaConDistanze> getFrontiera(IGrigliaConOrigine g, ICellaConDistanze D);

}
