package ru.spbau.textminer;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.textminer.extraction.Relation;
import ru.spbau.textminer.text.Config;
import ru.spbau.textminer.text.ParserException;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Config config = parseParameters(args, loadDefaultConfig());
            if (config != null) {
                RelationDatasetBuilder datasetBuilder = new RelationDatasetBuilder();
                Dataset<Relation> dataset = datasetBuilder.build(config.getFilesPath(),
                        config.getFilesExt(), config.getTestFraction());

                System.out.println("#Sentences: " + dataset.getSentencesNum());
                System.out.println("#Extractions: " + dataset.getDatasetSize());

                DatasetWriter<Relation> datasetWriter = writersMap.get(config.getFormatName());
                if (datasetWriter != null) {
                    datasetWriter.writeDataset(dataset, new File(config.getTrainFileName()),
                            new File(config.getTestFileName()));
                } else {
                    logger.error("Couldn't get dataset writer");
                }
            }
        } catch (ParserException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ConfigException e) {
            logger.error(e.getMessage());
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
        options.addOption("r", "train-file", true, "training file name");
        options.addOption("t", "test-file", true, "test file name");
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
    private static final Map<String, DatasetWriter<Relation>> writersMap = new HashMap<String, DatasetWriter<Relation>>();
    static {
        writersMap.put("mallet", new MalletRelationDatasetWriter());
    }
}
