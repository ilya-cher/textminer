package ru.spbau.textminer;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.textminer.extraction.Chunk;
import ru.spbau.textminer.extraction.ChunkExtractor;
import ru.spbau.textminer.text.ParserException;
import ru.spbau.textminer.text.Sentence;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChunkerDatasetBuilder {
    public static void main(String[] args) {
        if (parseParameters(args)) {
            try {
                Collection<File> files = FileUtils.listFiles(new File(filesPath), new String[]{filesExt}, true);
                List<Sentence> sentences = new TreeBankParser().parseFiles(files);
                ChunkerDataset dataset = new ChunkerDataset(sentences, testFraction);

                System.out.println("#Sentences: " + sentences.size());
                System.out.println("#Extractions: " + dataset.getChunksNum());

                datasetWriter.writeDataset(dataset, new File(trainFileName), new File(testFileName));
            } catch (ParserConfigurationException e) {
                logger.error(e.getMessage());
            } catch (ParserException e) {
                logger.error(e.getMessage());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private static boolean parseParameters(String[] args) {
        try {
            Options options = new Options();
            options.addOption("e", "files-ext", true, "extension of treebank files");
            options.addOption("p", "files-path", true, "path to treebank files");
            options.addOption("r", "train-file", true, "training file name");
            options.addOption("t", "test-file", true, "test file name");
            options.addOption("s", "test-fraction", true, "test instances fraction");
            options.addOption("f", "format", true, "training and test files format (currently supported: mallet)");
            options.addOption("h", "help", false, "print help message");

            CommandLineParser parser = new GnuParser();
            CommandLine cmd  = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java [-cp ...] " + ChunkerDatasetBuilder.class.getCanonicalName(), options);
                return false;
            }
            filesExt = getOptionValue(cmd, "files-ext", defaultFilesExt);
            filesPath = getOptionValue(cmd, "files-path", defaultFilesPath);
            trainFileName = getOptionValue(cmd, "train-file", defaultTrainFileName);
            testFileName = getOptionValue(cmd, "test-file", defaultTestFileName);
            testFraction = getOptionValue(cmd, "test-fraction", defaultTestFraction);
            String formatName = getOptionValue(cmd, "format", defaultFormatName);
            if (writersMap.get(formatName) != null) {
                datasetWriter = writersMap.get(formatName);
            } else {
                logger.warn("Invalid format name: '" + formatName + "'\n" +
                                        "Assuming '" + defaultFormatName + "' format");
                datasetWriter = writersMap.get(defaultFormatName);
            }

        } catch (ParseException e) {
            logger.warn(e.getMessage());
        }
        return true;
    }

    private static String getOptionValue(CommandLine cmd, String optionName, String defaultValue) {
        if (cmd.hasOption(optionName)) {
            return cmd.getOptionValue(optionName);
        } else {
            logger.info("using default value for " + optionName + ": " + defaultValue);
            return defaultValue;
        }
    }

    private static double getOptionValue(CommandLine cmd, String optionName, double defaultValue) {
        if (cmd.hasOption((optionName))) {
            try {
                return Double.parseDouble(cmd.getOptionValue(optionName));
            } catch (NumberFormatException e) {
                logger.warn("invalid option value for " + optionName);
            }
        }
        logger.info("using default value for " + optionName + ": " + defaultValue);
        return defaultValue;
    }

    private static String filesExt;
    private static String filesPath;
    private static String trainFileName;
    private static String testFileName;
    private static double testFraction;
    private static DatasetWriter datasetWriter;

    private static final String defaultFilesExt = "tgt";
    private static final String defaultFilesPath = "trees";
    private static final String defaultTrainFileName = "train.txt";
    private static final String defaultTestFileName = "test.txt";
    private static final double defaultTestFraction = 0.1;
    private static final String defaultFormatName = "mallet";
    private static final DatasetWriter defaultWriter = new MalletDatasetWriter();
    private static final Map<String, DatasetWriter> writersMap = new HashMap<String, DatasetWriter>();
    static {
        writersMap.put(defaultFormatName, defaultWriter);
    }
    private static final Logger logger = LoggerFactory.getLogger(ChunkerDatasetBuilder.class);
}
