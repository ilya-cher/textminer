package ru.spbau.textminer.text;

import ru.spbau.textminer.text.feature.Link;

public class LinkWordPair {
    public LinkWordPair(Link link, Word word) {
        this.link = link;
        this.word = word;
    }

    public Link getLink() {
        return link;
    }

    public Word getWord() {
        return word;
    }

    private Link link;
    private Word word;
}
