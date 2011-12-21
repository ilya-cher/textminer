package ru.spbau.textminer;

import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import ru.spbau.textminer.extraction.Relation;
import ru.spbau.textminer.extraction.RelationExtractor;
import ru.spbau.textminer.text.ParserException;
import ru.spbau.textminer.text.Sentence;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class RelationDatasetBuilder implements DatasetBuilder<Relation> {
    public RelationDatasetBuilder() {
        parser = new TreeBankParser();
        extractor = new RelationExtractor();
    }

    public Dataset<Relation> build(String filesPath, String filesExt, double testFraction) throws ParserException, IOException {
        Collection<File> files = FileUtils.listFiles(new File(filesPath), new String[]{filesExt}, true);
        List<Sentence> sentences = parser.parseFiles(files);
        Multimap<Sentence, Relation> relations = extractor.extract(sentences);
        return new Dataset<Relation>(relations, testFraction);
    }

    private final TreeBankParser parser;
    private final RelationExtractor extractor;
}
