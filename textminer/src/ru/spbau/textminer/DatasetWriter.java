package ru.spbau.textminer;

import java.io.File;
import java.io.IOException;

public interface DatasetWriter<T> {
    void writeDataset(Dataset<T> dataset, File trainingFile, File testFile) throws IOException;
}
