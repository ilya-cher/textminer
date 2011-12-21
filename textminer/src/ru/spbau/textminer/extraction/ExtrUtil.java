package ru.spbau.textminer.extraction;

import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.Word;
import ru.spbau.textminer.text.feature.Features;
import ru.spbau.textminer.text.feature.Link;
import ru.spbau.textminer.text.feature.POS;
import ru.spbau.textminer.text.feature.VerbForm;

import java.util.*;

public class ExtrUtil {
    private ExtrUtil() {}

    public static List<Word> getConj(Word word) {
        List<Word> result = new ArrayList<Word>();
        result.add(word);

        while (word != null) {
            List<Word> conj = word.findByLink(Link.S);
            conj.addAll(word.findByLink(Link.S_CONJ));

            word = null;
            if (conj.size() > 0) {
                word = conj.get(0);
                result.add(word);
            }
        }

        return result;
    }

    public static List<Word> getAllEqualsPOS(List<Word> words, POS... pos) {
        List<POS> tags = Arrays.asList(pos);
        List<Word> result = new ArrayList<Word>();
        for (Word word : words) {
            if (tags.contains(word.getFeatures().getPOS())) {
                result.add(word);
            }
        }
        return result;
    }

    public static List<Word> getAllButPOS(List<Word> words, POS... pos) {
        List<POS> tags = Arrays.asList(pos);
        List<Word> result = new ArrayList<Word>();
        for (Word word : words) {
            if (!tags.contains(word.getFeatures().getPOS())) {
                result.add(word);
            }
        }
        return result;
    }

    public static Word getFirstEqualsLemma(List<Word> words, String lemma) {
        for (Word word : words) {
            if (word.getLemma().equals(lemma)) {
                return word;
            }
        }
        return null;
    }

    public static Word getFirstEqualsPOS(List<Word> words, POS pos) {
        for (Word word : words) {
            if (word.getFeatures().getPOS() == pos) {
                return word;
            }
        }
        return null;
    }

    public static Set<Word> addConstr(Word word) {
        Set<Word> result = new HashSet<Word>();
        result.addAll(word.findByLink(Link.CONSTR));
        return result;
    }

    public static Set<Word> addAttrs(Word word) {
        Set<Word> result = new HashSet<Word>();
        List<Word> attrs = word.findByLink(Link.ATTR);
        for (Word attr: attrs) {
            switch (attr.getFeatures().getPOS()) {
                case PR: {
                    Word noun = getFirstEqualsPOS(attr.findByLink(Link.PREP), POS.S);
                    if (noun != null) {
                        result.add(attr);
                        result.add(noun);
                        result.addAll(addAttrs(noun));
                    }
                    break;
                }
                case S: {
                    result.add(attr);
                    result.addAll(addAttrs(attr));
                    break;
                }
            }
        }
        attrs = word.findByLink(Link.DEF);
        attrs.addAll(word.findByLink(Link.DESCR_DEF));
        attrs.addAll(word.findByLink(Link.NUM));
        for (Word attr : attrs) {
            result.add(attr);
            result.addAll(addConstr(attr));
        }
        return result;
    }

    public static Set<Word> addCompl(Word word) {
        Set<Word> result = new HashSet<Word>();
        List<Word> compl = word.findByLink(Link.COMPL);

        for (Word cmpl : compl) {
            POS pos = cmpl.getFeatures().getPOS();
            if (pos == POS.S) {
                result.add(cmpl);
                result.addAll(addAgents(cmpl));
                result.addAll(addAttrs(cmpl));
            } else if (pos == POS.PR) {
                Word noun = getFirstEqualsPOS(cmpl.findByLink(Link.PREP), POS.S);
                if (noun != null) {
                    result.add(cmpl);
                    result.add(noun);
                    result.addAll(addAttrs(noun));
                }
            }
        }

        return result;
    }

    public static Set<Word> addAgents(Word word) {
        Set<Word> result = new HashSet<Word>();
        List<Word> agents = word.findByLink(Link.AGENT);
        agents.addAll(word.findByLink(Link.Q_AGENT));
        for (Word agent : agents) {
            result.add(agent);
            result.addAll(addAttrs(agent));
            result.addAll(addAgents(agent));
        }
        return result;
    }

    public static Word getNot(Word word) {
        List<Word> words = word.findByLink(Link.CONSTR);
        return getFirstEqualsLemma(words, "НЕ");
    }

    public static String printInOrder(Sentence sent) {
        return printInOrder(sent.getWords());
    }
    public static String printInOrder(Collection<Word> words) {
        List<Word> wordList = sortWords(words);
        String text = "";
        for (Word word : wordList) {
            if (text.length() > 0) {
                text += " ";
            }
            text = text + word.getText();
        }
        return text;
    }

    public static List<Word> sortWords(Collection<Word> words) {
        List<Word> wordList = new ArrayList<Word>(words);
        Collections.sort(wordList, new Comparator<Word>() {
            public int compare(Word word1, Word word2) {
                return word1.getNumber() - word2.getNumber();
            }
        });
        return wordList;
    }

    public static Set<Word> makeRel(Word pred) {
        Set<Word> result = new HashSet<Word>();
        result.add(pred);
        if (getNot(pred) != null) {
            result.add(getNot(pred));
        }
        result.addAll(addAgents(pred));
        return result;
    }

    public static Set<Word> makeArg(Word arg) {
        Set<Word> result = new HashSet<Word>();
        result.add(arg);
        if (getNot(arg) != null) {
            result.add(getNot(arg));
        }
        result.addAll(addAttrs(arg));
        result.addAll(addAgents(arg));
        result.addAll(addCompl(arg));
        return result;
    }

    public static Set<Word> closure(Word word, Link... links) {
        return closureWithConstraint(word, null, links);
    }

    public static Set<Word> closure(Set<Word> words, Link... links) {
        return closureWithConstraint(words, null, links);
    }

    public static Set<Word> closure(Word word, final Set<Word> stopWords, Link... links) {
        Set<Word> words = new HashSet<Word>();
        words.add(word);
        return closure(words, stopWords, links);
    }

    public static Set<Word> closure(Set<Word> words, final Set<Word> stopWords, Link... links) {
        return closureWithConstraint(words, new Constraint() {
            public boolean checkConstraint(Word word) {
                return !stopWords.contains(word);
            }
        },links);
    }

    public static Set<Word> closureWithConstraint(Word startWord, Constraint constraint, Link... links) {
        Set<Word> startWords = new HashSet<Word>();
        startWords.add(startWord);
        return closureWithConstraint(startWords, constraint, links);
    }

    public static Set<Word> closureWithConstraint(Set<Word> startWords, Constraint constraint, Link... links) {
        Set<Word> result = new HashSet<Word>();
        for (Word startWord : startWords) {
            if ((constraint == null) || constraint.checkConstraint(startWord)) {
                result.add(startWord);
            }
        }
        int prevSize;
        do {
            Set<Word> nextWords = new HashSet<Word>();
            for (Word word : result) {
                nextWords.add(word);
                List<Word> newWords = word.findByLink(links);
                for (Word newWord : newWords) {
                    if ((constraint == null) || constraint.checkConstraint(newWord)) {
                        nextWords.add(newWord);
                    }
                }
            }
            prevSize = result.size();
            result = nextWords;
        } while (result.size() != prevSize);

        return result;
    }

    public interface Constraint {
        boolean checkConstraint(Word word);
    }

    public static Set<Word> backwardClosure(Word word, Link... linksArr) {
        return backwardClosure(word, null, linksArr);
    }
    public static Set<Word> backwardClosure(Word word, Set<Word> stopWords, Link... linksArr) {
        List<Link> links = Arrays.asList(linksArr);
        Set<Word> result = new HashSet<Word>();
        if ((stopWords == null) || !stopWords.contains(word)) {
            result.add(word);
        }
        while (word != null && ((stopWords == null) || !stopWords.contains(word)) &&
                word.getLink() != null && links.contains(word.getLink())) {
            word = word.getParent();
            if (word != null && ((stopWords == null) || (!stopWords.contains(word)))) {
                result.add(word);
            }
        }
        return result;
    }

    public static boolean isVerbInf(Word word) {
        Features feats = word.getFeatures();
        return (feats.getPOS() == POS.V && feats.getVerbForm() == VerbForm.INF);
    }

    public static boolean isAdvMod(Word word) {
        Link link = word.getLink();
        return link == Link.ADVMOD || link == Link.TIME ||
                link == Link.REP_TIME || link == Link.DIST ||
                link == Link.SUBJ_ADVMOD || link == Link.OBJ_ADVMOD;
    }

    public static boolean isCompl(Word word) {
        Link link = word.getLink();
        return link == Link.COMPL || link == Link.N_COMPL || link == Link.NA_COMPL;
    }

    public static boolean isPred(Word word) {
        return word.getLink() == Link.PRED;
    }
}
