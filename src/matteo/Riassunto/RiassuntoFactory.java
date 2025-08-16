package matteo.Riassunto;

import matteo.ICammino;
import matteo.ILandmark;
import nicolas.StatoCella;
import utils.Utils;

public class RiassuntoFactory {
	
	public static Riassunto creaRiassunto(TipoRiassunto tipo, IStatisticheEsecuzione stats) {
		return switch(tipo) {
		case CSV -> creaRiassuntoCSV(stats);
		case JSON -> creaRiassuntoJSON(stats);
		case VERBOSE -> creaRiassuntoVerbose(stats);
		case TABELLA -> creaRiassuntoTabella(stats);
		case COMPATTO -> creaRiassuntoCompatto(stats);
		case MARKDOWN -> creaRiassuntoMarkdown(stats);
		};
	}
	
	private static Riassunto creaRiassuntoVerbose(IStatisticheEsecuzione stats) {
		String tempoFormattato = Utils.formatTempo(stats.getTempoEsecuzione());
		ICammino risultato = stats.getCammino();
		
		StringBuilder sb = new StringBuilder();
		sb.append("=== RIASSUNTO ESECUZIONE CAMMINOMIN ===\n");
		sb.append("Dimensioni griglia: Width = ").append(stats.getLarghezzaGriglia()).append(", Height = ").append(stats.getAltezzaGriglia()).append("\n");
		sb.append("Tipo griglia: ").append(stats.getTipoGriglia()).append("\n");
		sb.append("Origine: (").append(stats.getOrigine().x()).append(",").append(stats.getOrigine().y()).append(")").append("\n");
		sb.append("Destinazione: (").append(stats.getDestinazione().x()).append(",").append(stats.getDestinazione().y()).append(")").append("\n");
		sb.append("Modalità Compito Tre: ").append(stats.getCompitoTreMode().toString()).append("\n");
		sb.append("Modalità Compito Due: ").append(stats.getNomeCompitoDue()).append("\n");
		sb.append("Tempo di esecuzione: ").append(tempoFormattato).append("\n");
		sb.append("Profondità massima ricorsione: ").append(stats.getMaxDepth()).append("\n");
		sb.append("Spazio occupato singola griglia: ").append(CalcoloUsoMemoria.formattaMemoria(CalcoloUsoMemoria.calcolaUsoMemoriaSingolaGriglia(stats, TipoCella.TIPO_A))).append("\n");
		sb.append("Massimo spazio occupato in memoria: ").append(CalcoloUsoMemoria.formattaMemoria(CalcoloUsoMemoria.calcolaUsoMemoria(stats, TipoCella.TIPO_A))).append("\n");
		sb.append("Totale celle di frontiera considerate: ").append(stats.getQuantitaCelleFrontiera()).append("\n");
		sb.append("Totale iterazioni condizione (riga 16/17): ").append(stats.getIterazioniCondizione()).append("\n");
		sb.append("Calcolo interrotto: ").append(stats.isCalcoloInterrotto() ? "SI" : "NO").append("\n");
		sb.append("Cache attiva: ").append(stats.isCacheAttiva() ? "SI" : "NO").append("\n");
		sb.append("Cache hit: ").append(stats.getCacheHit()).append("\n");
		sb.append("Frontiera sorted: ").append(stats.isFrontieraSorted() ? "SI" : "NO").append("\n");
		sb.append("Svuota frontiera attiva: ").append(stats.isSvuotaFrontieraAttiva() ? "SI" : "NO").append("\n");
		sb.append("Totale svuota frontiera: ").append(stats.getQuantitaSvuotaFrontiera()).append("\n");
		
		if (risultato != null) {
			sb.append("Lunghezza cammino trovato: ").append(risultato.lunghezza()).append("\n");
			sb.append("Numero landmarks: ").append(risultato.landmarks().size()).append("\n");
			sb.append("Sequenza landmarks: ");
			for (ILandmark l : risultato.landmarks()) { 
				sb.append("<(").append(l.x()).append(",").append(l.y()).append("),")
				.append(StatoCella.CONTESTO.is(l) ? "1" : "2").append(">, ");
			}
			sb.append("\n");
		}
		
		return new Riassunto(TipoRiassunto.VERBOSE, sb.toString());
	}
	
	private static Riassunto creaRiassuntoTabella(IStatisticheEsecuzione stats) {
		var map = stats.toSequencedMap();
		var l1 = map.keySet().stream().mapToInt(String::length).max().getAsInt()+2;
		var l2 = map.values().stream().mapToInt(String::length).max().getAsInt()+2;
		var format = "│ %%-%ds │ %%-%ds │\n".formatted(l1-2, l2-2);
		var top = "┌"+"─".repeat(l1)+"┬"+"─".repeat(l2)+"┐\n";
		var sep = "├"+"─".repeat(l1)+"┼"+"─".repeat(l2)+"┤\n";
		var bot = "└"+"─".repeat(l1)+"┴"+"─".repeat(l2)+"┘";
		
		var sb = new StringBuilder(top);
		sb.append(format.formatted("PARAMETRO","VALORE"));
		sb.append(sep);
		map.forEach((k,v) -> sb.append(format.formatted(k,v)));
		sb.append(bot);
		
		return new Riassunto(TipoRiassunto.TABELLA, sb.toString());
		
	}
	
	private static Riassunto creaRiassuntoCompatto(IStatisticheEsecuzione stats) {
		StringBuilder sb = new StringBuilder();
		sb.append("CAMMINOMIN: ");
		sb.append("Griglia ").append(stats.getLarghezzaGriglia()).append("x").append(stats.getAltezzaGriglia());
		sb.append(" | Tempo: ").append(Utils.formatTempo(stats.getTempoEsecuzione()));
		sb.append(" | Celle frontiera: ").append(stats.getQuantitaCelleFrontiera());
		sb.append(" | Cache hit: ").append(stats.getCacheHit());
		
		if (stats.getCammino() != null) {
			sb.append(" | Cammino: ").append(stats.getCammino().lunghezza());
		}
		
		if (stats.isCalcoloInterrotto()) {
			sb.append(" | INTERROTTO");
		}
		
		return new Riassunto(TipoRiassunto.COMPATTO, sb.toString());
	}
	
	private static Riassunto creaRiassuntoJSON(IStatisticheEsecuzione stats) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("  \"dimensioni\": {\n");
		sb.append("    \"larghezza\": ").append(stats.getLarghezzaGriglia()).append(",\n");
		sb.append("    \"altezza\": ").append(stats.getAltezzaGriglia()).append("\n");
		sb.append("  },\n");
		sb.append("  \"tipoGriglia\": ").append(stats.getTipoGriglia()).append(",\n");
		sb.append("  \"origine\": {\"x\": ").append(stats.getOrigine().x()).append(", \"y\": ").append(stats.getOrigine().y()).append("},\n");
		sb.append("  \"destinazione\": {\"x\": ").append(stats.getDestinazione().x()).append(", \"y\": ").append(stats.getDestinazione().y()).append("},\n");
		sb.append("  \"tempoEsecuzioneNs\": ").append(stats.getTempoEsecuzione()).append(",\n");
		sb.append("  \"maxDepth\": ").append(stats.getMaxDepth()).append(",\n");
		sb.append("  \"celleFrontiera\": ").append(stats.getQuantitaCelleFrontiera()).append(",\n");
		sb.append("  \"iterazioniCondizione\": ").append(stats.getIterazioniCondizione()).append(",\n");
		sb.append("  \"cacheHit\": ").append(stats.getCacheHit()).append(",\n");
		sb.append("  \"calcoloInterrotto\": ").append(stats.isCalcoloInterrotto()).append(",\n");
		sb.append("  \"cacheAttiva\": ").append(stats.isCacheAttiva()).append(",\n");
		sb.append("  \"frontieraSorted\": ").append(stats.isFrontieraSorted()).append(",\n");
		sb.append("  \"svuotaFrontieraAttiva\": ").append(stats.isSvuotaFrontieraAttiva()).append(",\n");
		sb.append("  \"svuotaFrontieraCount\": ").append(stats.getQuantitaSvuotaFrontiera());
		
		if (stats.getCammino() != null) {
			sb.append(",\n  \"cammino\": {\n");
			sb.append("    \"lunghezza\": ").append(stats.getCammino().lunghezza()).append(",\n");
			sb.append("    \"landmarks\": ").append(stats.getCammino().landmarks().size()).append("\n");
			sb.append("  }");
		}
		
		sb.append("\n}");
		
		return new Riassunto(TipoRiassunto.JSON, sb.toString());
	}
	
	private static Riassunto creaRiassuntoCSV(IStatisticheEsecuzione stats) {
		StringBuilder sb = new StringBuilder();
		sb.append("parametro,valore\n");
		sb.append("larghezza_griglia,").append(stats.getLarghezzaGriglia()).append("\n");
		sb.append("altezza_griglia,").append(stats.getAltezzaGriglia()).append("\n");
		sb.append("tipo_griglia,").append(stats.getTipoGriglia()).append("\n");
		sb.append("origine_x,").append(stats.getOrigine().x()).append("\n");
		sb.append("origine_y,").append(stats.getOrigine().y()).append("\n");
		sb.append("destinazione_x,").append(stats.getDestinazione().x()).append("\n");
		sb.append("destinazione_y,").append(stats.getDestinazione().y()).append("\n");
		sb.append("tempo_esecuzione_ns,").append(stats.getTempoEsecuzione()).append("\n");
		sb.append("max_depth,").append(stats.getMaxDepth()).append("\n");
		sb.append("celle_frontiera,").append(stats.getQuantitaCelleFrontiera()).append("\n");
		sb.append("iterazioni_condizione,").append(stats.getIterazioniCondizione()).append("\n");
		sb.append("cache_hit,").append(stats.getCacheHit()).append("\n");
		sb.append("calcolo_interrotto,").append(stats.isCalcoloInterrotto()).append("\n");
		sb.append("cache_attiva,").append(stats.isCacheAttiva()).append("\n");
		sb.append("frontiera_sorted,").append(stats.isFrontieraSorted()).append("\n");
		sb.append("svuota_frontiera_attiva,").append(stats.isSvuotaFrontieraAttiva()).append("\n");
		sb.append("svuota_frontiera_count,").append(stats.getQuantitaSvuotaFrontiera()).append("\n");
		
		if (stats.getCammino() != null) {
			sb.append("lunghezza_cammino,").append(stats.getCammino().lunghezza()).append("\n");
			sb.append("numero_landmarks,").append(stats.getCammino().landmarks().size()).append("\n");
		}
		
		return new Riassunto(TipoRiassunto.CSV, sb.toString());
	}
	
	private static Riassunto creaRiassuntoMarkdown(IStatisticheEsecuzione stats) {
		StringBuilder sb = new StringBuilder();
		sb.append("# Riassunto Esecuzione CamminoMin\n\n");
		sb.append("## Configurazione\n");
		sb.append("- **Griglia**: %d x %d (tipo %d)\n".formatted(
				stats.getLarghezzaGriglia(), stats.getAltezzaGriglia(), stats.getTipoGriglia()));
		sb.append("- **Origine**: %s\n".formatted(stats.getOrigine().coordinateToString()));
		sb.append("- **Destinazione**: %s\n".formatted(stats.getDestinazione().coordinateToString()));
		sb.append("- **Compito Due**: %s\n".formatted(stats.getNomeCompitoDue()));
		sb.append("- **Compito Tre**: %s\n\n".formatted(stats.getCompitoTreMode()));
		
		sb.append("## Risultati Esecuzione\n");
		sb.append("- **Tempo**: %s\n".formatted(Utils.formatTempo(stats.getTempoEsecuzione())));
		sb.append("- **Profondità massima**: %d\n".formatted(stats.getMaxDepth()));
		sb.append("- **Celle frontiera**: %d\n".formatted(stats.getQuantitaCelleFrontiera()));
		sb.append("- **Iterazioni condizione**: %d\n".formatted(stats.getIterazioniCondizione()));
		sb.append("- **Cache hit**: %d\n".formatted(stats.getCacheHit()));
		sb.append("- **Calcolo interrotto**: %s\n\n".formatted(stats.isCalcoloInterrotto() ? "<span style=\"color:green\">✔️</span>" : "<span style=\"color:red\">❌</span>"));
		
		String attiva = "<span style=\"color:green\">✔️</span> Attiva";
		String disattiva = "<span style=\"color:red\">❌</span> Disattiva";
		sb.append("## Ottimizzazioni\n");
		sb.append("- **Cache**: %s\n".formatted(stats.isCacheAttiva() ? attiva : disattiva));
		sb.append("- **Frontiera Sorted**: %s\n".formatted(stats.isFrontieraSorted() ? attiva : disattiva));
		sb.append("- **Svuota Frontiera**: %s\n\n".formatted(stats.isSvuotaFrontieraAttiva() ? attiva+" (" + stats.getQuantitaSvuotaFrontiera() + " volte)" : disattiva));
		
		if (stats.getCammino() != null) {
			sb.append("## Cammino Trovato\n");
			sb.append("- **Lunghezza**: %f\n".formatted(stats.getCammino().lunghezza()));
			sb.append("- **Landmarks**: %d\n".formatted(stats.getCammino().landmarks().size()));
		}
		
		return new Riassunto(TipoRiassunto.MARKDOWN, sb.toString());
	}
	
}
