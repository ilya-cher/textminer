package ru.spbau.textminer;

public class Config {
    public Config(String filesPath, String filesExt, String chunkerTrainFileName, String chunkerTestFileName,
                  String relTrainFileName, String relTestFileName,
                  double testFraction, String formatName) {
        this.filesPath = filesPath;
        this.filesExt = filesExt;
        this.chunkerTrainFileName = chunkerTrainFileName;
        this.chunkerTestFileName = chunkerTestFileName;
        this.relTrainFileName = relTrainFileName;
        this.relTestFileName = relTestFileName;
        this.testFraction = testFraction;
        this.formatName = formatName;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public String getFilesExt() {
        return filesExt;
    }

    public String getChunkerTrainFileName() {
        return chunkerTrainFileName;
    }

    public String getChunkerTestFileName() {
        return chunkerTestFileName;
    }

    public String getRelTrainFileName() {
        return relTrainFileName;
    }

    public String getRelTestFileName() {
        return relTestFileName;
    }

    public double getTestFraction() {
        return testFraction;
    }

    public String getFormatName() {
        return formatName;
    }

    private final String filesPath;
    private final String filesExt;
    private final String chunkerTrainFileName;
    private final String chunkerTestFileName;
    private final String relTrainFileName;
    private final String relTestFileName;
    private final Double testFraction;
    private final String formatName;
}
