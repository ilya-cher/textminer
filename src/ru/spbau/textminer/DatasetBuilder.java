package ru.spbau.textminer;

import ru.spbau.textminer.text.ParserException;

import java.io.IOException;

public interface DatasetBuilder<T> {
    Dataset<T> build(String filesPath, String filesExt, double testFraction) throws ParserException, IOException;
}
