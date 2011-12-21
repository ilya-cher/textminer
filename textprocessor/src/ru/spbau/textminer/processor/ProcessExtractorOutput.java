package ru.spbau.textminer.processor;
import java.util.*;
import java.io.*;

public class ProcessExtractorOutput {
	public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java [-cp ...] ru.spbau.textminer.ProcessExtractor input-file output-file");
            return;
        }

        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "cp1251"));
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "cp1251"));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf('[') != -1) {
                    line = removeFeatures(line).replace(" ] [ ", " ");
                    int index = line.indexOf('[');
                    if (line.indexOf('[', index + 1) == -1) {
                        writer.println(line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {}
            }
            if (writer != null) {
                writer.close();
            }
        }
	}

	private static String removeFeatures(String words) {
		Scanner scanner = new Scanner(words);
		StringBuilder sb = new StringBuilder();
		while (scanner.hasNext()) {
			String word = scanner.next().trim();
			if (word.indexOf('{') != -1) {
				sb.append(word.substring(0, word.indexOf('{')) + " ");
			} else {
				sb.append(word + " ");
			}
		}
		return sb.toString();
	}
}