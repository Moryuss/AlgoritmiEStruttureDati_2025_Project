package matteo.Strategies.SvuotaFrontiera;

import java.util.Deque;
import java.util.List;

import francesco.ICella2D;
import matteo.ILandmark;
import matteo.IProgressoMonitor;
import matteo.IStatisticheEsecuzione;
import nicolas.ICellaConDistanze;

public interface SvuotaFrontieraStrategy {

	List<ICellaConDistanze> isFrontieraDaSvuotare(ICella2D O, List<ICellaConDistanze> frontieraList, 
			IProgressoMonitor monitorMinimo, Deque<ILandmark> stackCammino, IStatisticheEsecuzione stats );

}
