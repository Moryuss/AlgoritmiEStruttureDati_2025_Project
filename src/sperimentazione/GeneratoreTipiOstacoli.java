package sperimentazione;

import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nicolas.GrigliaConOrigineFactory;
import nicolas.IGrigliaConOrigine;

public class GeneratoreTipiOstacoli {
	
	private static final int SEED = 42;
	private static final int HEIGHT = 20;
	private static final int WIDTH = 30;
	private static final int O_X = 0;
	private static final int O_Y = 0;
	private static final int D_Y = 29;
	private static final int D_X = 19;
	
	private static final String[] KEYS = {
			"SEMPLICE",
			"AGGLOMERATO",
			"BARRA_VERTICALE",
			"BARRA_ORIZZONTALE",
			"BARRA_DIAGONALE",
			"ZONA_CHIUSA",
			"DELIMITATORE_VERTICALE",
			"DELIMITATORE_ORIZZONTALE"
	};
	private static final int[] MAX_VALUES = {
			7, // SEMPLICE
			2, // AGGLOMERATO
			2, // BARRA_VERTICALE
			2, // BARRA_ORIZZONTALE
			2, // BARRA_DIAGONALE
			2, // ZONA_CHIUSA
			1, // DELIMITATORE_VERTICALE
			1 // DELIMITATORE_ORIZZONTALE
	};

	public static void main(String[] args) {
		generaTutteLeCombinazioni();
	}

	public static void generaTutteLeCombinazioni() {
		int total = 1 << KEYS.length; // 256 combinazioni
		List<String> paths = new ArrayList<>();
		List<String> names = new ArrayList<>();
		List<Integer> stati = new ArrayList<>();
		List<Integer> ox = new ArrayList<>();
		List<Integer> oy = new ArrayList<>();
		List<Integer> dx = new ArrayList<>();
		List<Integer> dy = new ArrayList<>();
		
		System.out.println("Generazione di " + total + " combinazioni di ostacoli...");
		
		for (int mask = 0; mask < total; mask++) {
			String numeroTipoGriglia = String.format("%03d", mask);
			System.out.println("Generando combinazione " + numeroTipoGriglia + "...");
			
			// Genera il config.json per questa combinazione
			String configContent = generaConfigPerCombinazione(mask);
			
			// Salva il config temporaneo
			String tempConfigPath = "temp_config_" + numeroTipoGriglia + ".json";
			try (FileWriter writer = new FileWriter(tempConfigPath)) {
				writer.write(configContent);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			// Crea la griglia
			try {
				IGriglia<?> griglia = new LettoreGriglia().crea(Paths.get(tempConfigPath));
				
				// Crea la griglia con origine
				IGrigliaConOrigine grigliaConOrigine = GrigliaConOrigineFactory.creaV0(griglia, 0, 0);
				
				// Genera la rappresentazione della griglia 
				String grigliaString = generaRappresentazioneGriglia(grigliaConOrigine);
				
				// Determina il tipo di griglia
				int tipoGrigliaInt = determinaTipoGriglia(grigliaConOrigine);
				String tipoGriglia = "" + tipoGrigliaInt;
				
				// Crea la directory
				String directoryPath = "src/sperimentazione/tipoGriglia/" + tipoGriglia;
				Path dir = Paths.get(directoryPath);
				Files.createDirectories(dir);
				
				// Salva il file JSON della griglia
				String grigliaJsonPath = directoryPath + "/" + tipoGriglia + ".int.json";
				String grigliaJsonContent = generaJsonGriglia(tipoGriglia, grigliaString);
				
				try (FileWriter writer = new FileWriter(grigliaJsonPath)) {
					writer.write(grigliaJsonContent);
				}
				
				// Aggiungi ai path per il paths.json finale
				paths.add(tipoGriglia + "/" + tipoGriglia + ".int.json");
				names.add(tipoGriglia);
				stati.add(tipoGrigliaInt);
				ox.add(O_X);
				oy.add(O_Y);
				dx.add(WIDTH - 1);
				dy.add(HEIGHT - 1);
				
			} catch (Exception e) {
				System.err.println("Errore nella generazione della combinazione " + numeroTipoGriglia + ": " + e.getMessage());
				e.printStackTrace();
			}
			
			// Elimina il file temporaneo
			new File(tempConfigPath).delete();
		}
		
		// Genera il paths.json finale
		generaPathsJson(paths, names, stati, ox, oy, dx, dy);
		
		System.out.println("Generazione completata!");
	}
	
	private static String generaConfigPerCombinazione(int mask) {
		StringBuilder config = new StringBuilder();
		config.append("{\n");
		config.append("\t\"width\": " + WIDTH + ",\n");
		config.append("\t\"height\": " + HEIGHT + ",\n");
		config.append("\t\"randomSeed\": " + SEED + ",\n");
		config.append("\t\"maxOstacoli\": {\n");
		
		for (int bit = 0; bit < KEYS.length; bit++) {
			int value = ((mask & (1 << bit)) != 0) ? MAX_VALUES[bit] : 0;
			config.append(String.format("\t\t\"%s\": %d", KEYS[bit], value));
			if (bit < KEYS.length - 1) {
				config.append(",");
			}
			config.append("\n");
		}
		
		config.append("\t},\n");
		config.append("\t\"load\":{\n");
		config.append("\t\t\"path\":\"griglie.int.json\",\n");
		config.append("\t\t\"name\":\"test1\"\n");
		config.append("\t},\n");
		config.append("\t\"applet\":{\n");
		config.append("\t\t\"width\":1200,\n");
		config.append("\t\t\"height\":600,\n");
		config.append("\t\t\"demo\":false,\n");
		config.append("\t\t\"showText\":false,\n");
		config.append("\t\t\"palette\":[\"0xff0000\", \"0xff4000\", \"0xff8000\", \"0xffb000\",\"0x004000\", \"0xffffff\", \"0x000000\", \"0xff00ff\", \"0x00ff00\"],\n");
		config.append("\t\t\"paletteLegend\":[\"origine\",\"regina\",\"contesto\",\"complemento\",\"frontiera\",\"vuota\",\"ostacolo\",\"destinazione\",\"landmark\"]\n");
		config.append("\t}\n");
		config.append("}");
		
		return config.toString();
	}
	
	private static String generaRappresentazioneGriglia(IGrigliaConOrigine griglia) {
		// Genera la rappresentazione come in AppletMain con Shift+P
		String grigliaString = griglia.collect(
				c -> c.is(nicolas.StatoCella.OSTACOLO) ? "1" : " ",
						Collectors.joining(",", "[", ",]"),
						Collectors.joining(",\n", "[\n", "\n]"));
		
		return grigliaString;
	}
	
	private static int determinaTipoGriglia(IGrigliaConOrigine griglia) {
		return griglia.getTipo();
	}
	
	private static String generaJsonGriglia(String numeroTipoGriglia, String grigliaString) {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\t\"").append(numeroTipoGriglia).append("\": ");
		json.append(grigliaString);
		json.append("\n}");
		return json.toString();
	}
	
	private static void generaPathsJson(List<String> paths, List<String> names, List<Integer> stati,
			List<Integer> ox, List<Integer> oy,
			List<Integer> dx, List<Integer> dy) {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		
		// Paths
		json.append("\t\"path\": [");
		for (int i = 0; i < paths.size(); i++) {
			json.append("\"").append(paths.get(i)).append("\"");
			if (i < paths.size() - 1) {
				json.append(", ");
			}
		}
		json.append("],\n");
		
		// Names
		json.append("\t\"name\": [");
		for (int i = 0; i < names.size(); i++) {
			json.append("\"").append(names.get(i)).append("\"");
			if (i < names.size() - 1) {
				json.append(", ");
			}
		}
		json.append("],\n");
		
		// Stati
		json.append("\t\"stati\": [");
		for (int i = 0; i < stati.size(); i++) {
			json.append("\"").append(stati.get(i)).append("\"");
			if (i < stati.size() - 1) {
				json.append(", ");
			}
		}
		json.append("],\n");
		
		// ox
		json.append("\t\"ox\": [");
		for (int i = 0; i < ox.size(); i++) {
			json.append(ox.get(i));
			if (i < ox.size() - 1) {
				json.append(", ");
			}
		}
		json.append("],\n");
		
		// oy
		json.append("\t\"oy\": [");
		for (int i = 0; i < oy.size(); i++) {
			json.append(oy.get(i));
			if (i < oy.size() - 1) {
				json.append(", ");
			}
		}
		json.append("],\n");	
		
		// dx
		json.append("\t\"dx\": [");
		for (int i = 0; i < dx.size(); i++) {
			json.append(dx.get(i));
			if (i < dx.size() - 1) {
				json.append(", ");
			}
		}
		json.append("],\n");
		
		// dy
		json.append("\t\"dy\": [");
		for (int i = 0; i < dy.size(); i++) {
			json.append(dy.get(i));
			if (i < dy.size() - 1) {
				json.append(", ");
			}
		}
		json.append("],\n");
		
		// txt
		json.append("\t\"txt\": \"tipoGriglia\"\n");
		json.append("}");
		
		try (FileWriter writer = new FileWriter("src/sperimentazione/tipoGriglia/paths.json")) {
			writer.write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
