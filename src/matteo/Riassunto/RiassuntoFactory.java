package matteo.Riassunto;

import matteo.ICammino;
import matteo.ILandmark;
import nicolas.StatoCella;
import utils.Utils;

public class RiassuntoFactory {

	public static Riassunto creaRiassunto(TipiRiassunto tipo, IStatisticheEsecuzione stats) {
		switch (tipo) {
		case VERBOSE:
			return creaRiassuntoVerbose(stats);
		case TABELLA:
			return creaRiassuntoTabella(stats);
		case COMPATTO:
			return creaRiassuntoCompatto(stats);
		case JSON:
			return creaRiassuntoJSON(stats);
		case CSV:
			return creaRiassuntoCSV(stats);
		case MARKDOWN:
			return creaRiassuntoMarkdown(stats);
		default:
			return creaRiassuntoVerbose(stats);
		}
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
		sb.append("Spazio occupato singola griglia: ").append(CalcoloUsoMemoria.calcolaUsoMemoriaSingolaGriglia(stats, TipoCella.TIPO_A)).append(" bytes\n");
		sb.append("Massimo spazio occupato in memoria: ").append(CalcoloUsoMemoria.calcolaUsoMemoria(stats, TipoCella.TIPO_A)).append(" bytes\n");
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

		return new Riassunto(TipiRiassunto.VERBOSE, sb.toString());
	}

	private static Riassunto creaRiassuntoTabella(IStatisticheEsecuzione stats) {
		StringBuilder sb = new StringBuilder();
		sb.append("┌─────────────────────────────────────┬──────────────────────────────┐\n");
		sb.append("│ PARAMETRO                           │ VALORE                       │\n");
		sb.append("├─────────────────────────────────────┼──────────────────────────────┤\n");
		sb.append(String.format("│ %-35s │ %-28s │\n", "Dimensioni griglia", stats.getLarghezzaGriglia() + "x" + stats.getAltezzaGriglia()));
		sb.append(String.format("│ %-35s │ %-28s │\n", "Tipo griglia", stats.getTipoGriglia()));
		sb.append(String.format("│ %-35s │ %-28s │\n", "Origine", "(" + stats.getOrigine().x() + "," + stats.getOrigine().y() + ")"));
		sb.append(String.format("│ %-35s │ %-28s │\n", "Destinazione", "(" + stats.getDestinazione().x() + "," + stats.getDestinazione().y() + ")"));
		sb.append(String.format("│ %-35s │ %-28s │\n", "Tempo esecuzione", Utils.formatTempo(stats.getTempoEsecuzione())));
		sb.append(String.format("│ %-35s │ %-28s │\n", "Celle frontiera", String.valueOf(stats.getQuantitaCelleFrontiera())));
		sb.append(String.format("│ %-35s │ %-28s │\n", "Iterazioni condizione", String.valueOf(stats.getIterazioniCondizione())));
		sb.append(String.format("│ %-35s │ %-28s │\n", "Cache hit", String.valueOf(stats.getCacheHit())));
		sb.append(String.format("│ %-35s │ %-28s │\n", "Max depth", String.valueOf(stats.getMaxDepth())));

		if (stats.getCammino() != null) {
			sb.append(String.format("│ %-35s │ %-28s │\n", "Lunghezza cammino", String.valueOf(stats.getCammino().lunghezza())));
			sb.append(String.format("│ %-35s │ %-28s │\n", "Landmarks", String.valueOf(stats.getCammino().landmarks().size())));
		}

		sb.append("└─────────────────────────────────────┴──────────────────────────────┘\n");

		return new Riassunto(TipiRiassunto.TABELLA, sb.toString());
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

		return new Riassunto(TipiRiassunto.COMPATTO, sb.toString());
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

		return new Riassunto(TipiRiassunto.JSON, sb.toString());
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

		return new Riassunto(TipiRiassunto.CSV, sb.toString());
	}

	private static Riassunto creaRiassuntoMarkdown(IStatisticheEsecuzione stats) {
		StringBuilder sb = new StringBuilder();
		sb.append("# Riassunto Esecuzione CamminoMin\n\n");
		sb.append("## Configurazione\n");
		sb.append("- **Griglia**: ").append(stats.getLarghezzaGriglia()).append(" x ").append(stats.getAltezzaGriglia()).append(" (tipo ").append(stats.getTipoGriglia()).append(")\n");
		sb.append("- **Origine**: (").append(stats.getOrigine().x()).append(",").append(stats.getOrigine().y()).append(")\n");
		sb.append("- **Destinazione**: (").append(stats.getDestinazione().x()).append(",").append(stats.getDestinazione().y()).append(")\n");
		sb.append("- **Compito Due**: ").append(stats.getNomeCompitoDue()).append("\n");
		sb.append("- **Compito Tre**: ").append(stats.getCompitoTreMode().toString()).append("\n\n");

		sb.append("## Risultati Esecuzione\n");
		sb.append("- **Tempo**: ").append(Utils.formatTempo(stats.getTempoEsecuzione())).append("\n");
		sb.append("- **Profondità massima**: ").append(stats.getMaxDepth()).append("\n");
		sb.append("- **Celle frontiera**: ").append(stats.getQuantitaCelleFrontiera()).append("\n");
		sb.append("- **Iterazioni condizione**: ").append(stats.getIterazioniCondizione()).append("\n");
		sb.append("- **Cache hit**: ").append(stats.getCacheHit()).append("\n");
		sb.append("- **Calcolo interrotto**: ").append(stats.isCalcoloInterrotto() ? "✅" : "❌").append("\n\n");

		sb.append("## Ottimizzazioni\n");
		sb.append("- **Cache**: ").append(stats.isCacheAttiva() ? "✅ Attiva" : "❌ Disattiva").append("\n");
		sb.append("- **Frontiera Sorted**: ").append(stats.isFrontieraSorted() ? "✅ Attiva" : "❌ Disattiva").append("\n");
		sb.append("- **Svuota Frontiera**: ").append(stats.isSvuotaFrontieraAttiva() ? "✅ Attiva (" + stats.getQuantitaSvuotaFrontiera() + " volte)" : "❌ Disattiva").append("\n\n");

		if (stats.getCammino() != null) {
			sb.append("## Cammino Trovato\n");
			sb.append("- **Lunghezza**: ").append(stats.getCammino().lunghezza()).append("\n");
			sb.append("- **Landmarks**: ").append(stats.getCammino().landmarks().size()).append("\n");
		}

		return new Riassunto(TipiRiassunto.MARKDOWN, sb.toString());
	}
}