package ru.spbau.textminer.extraction;

import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.Word;

public class Relation {
    public Relation(String arg1, String rel, String arg2, Sentence sentence) {
        this.arg1 = arg1;
        this.rel = rel;
        this.arg2 = arg2;
        this.sentence = sentence;
    }

    public String getArg1() {
        return arg1;
    }

    public String getRel() {
        return rel;
    }

    public String getArg2() {
        return arg2;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public String toString() {
        String result = getArg1() + " *" + getRel() + "* " + getArg2() + " // ";
        for (Word word : getSentence().getWords()) {
            result += word.getText() + " ";
        }
        result += "[" + getSentence().getFilePath() + "]";
        return result;
    }

    private String arg1;
    private String rel;
    private String arg2;
    private Sentence sentence;
}
