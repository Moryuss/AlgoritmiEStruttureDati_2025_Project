package francesco.generatoreJSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import francesco.DisposizioneOstacoli;
import francesco.TipoOstacolo;
import matteo.ConfigurationFlag;
import matteo.Riassunto.TipoRiassunto;

public class GeneratoreJSON {

	// Appelt default 
	private static final int DEFAULT_LARGHEZZA_APPLET = 1200;
	private static final int DEFAULT_ALTEZZA_APPLET = 800;
	private static final boolean DEFAULT_DEMO = false;
	private static final boolean DEFAULT_SHOW_TEXT = false;
	private static final int DEFAULT_PIXEL_DENSITY = 1;
	private static final List<String> DEFAULT_PALETTE = List.of("0xff0000", "0xff4000", "0xff8000", 
			"0xffb000","0x004000", "0xffffff", "0x000000", "0xff00ff", "0x00ff00");
	private static final List<String> DEFAULT_PALETTE_LEGEND = List.of("origine","regina","contesto","complemento",
			"frontiera","vuota","ostacolo","destinazione","landmark");
	
	// Impostazioni Compito Tre default
	private static final int DEFAULT_TEMPO_LIMITE = 30;
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
	private static final int DEFAULT_ORIGINE_X = 0;
	private static final int DEFAULT_ORIGINE_Y = 0;
	private static final int DEFAULT_DESTINAZIONE_X = 10;
	private static final int DEFAULT_DESTINAZIONE_Y = 10;
	private static final String DEFAULT_COMPITO_DUE = "V0";
	private static final Map<String, Boolean> DEFAULT_COMPITO_TRE_MODALITA = Map.of(
			ConfigurationFlag.DEBUG.toString(), false,
			ConfigurationFlag.MONITOR_ENABLED.toString(), true,
			ConfigurationFlag.STATE_CHECK.toString(), false,
			ConfigurationFlag.STATE_CHECK.toString(), false,
			ConfigurationFlag.SORTED_FRONTIERA.toString(), false,
			ConfigurationFlag.CONDIZIONE_RAFFORZATA.toString(), false,
			ConfigurationFlag.CACHE_ENABLED.toString(), false,
			ConfigurationFlag.SVUOTA_FRONTIERA.toString(), false
			);
	
	// Tipo Riassunto default
	private static final TipoRiassunto DEFAULT_TIPO_RIASSUNTO = TipoRiassunto.VERBOSE;
	
	// Griglia default
	private static final int DEFAULT_RANDOM_SEED = 42;
	private static final int DEFAULT_ALTEZZA_GRIGLIA = 20;
	private static final int DEFAULT_LARGHEZZA_GRIGLIA = 30;
	private static final int DEFAULT_NUMERO_OSTACOLI = 0;
	
	// load default
	private static final String DEFAULT_PATH_GRIGLIA = "griglie.int.json";
	private static final String DEFAULT_NAME_GRIGLIA = "test1";
	
	public static void generaJSON(JSONFormato tipo, String nomeFile) {
		String contenuto = "";
		switch (tipo) {
			case BASE_COMPITO_UNO:
				contenuto = generaJSONBaseCompitoUno();
				break;
			case GRAFICA_COMPITO_DUE:
				contenuto = generaJSONGraficaCompitoDue();
				break;
			case IMPOSTAZIONI_COMPITO_TRE:
				contenuto = generaJSONImpostazioniCompitoTre();
				break;
			default:
				throw new IllegalArgumentException("Tipo di JSON non riconosciuto: " + tipo);
		}
		contenuto.concat("}");
	}
	
	private static String generaJSONBaseCompitoUno() {
		StringBuilder sb = new StringBuilder();
		// Larghezza
		sb.append("{\n");
		sb.append("\t\"width\":"+DEFAULT_LARGHEZZA_GRIGLIA);
		// Altezza
		sb.append(",\n");
		sb.append("\t\"height\":"+DEFAULT_ALTEZZA_GRIGLIA);
		// Random Seed
		sb.append(",\n");
		sb.append("\t\"randomSeed\":"+DEFAULT_RANDOM_SEED);
		sb.append(",\n");
		// Numero massimo di ostacoli
		sb.append("\t\"maxOstacoli\": {\n");
		for(TipoOstacolo ost : TipoOstacolo.values()) {
			if(ost == TipoOstacolo.PERSONALIZZATO) {
				continue; // Il tipo PERSONALIZZATO non è inserito
			}
			sb.append("\t\t\"").append(ost.toString()).append("\": ").append(DEFAULT_NUMERO_OSTACOLI);
			if (ost != TipoOstacolo.values()[TipoOstacolo.values().length - 1]) {
				sb.append(",");
			}
			sb.append("\n");
		}
		sb.append("\t},\n");
		// Disposizioni
		sb.append("\t\"disposizioni\":{\n");
		for(DisposizioneOstacoli disp : DisposizioneOstacoli.values()) {
			sb.append("\t\t\"").append(disp.toString()).append("\": ").append("false");
			if (disp != DisposizioneOstacoli.values()[DisposizioneOstacoli.values().length - 1]) {
				sb.append(",");
			}
			sb.append("\n");
		}
		sb.append("\t}\n");
		return sb.toString();
	}
	
	private static String generaJSONGraficaCompitoDue() {
		StringBuilder sb = new StringBuilder();
		sb.append(generaJSONBaseCompitoUno());
		sb.append(",\n");
		// load
		sb.append("\t\"load\": {\n");
		// Path
		sb.append("\t\t\"path\":\""+ DEFAULT_PATH_GRIGLIA + "\",\n");
		// Nome
		sb.append("\t\t\"name\":\"" + DEFAULT_NAME_GRIGLIA + "\"\n");
		sb.append("\t},\n");
		
		// Applet
		sb.append("\t\"applet\": {\n");
		// Width
		sb.append("\t\t\"width\":" + DEFAULT_LARGHEZZA_APPLET +",\n");
		// Height
		sb.append("\t\t\"height\":" + DEFAULT_ALTEZZA_APPLET + ",\n");
		// demo
		sb.append("\t\t\"demo\":" + DEFAULT_DEMO + ",\n");
		// showText
		sb.append("\t\t\"showText\":" + DEFAULT_SHOW_TEXT + ",\n");
		// pixelDensity
		sb.append("\t\t\"pixelDensity\":" + DEFAULT_PIXEL_DENSITY + "\n");
		// palette
		sb.append("\t\t\"palette\":[");
		for (int i = 0; i < DEFAULT_PALETTE.size(); i++) {
			sb.append("\"").append(DEFAULT_PALETTE.get(i)).append("\"");
			if (i < DEFAULT_PALETTE.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append("],\n");
		// paletteLegend
		sb.append("\t\t\"paletteLegend\":[");
		for (int i = 0; i < DEFAULT_PALETTE_LEGEND.size(); i++) {
			sb.append("\"").append(DEFAULT_PALETTE_LEGEND.get(i)).append("\"");
			if (i < DEFAULT_PALETTE_LEGEND.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append("]\n");
		sb.append("\t}\n");
		return sb.toString();
	}
	
	private static String generaJSONImpostazioniCompitoTre() {
		StringBuilder sb = new StringBuilder();
		sb.append(generaJSONBaseCompitoUno());
		sb.append(",\n");
		// Impostazioni main sub JSON
		sb.append("\t\"impostazioniMain\": {\n");
		// Tempo limite
		sb.append("\t\t\"tempoLimite\": " + DEFAULT_TEMPO_LIMITE + ",\n");
		// Time unit
		sb.append("\t\t\"timeUnit\": \"" + DEFAULT_TIME_UNIT.toString() + "\",\n");
		// OrigineX
		sb.append("\t\t\"origineX\": " + DEFAULT_ORIGINE_X + ",\n");
		// OrigineY
		sb.append("\t\t\"origineY\": " + DEFAULT_ORIGINE_Y + ",\n");
		// DestinazioneX
		sb.append("\t\t\"destinazioneX\": " + DEFAULT_DESTINAZIONE_X + ",\n");
		// DestinazioneY
		sb.append("\t\t\"destinazioneY\": " + DEFAULT_DESTINAZIONE_Y + ",\n");
		// CompitoDue
		sb.append("\t\t\"compitoDue\": \"" + DEFAULT_COMPITO_DUE + "\",\n");
		// Comptio Tre Flag Sub JSON
		sb.append("\t\t\"compitoTreModalitaFlags\": {\n");
		for (Map.Entry<String, Boolean> entry : DEFAULT_COMPITO_TRE_MODALITA.entrySet()) {
			sb.append("\t\t\t\"").append(entry.getKey()).append("\": ").append(entry.getValue());
			if (!entry.equals(DEFAULT_COMPITO_TRE_MODALITA.entrySet().toArray()[DEFAULT_COMPITO_TRE_MODALITA.size() - 1])) {
				sb.append(",");
			}
			sb.append("\n");
		}
		sb.append("\t\t},\n");
		// Tipo Riassunto
		sb.append("\t\t\"tipoRiassunto\": \"" + DEFAULT_TIPO_RIASSUNTO + "\"\n");
		sb.append("\t}\n");
		return "";
	}
}
