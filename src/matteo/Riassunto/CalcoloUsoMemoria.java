package matteo.Riassunto;

public class CalcoloUsoMemoria {

	public static String calcolaUsoMemoria(IStatisticheEsecuzione stats, TipoCella tipoCella) {
	    int larghezza = stats.getLarghezzaGriglia();
	    int altezza = stats.getAltezzaGriglia();
	    int profondita = stats.getMaxDepth();

	    int numCelleTotali = larghezza * altezza * profondita;
	    int totaleByte = numCelleTotali * tipoCella.getDimensioneCella();

	    return formattaMemoria(totaleByte);
	}

	public static String calcolaUsoMemoriaSingolaGriglia(IStatisticheEsecuzione stats, TipoCella tipoCella) {
	    int larghezza = stats.getLarghezzaGriglia();
	    int altezza = stats.getAltezzaGriglia();

	    int numCelle = larghezza * altezza;
	    int totaleByte = numCelle * tipoCella.getDimensioneCella();

	    return formattaMemoria(totaleByte);
	}
	private static String formattaMemoria(int byteTotali) {
	    if (byteTotali < 1024)
	        return byteTotali + " B";
	    else if (byteTotali < 1024 * 1024)
	        return String.format("%.2f KB", byteTotali / 1024.0);
	    else
	        return String.format("%.2f MB", byteTotali / (1024.0 * 1024));
	}


}
