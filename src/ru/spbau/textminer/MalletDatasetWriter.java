package ru.spbau.textminer;

import ru.spbau.textminer.extraction.Chunk;
import ru.spbau.textminer.extraction.ChunkType;
import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.Word;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MalletDatasetWriter implements DatasetWriter {
    public void writeDataset(ChunkerDataset dataset, File trainingFile, File testFile) throws IOException {
        writeData(dataset.getTrainingSet(), trainingFile);
        writeData(dataset.getTestSet(), testFile);
    }

    private void writeData(Map<Sentence, List<Chunk>> data, File file) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<Sentence, List<Chunk>> entry: data.entrySet()) {
                Sentence sentence = entry.getKey();
                List<Chunk> chunks = entry.getValue();
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
