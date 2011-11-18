package ru.spbau.textminer.text;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;

public class Sentence {
    public Sentence(Element sElement, String filePath) throws InvalidSentenceException {
        this.filePath = filePath;
        sentenceId = readSentenceId(sElement);
        readWords(sElement);
        root.buildTree(words);
    }

    private int readSentenceId(Element sElement) throws InvalidSentenceException {
        try {
            return Integer.parseInt(sElement.getAttribute(ID_ATTR));
        } catch (NumberFormatException e) {
            throw new InvalidSentenceException("Invalid sentence ID: '" + sElement.getAttribute(ID_ATTR) +
                    "' (file path: " + filePath + ")", e);
        }
    }

    private void readWords(Element sElement) throws InvalidSentenceException {
        NodeList wList = sElement.getElementsByTagName(W_TAG);
        if (wList.getLength() == 0) {
            throw new InvalidSentenceException("Sentence has no words (sentence ID: " + sentenceId + ", " + filePath + ")");
        }

        words = new ArrayList<Word>();
        for (int i = 0; i < wList.getLength(); i++) {
            Element wElement = (Element) wList.item(i);
            try {
                Word word = new Word(wElement, this);
                words.add(word);
                if (word.isRoot()) {
                    if (root == null) {
                        root = word;
                    } else {
                        throw new InvalidSentenceException("Duplicate root word '" + word.getText() +
                                "' (sentence ID: " + sentenceId + ", " + filePath + ")");
                    }
                }
            } catch (InvalidWordException e) {
                throw new InvalidSentenceException("Sentence has invalid word '" + wElement.getTextContent() +
                        "': " + e.getMessage() + " (sentence ID: " + sentenceId + ", " + filePath + ")", e);
            }
        }
        if (root == null) {
            throw new InvalidSentenceException("No root word specified (sentence ID: " +
                    sentenceId + ", " + filePath + ")");
        }
    }

    public int getId() {
        return sentenceId;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<Word> getWords() {
        return Collections.unmodifiableList(words);
    }

    public Word getRootWord() {
        return root;
    }

    private int sentenceId;
    private String filePath;
    private List<Word> words;
    private Word root;

    private static final String ID_ATTR = "ID";
    private static final String W_TAG = "W";
}
