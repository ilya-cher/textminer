package ru.spbau.textminer.processor;

import java.io.*;

public class ProcessMystemOutput {
	public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java [-cp ...] ru.spbau.textminer.ProcessMystemOutput input-file output-file");
            return;
        }

        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "cp1251"));
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "cp1251"));

            String line = "";
            while ((line != null) && ((line = reader.readLine()) != null)) {
                do {
                    if (!line.contains("??") && line.trim().length() > 0) {
                        int i = line.indexOf('}');
                        if ((line.indexOf('|') != -1) && (line.indexOf('|') < i)) {
                            i = line.indexOf('|');
                        }
                        line = line.substring(0, i);

                        i = line.indexOf('{');
                        String word = line.substring(0, i);
                        i = line.indexOf('=');
                        i++;
                        String rest = line.substring(i);
                        i = rest.indexOf('=');
                        if ((rest.indexOf(',') != -1) && (i > rest.indexOf(','))) {
                            i = rest.indexOf(',');
                        }
                        String pos = rest.substring(0, i);
                        String str = word + "{" + rest + "}_" + convert(pos) + " ";
                        writer.print(str);
                    }
                    line = reader.readLine();
                } while (line != null && line.trim().length() != 0);
                writer.println();
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

	private static String convert(String pos) {
		pos = pos.trim();
		if (pos.equals("ADVPRO")) {
			return "ADV";
		} else if (pos.equals("ANUM")) {
			return "NUM";
		} else if (pos.equals("APRO")) {
			return "S";
		} else if (pos.equals("SPRO")) {
			return "S";
		} else {
			return pos;
		}
	}
}