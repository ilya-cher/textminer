package ru.spbau.textminer;

import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.textminer.extraction.Chunk;
import ru.spbau.textminer.extraction.ChunkExtractor;
import ru.spbau.textminer.text.ParserException;
import ru.spbau.textminer.text.Sentence;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChunkerDatasetBuilder implements DatasetBuilder<Chunk> {
    public ChunkerDatasetBuilder() {
        parser = new TreeBankParser();
        chunkExtractor = new ChunkExtractor();
    }

    public Dataset<Chunk> build(String filesPath, String filesExt, double testFraction) throws ParserException, IOException {
        Collection<File> files = FileUtils.listFiles(new File(filesPath), new String[]{filesExt}, true);
        List<Sentence> sentences = parser.parseFiles(files);
        Multimap<Sentence, Chunk> chunks = chunkExtractor.extract(sentences);
        return new Dataset<Chunk>(chunks, testFraction);
    }

    private final TreeBankParser parser;
    private final ChunkExtractor chunkExtractor;
}
