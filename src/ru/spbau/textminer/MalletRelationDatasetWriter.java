package ru.spbau.textminer;

import com.google.common.collect.Multimap;
import ru.spbau.textminer.extraction.Chunk;
import ru.spbau.textminer.extraction.ChunkType;
import ru.spbau.textminer.extraction.Relation;
import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.Word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MalletRelationDatasetWriter implements DatasetWriter<Relation> {
    public void writeDataset(Dataset<Relation> dataset, File trainingFile, File testFile) throws IOException {
        writeData(dataset.getTrainingSet(), trainingFile);
        writeData(dataset.getTestSet(), testFile);
    }

    private void writeData(Multimap<Sentence, Relation> data, File file) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (Sentence sentence : data.keySet()) {
                //writer.write(sentence + "\n");
                Collection<Relation> relations = data.get(sentence);
                for (Relation relation : relations) {
                    String example = getRelationExample(sentence, relation);
                    //writer.write(relation + "\n");
                    writer.write(example + "\n");
                }
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {}
            }
        }
    }

    private String getRelationExample(Sentence sentence, Relation relation) {
        int left = getMinNumber(relation.getLeftChunk().getWords());
        int right = getMaxNumber(relation.getRightChunk().getWords());

        StringBuilder sb = new StringBuilder();
        for (int index = left; index <= right; index++) {
            Word word = sentence.getWords().get(index);
            sb.append(word.getLemma() + " " + word.getFeatures().getPOS() + " ");
            if (relation.getRel().contains(word)) {
                sb.append("IN\n");
            } else {
                sb.append("O\n");
            }
        }
        return sb.toString();
    }

    private int getMaxNumber(Collection<Word> words) {
        int maxNumber = -1;
        for (Word word : words) {
            if (maxNumber < word.getNumber()) {
                maxNumber = word.getNumber();
            }
        }
        return maxNumber;
    }

    private int getMinNumber(Collection<Word> words) {
        Integer minNumber = null;
        for (Word word : words) {
            if (minNumber == null) {
                minNumber = word.getNumber();
            } else if (minNumber > word.getNumber()) {
                minNumber = word.getNumber();
            }
        }
        return minNumber;
    }
}
