package ru.spbau.textminer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import ru.spbau.textminer.extraction.Chunk;
import ru.spbau.textminer.text.Sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dataset<T> {
    public Dataset(Multimap<Sentence, T> dataMap, double testFraction) {
        List<Sentence> sentenceList = new ArrayList<Sentence>(dataMap.keySet());
        Collections.shuffle(sentenceList);
        int testsNum = (int) (sentenceList.size() * testFraction);
        if (testsNum < 0) {
            testsNum = 0;
        } else if (testsNum > sentenceList.size()) {
            testsNum = sentenceList.size();
        }

        List<Sentence> testSentences = sentenceList.subList(0, testsNum);
        List<Sentence> trainSentences = sentenceList.subList(testsNum, sentenceList.size());

        testSet = HashMultimap.create();
        trainingSet = HashMultimap.create();

        for (Sentence testSentence : testSentences) {
            testSet.putAll(testSentence, dataMap.get(testSentence));
        }

        for (Sentence trainSentence : trainSentences) {
            trainingSet.putAll(trainSentence, dataMap.get(trainSentence));
        }

        examplesNum = dataMap.size();
        sentencesNum = sentenceList.size();
    }

    public Multimap<Sentence, T> getTrainingSet() {
        return Multimaps.unmodifiableMultimap(trainingSet);
    }

    public Multimap<Sentence, T> getTestSet() {
        return Multimaps.unmodifiableMultimap(testSet);
    }

    public int getDatasetSize() {
        return examplesNum;
    }

    public int getSentencesNum() {
        return sentencesNum;
    }

    private final Multimap<Sentence, T> trainingSet;
    private final Multimap<Sentence, T> testSet;
    private final int examplesNum;
    private final int sentencesNum;
}
