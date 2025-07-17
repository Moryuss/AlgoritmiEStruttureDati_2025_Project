package matteo;

import java.util.List;

public interface ICammino {
	
	double lunghezza();
	List<ILandmark> landmarks();
	int lunghezzaTorre();
	int lunghezzaAlfiere();
}
