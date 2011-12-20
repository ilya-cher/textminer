package ru.spbau.textminer.text;

public class Config {
    public Config(String filesPath, String filesExt, String trainFileName, String testFileName,
                  double testFraction, String formatName) {
        this.filesPath = filesPath;
        this.filesExt = filesExt;
        this.trainFileName = trainFileName;
        this.testFileName = testFileName;
        this.testFraction = testFraction;
        this.formatName = formatName;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public String getFilesExt() {
        return filesExt;
    }

    public String getTrainFileName() {
        return trainFileName;
    }

    public String getTestFileName() {
        return testFileName;
    }

    public double getTestFraction() {
        return testFraction;
    }

    public String getFormatName() {
        return formatName;
    }

    private final String filesPath;
    private final String filesExt;
    private final String trainFileName;
    private final String testFileName;
    private final Double testFraction;
    private final String formatName;
}
