package ru.spbau.textminer.extraction;

import ru.spbau.textminer.text.Word;
import ru.spbau.textminer.text.feature.Features;
import ru.spbau.textminer.text.feature.Link;
import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.feature.POS;
import java.util.*;

public class RelationExtractor {
    public List<Relation> extract(List<Sentence> sentences) {
        List<Relation> result = new ArrayList<Relation>();

        for (Sentence sent : sentences) {
            List<Word> words = sent.getWords();
            for (Word word : words) {
                Features feats = word.getFeatures();
                if (feats.getPOS() == POS.S &&
                        (word.getLink() == Link.PRED || word.getLink() == Link.DAT_SUBJ)) {

                    List<Word> subs = ExtrUtil.getConj(word);
                    subs = ExtrUtil.getAllEqualsPOS(subs, POS.S, POS.V,  POS.NID);
                    Word pred = word.getParent();
                    List<Word> preds = ExtrUtil.getConj(pred);

                    for (Word sub : subs) {
                        for (Word rel : preds) {
                            result.addAll(extractFromSentence(sub, rel, sent));
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<Relation> extractFromSentence(Word word, Word pred, Sentence sent) {
        List<Relation> result = new ArrayList<Relation>();
        List<Word> args = new ArrayList<Word>();

        args.addAll(pred.findByLink(Link.COMPL));
        args.addAll(pred.findByLink(Link.NA_COMPL));
        args.addAll(pred.findByLink(Link.LINK));

        for (Word arg : args) {
            POS argPOS = arg.getFeatures().getPOS();
            if (argPOS == POS.PR) {
                List<Word> nodes = arg.findByLink(Link.PREP);
                if (nodes.size() == 0) {
                    continue;
                }
                Word node = nodes.get(0);
                if (node.getFeatures().getPOS() == POS.S) {
                    String rel = ExtrUtil.printInOrder(ExtrUtil.makeRel(pred));
                    rel += " " + arg.getText();
                    result.add(new Relation(ExtrUtil.printInOrder(ExtrUtil.makeArg(word)), rel,
                            ExtrUtil.printInOrder(ExtrUtil.makeArg(node)), sent));
                }
            }
        }

        args = pred.findByLink(Link.COMP);
        for (Word arg : args) {
            if (arg.getFeatures().getPOS() == POS.S) {
                result.add(new Relation(ExtrUtil.printInOrder(ExtrUtil.makeArg(word)),
                        ExtrUtil.printInOrder(ExtrUtil.makeRel(pred)),
                        ExtrUtil.printInOrder(ExtrUtil.makeArg(arg)), sent));
            }
        }

        return result;
    }
}
