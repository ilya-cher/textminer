package ru.spbau.textminer;

import com.google.common.collect.Multimap;
import ru.spbau.textminer.extraction.Chunk;
import ru.spbau.textminer.extraction.ChunkType;
import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.Word;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MalletChunkerDatasetWriter implements DatasetWriter<Chunk> {
    public void writeDataset(Dataset<Chunk> dataset, File trainingFile, File testFile) throws IOException {
        writeData(dataset.getTrainingSet(), trainingFile);
        writeData(dataset.getTestSet(), testFile);
    }

    private void writeData(Multimap<Sentence, Chunk> data, File file) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<Sentence, Collection<Chunk>> entry: data.asMap().entrySet()) {
                Sentence sentence = entry.getKey();
                Collection<Chunk> chunks = entry.getValue();
                if (chunks.size() > 0) {
                    Map<Word, ChunkType> wordsInChunks = new HashMap<Word, ChunkType>();
                    for (Chunk chunk : chunks) {
                        for (Word word : chunk.getWords()) {
                            wordsInChunks.put(word, chunk.getType());
                        }
                    }

                    ChunkType prevType = null;
                    for (Word word : sentence.getWords()) {
                        String strTag;
                        ChunkType chunkType = wordsInChunks.get(word);
                        if (chunkType != null) {
                            if (chunkType == prevType) {
                                strTag = "I-" + chunkType;
                            } else {
                                strTag = "B-" + chunkType;
                            }
                            prevType = chunkType;
                        } else {
                            strTag = "O";
                            prevType = null;
                        }
                        writer.write(word.getText() + " " + word.getFeatures().getPOS() + " " + strTag + "\n");
                    }
                    writer.write("\n");
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
}
