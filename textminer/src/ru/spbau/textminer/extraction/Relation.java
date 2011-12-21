package ru.spbau.textminer.extraction;

import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.Word;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Relation {
    public Relation(Chunk leftChunk, Chunk rightChunk, Collection<Word> arg1, Collection<Word> rel, Collection<Word> arg2, Sentence sentence, Set<Word> path) {
        this.leftChunk = leftChunk;
        this.rightChunk = rightChunk;
        this.arg1 = arg1;
        this.rel = rel;
        this.arg2 = arg2;
        this.sentence = sentence;
        this.path = path;
    }

    public Collection<Word> getArg1() {
        return arg1;
    }

    public Collection<Word> getRel() {
        return rel;
    }

    public Collection<Word> getArg2() {
        return arg2;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public Chunk getLeftChunk() {
        return leftChunk;
    }

    public Chunk getRightChunk() {
        return rightChunk;
    }

    public String toString() {
        String result = ExtrUtil.printInOrder(getArg1()) +
                " *" + ExtrUtil.printInOrder(getRel()) + "* " +
                ExtrUtil.printInOrder(getArg2()) + " // ";
        /*for (Word word : getSentence().getWords()) {
            result += word.getText() + " ";
        }*/
        /*for (Word word : path) {
            result += word.getText() + " ";
        }*/
        result += "// " + ExtrUtil.printInOrder(path);

        return result;
    }

    private Collection<Word> arg1;
    private Collection<Word> rel;
    private Collection<Word> arg2;
    private Sentence sentence;
    private Set<Word> path;
    private Chunk leftChunk;
    private Chunk rightChunk;
}
