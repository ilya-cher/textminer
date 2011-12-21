package ru.spbau.textminer;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.textminer.text.Config;
import java.util.Properties;

public class ConfigBuilder {
    public ConfigBuilder() {}

    public ConfigBuilder(Config defaults) {
        filesPath = defaults.getFilesPath();
        filesExt = defaults.getFilesExt();
        trainFileName = defaults.getTrainFileName();
        testFileName = defaults.getTestFileName();
        testFraction = defaults.getTestFraction();
        formatName = defaults.getFormatName();
    }

    public ConfigBuilder setFilesPath(String filesPath) {
        if (filesPath != null) {
            this.filesPath = filesPath;
        }
        return this;
    }

    public ConfigBuilder setFilesExt(String filesExt) {
        if (filesExt != null) {
            this.filesExt = filesExt;
        }
        return this;
    }

    public ConfigBuilder setTrainFileName(String trainFileName) {
        if (trainFileName != null) {
            this.trainFileName = trainFileName;
        }
        return this;
    }

    public ConfigBuilder setTestFileName(String testFileName) {
        if (testFileName != null) {
            this.testFileName = testFileName;
        }
        return this;
    }

    public ConfigBuilder setTestFraction(Double testFraction) {
        if (testFraction != null) {
            this.testFraction = testFraction;
        }
        return this;
    }

    public ConfigBuilder setFormatName(String formatName) {
        if (formatName != null) {
            this.formatName = formatName;
        }
        return this;
    }

    public ConfigBuilder from(Properties properties) {
        setFilesPath(properties.getProperty("filesPath"));
        setFilesExt(properties.getProperty("filesExt"));
        setTrainFileName(properties.getProperty("trainFileName"));
        setTestFileName(properties.getProperty("testFileName"));
        setTestFraction(getTestFractionValue(properties.getProperty("testFraction")));
        setFormatName(properties.getProperty("formatName"));
        return this;
    }

    public ConfigBuilder from(CommandLine cmd) {
        setFilesPath(cmd.getOptionValue("files-path"));
        setFilesExt(cmd.getOptionValue("files-ext"));
        setTrainFileName(cmd.getOptionValue("train-file"));
        setTestFileName(cmd.getOptionValue("test-file"));
        setTestFraction(getTestFractionValue(cmd.getOptionValue("test-fraction")));
        setFormatName(cmd.getOptionValue("format"));
        return this;
    }

    private static Double getTestFractionValue(String testFraction) {
        Double testFractionValue = null;
        if (testFraction != null) {
            try {
                testFractionValue = Double.parseDouble(testFraction);
                if (testFractionValue < 0 || testFractionValue > 1) {
                    logger.warn("Invalid testFraction");
                    testFractionValue = null;
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid testFraction");
                testFractionValue = null;
            }
        }
        return testFractionValue;
    }

    public Config build() throws ConfigException {
        if (checkNulls()) {
            throw new ConfigException("Config values are missing");
        }

        return new Config(filesPath, filesExt, trainFileName, testFileName, testFraction, formatName);
    }

    private boolean checkNulls() {
        return (filesPath == null ||
                filesExt == null ||
                trainFileName == null ||
                testFileName == null ||
                testFraction == null ||
                formatName == null);
    }

    private String filesPath;
    private String filesExt;
    private String trainFileName;
    private String testFileName;
    private Double testFraction;
    private String formatName;

    private static final Logger logger = LoggerFactory.getLogger(ConfigBuilder.class);
}
