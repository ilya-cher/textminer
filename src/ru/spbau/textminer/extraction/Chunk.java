package ru.spbau.textminer.extraction;

import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.Word;

import java.util.Set;

public class Chunk {
    public Chunk(Set<Word> words, Word head, ChunkType type, Sentence sentence) {
        this.words = words;
        this.head = head;
        this.type = type;
        this.sentence = sentence;
    }

    public Set<Word> getWords() {
        return words;
    }

    public Word getHead() {
        return head;
    }

    public ChunkType getType() {
        return type;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public String getPhrase() {
        return ExtrUtil.printInOrder(words);
    }

    public String toString() {
        String result =   getPhrase() + " [" + type + " : " + head.getText() +  "] ";/* + " // ";
        for (XMLWord w : getSentence().getWords()) {
            result += w.getText() + " ";
        }
        result += "[" + getSentence().getFilePath() + "]";*/
        return result;
    }

    private Set<Word> words;
    private Word head;
    private ChunkType type;
    private Sentence sentence;
}
