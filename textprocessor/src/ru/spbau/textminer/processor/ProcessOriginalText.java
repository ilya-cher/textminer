package ru.spbau.textminer.processor;
import java.util.*;
import java.io.*;

public class ProcessOriginalText {
	public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java [-cp ...] ru.spbau.textminer.ProcessOriginalText input-file output-file");
            return;
        }

        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "cp1251"));
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "cp1251"));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            List<String> sentences = getSentences(sb.toString());
            sentences = replaceWhitespace(sentences);

            for (String sentence : sentences) {
                writer.println(sentence);
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

	private static List<String> replaceWhitespace(List<String> sentences) {
		List<String> result = new ArrayList<String>();
		for (String sentence : sentences) {
			result.add(sentence.replaceAll("\\p{Space}", " "));
		}
		return result;
	}

	private static List<String> getSentences(String text) {
		List<String> sentences = new ArrayList<String>();
		int start = 0;
		int quoteNum = 0;
		int bracketsNum = 0;
		char ch;
		for (int i = 0; i < text.length(); i++) {
			ch = text.charAt(i);
			if ((i == start) && ((ch == '.') || (ch == '!') || (ch == '?'))) {
				start = i + 1;
				continue;
			}

			if (ch == '(' || ch == '[') {
				bracketsNum++;
			}
			if (ch == ')' || ch == ']') {
				bracketsNum--;
				if (bracketsNum < 0) {
					bracketsNum = 0;
				}
			}

			if (ch == '"' || ch == '\'') {
				if (quoteNum > 0) {
					if (matchLetterDigit(text, i + 1))  {
						quoteNum++;
					} else {
						quoteNum--;
					}
				} else {
					quoteNum = 1;
				}
				continue;
			}
			if (ch == '«') {
				quoteNum++;
			}
			if (ch == '»') {
				quoteNum--;
				if (quoteNum < 0) {
					quoteNum = 0;
				}
			}

			if (ch == '!' || ch == '?') {
				if (quoteNum > 0 || bracketsNum > 0) {
					continue;
				} else {
					while ((i < text.length()) && (text.charAt(i) == '!' || text.charAt(i) == '?')) {
						i++;
					}
					sentences.add(text.substring(start, i).trim());
					quoteNum = 0;
					start = i;
					i--;
					continue;
				}
			}

			if (ch == '.') {
				if (quoteNum > 0 || bracketsNum > 0) {
					continue;
				}

				int k = getPrevNonWhitespace(i - 1, text);
				if (k != -1) {
					if (!match(text, k, ')') && !match(text, k, ']') && !matchCloseQuote(text, k) &&
						!(matchLetterDigit(text, k) && matchLetterDigit(text, k - 1))) {
						continue;
					}
				} else {
					start = i + 1;
					continue;
				}

				if ((!match(text, i - 1, '.')) && match(text, i + 1, '.') && match(text, i + 2, '.')) {
					i += 2;
					if (matchWhitespace(text, i + 1)) {
						int j = getNextNonWhitespace(i + 1, text);
						if (j != -1) {
							char ch2 = text.charAt(j);
							if (Character.isDigit(ch2) || Character.isUpperCase(ch2) ||
									ch2 == '"' || ch2 == '\'' || ch2 == '«') {
								sentences.add(text.substring(start, i + 1).trim());
								quoteNum = 0;
								start = i + 1;
								continue;
							}
						} else {
							sentences.add(text.substring(start, i + 1).trim());
							quoteNum = 0;
							//start = i + 1;
							break;
						}
					}
				} else if (matchWhitespace(text, i + 1)) {
					int j = getNextNonWhitespace(i + 1, text);
					if (j != -1) {
						char ch2 = text.charAt(j);
						if (!Character.isLowerCase(ch2)) {
							sentences.add(text.substring(start, i + 1).trim());
							quoteNum = 0;
							start = i + 1;
							continue;
						}
					} else {
						sentences.add(text.substring(start, i + 1).trim());
						quoteNum = 0;
						break;
					}
				} else {
					if (!match(text, i + 2, '.') && matchUpperCase(text, i + 1)) {
						sentences.add(text.substring(start, i + 1).trim());
						quoteNum = 0;
						start = i + 1;
						continue;
					}
				}
			}
		}

		return sentences;
	}

	private static boolean matchCloseQuote(String text, int i) {
		return (i < text.length()) && (text.charAt(i) == '"' || text.charAt(i) == '\'' || text.charAt(i) == '»');
	}

	private static boolean matchUpperCase(String text, int i) {
		return (i < text.length()) && (Character.isUpperCase(text.charAt(i)));
	}

	private static boolean matchLetterDigit(String text, int i) {
		return (i < text.length()) && (Character.isLetter(text.charAt(i)) || Character.isDigit(text.charAt(i)));
	}

	private static boolean matchWhitespace(String text, int i) {
		return (i < text.length()) && (Character.isWhitespace(text.charAt(i)));
	}

	private static boolean match(String text, int i, char ch) {
		return (i < text.length()) && (text.charAt(i) == ch);
	}

	private static int getPrevNonWhitespace(int i, String text) {
		while (i >= 0 && Character.isWhitespace(text.charAt(i))) {
			i--;
		}
		if (i >= 0) {
			return i;
		} else {
			return -1;
		}
	}

	private static int getNextNonWhitespace(int i, String text) {
		while (i < text.length() && Character.isWhitespace(text.charAt(i))) {
			i++;
		}
		if (i < text.length()) {
			return i;
		} else {
			return -1;
		}
	}
}