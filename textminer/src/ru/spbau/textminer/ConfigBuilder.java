package ru.spbau.textminer;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConfigBuilder {
    public ConfigBuilder() {}

    public ConfigBuilder(Config defaults) {
        filesPath = defaults.getFilesPath();
        filesExt = defaults.getFilesExt();
        chunkerTrainFileName = defaults.getChunkerTrainFileName();
        chunkerTestFileName = defaults.getChunkerTestFileName();
        relTrainFileName = defaults.getRelTrainFileName();
        relTestFileName = defaults.getRelTestFileName();
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

    public ConfigBuilder setChunkerTrainFileName(String chunkerTrainFileName) {
        if (chunkerTrainFileName != null) {
            this.chunkerTrainFileName = chunkerTrainFileName;
        }
        return this;
    }

    public ConfigBuilder setChunkerTestFileName(String chunkerTestFileName) {
        if (chunkerTestFileName != null) {
            this.chunkerTestFileName = chunkerTestFileName;
        }
        return this;
    }

    public ConfigBuilder setRelTrainFileName(String relTrainFileName) {
        if (relTrainFileName != null) {
            this.relTrainFileName = relTrainFileName;
        }
        return this;
    }

    public ConfigBuilder setRelTestFileName(String relTestFileName) {
        if (relTestFileName != null) {
            this.relTestFileName = relTestFileName;
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
        setChunkerTrainFileName(properties.getProperty("chunkerTrainFileName"));
        setChunkerTestFileName(properties.getProperty("chunkerTestFileName"));
        setRelTrainFileName(properties.getProperty("relTrainFileName"));
        setRelTestFileName(properties.getProperty("relTestFileName"));
        setTestFraction(getTestFractionValue(properties.getProperty("testFraction")));
        setFormatName(properties.getProperty("formatName"));
        return this;
    }

    public ConfigBuilder from(CommandLine cmd) {
        setFilesPath(cmd.getOptionValue("files-path"));
        setFilesExt(cmd.getOptionValue("files-ext"));
        setChunkerTrainFileName(cmd.getOptionValue("chunker-train-file"));
        setChunkerTestFileName(cmd.getOptionValue("chunker-test-file"));
        setRelTrainFileName(cmd.getOptionValue("rel-train-file"));
        setRelTestFileName(cmd.getOptionValue("rel-test-file"));
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

        return new Config(filesPath, filesExt, chunkerTrainFileName, chunkerTestFileName,
                relTrainFileName, relTestFileName, testFraction, formatName);
    }

    private boolean checkNulls() {
        return (filesPath == null ||
                filesExt == null ||
                chunkerTrainFileName == null ||
                chunkerTestFileName == null ||
                relTrainFileName == null ||
                relTestFileName == null ||
                testFraction == null ||
                formatName == null);
    }

    private String filesPath;
    private String filesExt;
    private String chunkerTrainFileName;
    private String chunkerTestFileName;
    private String relTrainFileName;
    private String relTestFileName;
    private Double testFraction;
    private String formatName;

    private static final Logger logger = LoggerFactory.getLogger(ConfigBuilder.class);
}
