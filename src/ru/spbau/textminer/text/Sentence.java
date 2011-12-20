package ru.spbau.textminer.text;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.spbau.textminer.extraction.ExtrUtil;

import java.util.*;

public class Sentence {
    public Sentence(Word root, List<Word> words) {
        this.root = root;
        this.words = words;
    }

    public List<Word> getWords() {
        return Collections.unmodifiableList(words);
    }

    public Word getRootWord() {
        return root;
    }

    public String toString() {
        return ExtrUtil.printInOrder(this);
    }

    private List<Word> words;
    private Word root;
}
