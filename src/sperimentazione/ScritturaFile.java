package sperimentazione;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ScritturaFile {
	
	public static void writeToFile(String filePath, String content) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
			writer.append(content);
			writer.newLine(); 
		} catch (Exception e) {
			System.err.println("Errore nella scrittura su file: " + e.getMessage());
		}
	}
	
	public static void pulisciFile(String filePath) {
		try (PrintWriter writer = new PrintWriter(filePath)) {}
		catch (IOException e) {
			System.err.println("Errore nella pulizia del file: " + e.getMessage());
		}
	}
	
}

