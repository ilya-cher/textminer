package ru.spbau.textminer;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.textminer.extraction.Chunk;
import ru.spbau.textminer.extraction.Relation;
import ru.spbau.textminer.text.ParserException;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Config config = parseParameters(args, loadDefaultConfig());
            if (config != null) {
                makeDataset(new ChunkerDatasetBuilder(), chunkerWriters, config.getChunkerTrainFileName(), config.getChunkerTestFileName(), config);
                makeDataset(new RelationDatasetBuilder(), relationWriters, config.getRelTrainFileName(), config.getRelTestFileName(), config);
            }
        } catch (ParserException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ConfigException e) {
            logger.error(e.getMessage());
        }
    }

    private static <T> void makeDataset(DatasetBuilder<T> builder, Map<String, DatasetWriter<T>> writers,
                                        String trainFileName, String testFileName, Config config) throws ParserException, IOException {
        DatasetWriter<T> writer = writers.get(config.getFormatName());
        if (writer != null) {
            Dataset<T> dataset = builder.build(config.getFilesPath(),
                    config.getFilesExt(), config.getTestFraction());

            System.out.println("#Sentences: " + dataset.getSentencesNum());
            System.out.println("#Extractions: " + dataset.getDatasetSize());

            writer.writeDataset(dataset, new File(trainFileName),
                    new File(testFileName));
        } else {
            logger.error("Couldn't get dataset writer");
        }
    }

    private static Config loadDefaultConfig() throws ConfigException, IOException {
        FileReader propertiesReader = null;
        try {
            Properties properties = new Properties();
            propertiesReader = new FileReader("config/default.properties");
            properties.load(propertiesReader);
            ConfigBuilder builder = new ConfigBuilder().from(properties);
            return builder.build();
        } finally {
            if (propertiesReader != null) {
                propertiesReader.close();
            }
        }
    }

    private static Config parseParameters(String[] args, Config defaults) throws ConfigException {
        Options options = new Options();
        options.addOption("e", "files-ext", true, "extension of treebank files");
        options.addOption("p", "files-path", true, "path to treebank files");
        options.addOption("r", "chunker-train-file", true, "chunker training file name");
        options.addOption("t", "chunker-test-file", true, "chunker test file name");
        options.addOption("u", "rel-train-file", true, "relations training file name");
        options.addOption("o", "rel-test-file", true, "relations test file name");
        options.addOption("s", "test-fraction", true, "test instances fraction");
        options.addOption("f", "format", true, "training and test files format (currently supported: " + defaults.getFormatName() + ")");
        options.addOption("h", "help", false, "print help message");
        CommandLineParser parser = new GnuParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                printUsage(options);
                return null;
            }
            ConfigBuilder builder = new ConfigBuilder(defaults).from(cmd);
            return builder.build();
        } catch (ParseException e) {
            logger.error("Invalid command line params");
            printUsage(options);
            return defaults;
        }
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java [-cp ...] " + Main.class.getCanonicalName(), options);
    }

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Map<String, DatasetWriter<Relation>> relationWriters = new HashMap<String, DatasetWriter<Relation>>();
    static {
        relationWriters.put("mallet", new MalletRelationDatasetWriter());
    }
    private static final Map<String, DatasetWriter<Chunk>> chunkerWriters = new HashMap<String, DatasetWriter<Chunk>>();
    static {
        chunkerWriters.put("mallet", new MalletChunkerDatasetWriter());
    }
}
