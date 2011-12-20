package ru.spbau.textminer.text;

import org.w3c.dom.Element;
import ru.spbau.textminer.text.feature.Features;
import ru.spbau.textminer.text.feature.Link;

import java.util.*;

public class Word {
    public Word(String lemma, String text, Link link, Features features, int number) {
        this.lemma = lemma;
        this.text = text;
        this.link = link;
        this.features = features;
        this.number = number;
        children = new ArrayList<LinkWordPair>();
    }

    public List<Word> findByLink(Link... linkArr) {
        List<Link> linkList = Arrays.asList(linkArr);
        List<Word> result = new ArrayList<Word>();
        for (LinkWordPair pair : children) {
            if (linkList.contains(pair.getLink())) {
                result.add(pair.getWord());
            }
        }
        return result;
    }

    public boolean hasLink(Link... linkArr) {
        List<Link> linkList = Arrays.asList(linkArr);
        for (LinkWordPair pair : children) {
            if (linkList.contains(pair.getLink())) {
                return true;
            }
        }
        return false;
    }

    public void addChild(Link link, Word child) {
        children.add(new LinkWordPair(link, child));
    }

    public List<LinkWordPair> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void setParent(Word parent) {
        this.parent = parent;
    }

    public Word getParent() {
        return parent;
    }

    public String getLemma() {
        return lemma;
    }

    public String getText() {
        if (text.length() != 0) {
            return text;
        } else {
            return "[" + lemma + "]";
        }
    }

    public Link getLink() {
        return link;
    }

    public Features getFeatures() {
        return features;
    }

    public int getNumber() {
        return number;
    }

    private String lemma;
    private String text;
    private Link link;
    private Features features;
    private int number;

    private Word parent;
    private List<LinkWordPair> children;
}
