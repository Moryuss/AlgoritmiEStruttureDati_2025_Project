package matteo.Strategies.SvuotaFrontiera;

import java.util.Collections;
import java.util.Deque;
import java.util.List;
import francesco.ICella2D;
import matteo.ILandmark;
import matteo.IProgressoMonitor;
import matteo.Riassunto.IStatisticheEsecuzione;
import utils.Utils;

public sealed interface SvuotaFrontieraStrategy {

	List<ICella2D> isFrontieraDaSvuotare(ICella2D O, List<ICella2D> frontieraList, 
			IProgressoMonitor monitorMinimo, Deque<ILandmark> stackCammino, IStatisticheEsecuzione stats );
	
	
	
	public static record SvuotaFrontieraDisabilitato() implements SvuotaFrontieraStrategy {
		@Override
		public List<ICella2D> isFrontieraDaSvuotare(ICella2D O, List<ICella2D> frontieraList,
				IProgressoMonitor monitorMinimo, Deque<ILandmark> stackCammino, IStatisticheEsecuzione stats) {
			return frontieraList;
		}
	}

	public static record SvuotaFrontieraAbilitato() implements SvuotaFrontieraStrategy {
		@Override
		public List<ICella2D> isFrontieraDaSvuotare(ICella2D O, List<ICella2D> frontieraList,
				IProgressoMonitor monitorMinimo, Deque<ILandmark> stackCammino,IStatisticheEsecuzione stats){
			var camminoMonitor = monitorMinimo.getCammino();
			if (camminoMonitor!=null) {
				double dist = stackCammino.stream()
						.collect(Utils.collectPairsToDouble(Utils::distanzaLiberaTra))
						.sum();

				if (dist>camminoMonitor.lunghezza()) {
					frontieraList = Collections.emptyList();
					stats.incrementaSvuotaFrontiera();
				} else {
					var landmarks = camminoMonitor.landmarks();
					for (int i=0; i<landmarks.size(); i++) {
						if (landmarks.get(i).sameCoordinateAs(O)) {
							double dist2 = landmarks.stream().limit(i+1)
									.collect(Utils.collectPairsToDouble(Utils::distanzaLiberaTra))
									.sum();
							if (dist>dist2) {
								frontieraList = Collections.emptyList();
								stats.incrementaSvuotaFrontiera();
							}
							break;
						}
					}
				}
			}
			return frontieraList;
		}
	}
}
