package ru.spbau.textminer;

import ru.spbau.textminer.extraction.Chunk;
import ru.spbau.textminer.extraction.ChunkExtractor;
import ru.spbau.textminer.text.Sentence;

import java.util.*;

public class ChunkerDataset {
    public ChunkerDataset(List<Sentence> sentences, double testFraction) {
        List<Sentence> sentenceList = new ArrayList<Sentence>(sentences);
        Collections.shuffle(sentenceList);
        int testsNum = (int) (sentenceList.size() * testFraction);
        if (testsNum < 0) {
            testsNum = 0;
        } else if (testsNum > sentenceList.size()) {
            testsNum = sentenceList.size();
        }
        ChunkExtractor chunkExtractor = new ChunkExtractor();
        testSet = chunkExtractor.extract(sentenceList.subList(0, testsNum));
        trainingSet = chunkExtractor.extract(sentenceList.subList(testsNum, sentenceList.size()));

        chunksNum = 0;
        for (Sentence sentence : trainingSet.keySet()) {
            chunksNum += trainingSet.get(sentence).size();
        }
        for (Sentence sentence : testSet.keySet()) {
            chunksNum += testSet.get(sentence).size();
        }
    }

    public Map<Sentence, List<Chunk>> getTrainingSet() {
        return trainingSet;
    }

    public Map<Sentence, List<Chunk>> getTestSet() {
        return testSet;
    }

    public int getChunksNum() {
        return chunksNum;
    }

    private Map<Sentence, List<Chunk>> trainingSet;
    private Map<Sentence, List<Chunk>> testSet;
    private int chunksNum;
}
