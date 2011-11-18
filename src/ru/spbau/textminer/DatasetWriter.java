package ru.spbau.textminer;

import java.io.File;
import java.io.IOException;

public interface DatasetWriter {
    void writeDataset(ChunkerDataset dataset, File trainingFile, File testFile) throws IOException;
}
