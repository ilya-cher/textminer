package ru.spbau.textminer.extraction;

import ru.spbau.textminer.text.Word;
import ru.spbau.textminer.text.feature.Features;
import ru.spbau.textminer.text.feature.Link;
import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.feature.POS;
import ru.spbau.textminer.text.feature.VerbForm;

import java.util.*;

public class ChunkExtractor {
    public Map<Sentence, List<Chunk>> extract(List<Sentence> sentences) {
        Map<Sentence, List<Chunk>> result = new HashMap<Sentence, List<Chunk>>();
        for (Sentence sentence : sentences) {
            List<Chunk> vpChunks = extractVPChunks(sentence);
            Set<Word> stopWords = new HashSet<Word>();
            for (Chunk vpChunk : vpChunks) {
                stopWords.addAll(vpChunk.getWords());
            }
            List<Chunk> npChunks = extractNPChunks(sentence, stopWords);
            List<Chunk> chunks = new ArrayList<Chunk>();
            chunks.addAll(npChunks);
            chunks.addAll(vpChunks);
            result.put(sentence, chunks);
        }

        return result;
    }

    List<Chunk> extractVPChunks(Sentence sent) {
        List<Chunk> extrs = new ArrayList<Chunk>();
        List<Word> words = sent.getWords();
        Set<Word> phraseWords = new HashSet<Word>();

        for (Word head : words) {
            if (!phraseWords.contains(head)) {
                Features feats = head.getFeatures();
                if ((feats.getPOS() == POS.V && (feats.getVerbForm() == VerbForm.INF || head.findByLink(Link.PRED).size() > 0)) ||
                        (feats.getPOS() == POS.A && head.findByLink(Link.PRED).size() > 0)) {

                    Set<Word> phrase = ExtrUtil.closureWithConstraint(head,
                            new ExtrUtil.Constraint() {
                                public boolean checkConstraint(Word word) {
                                    Link link = word.getLink();
                                    Features feats = word.getFeatures();
                                    boolean linkAdvMod = (
                                            link == Link.ADVMOD || link == Link.TIME ||
                                            link == Link.REP_TIME || link == Link.DIST ||
                                            link == Link.SUBJ_ADVMOD || link == Link.OBJ_ADVMOD
                                    );
                                    if (linkAdvMod) {
                                        return (feats.getPOS() != POS.PR && feats.getPOS() != POS.V);
                                    } else if (link == Link.COMPL || link == Link.N_COMPL || link == Link.NA_COMPL) {
                                        return /*(feats.getPOS() == POS.S)||*/ (feats.getPOS() == POS.V) && (feats.getVerbForm() == VerbForm.INF);
                                    } else if (link == Link.PRED) {
                                        return (feats.getPOS() == POS.V) && (feats.getVerbForm() == VerbForm.INF);
                                    }
                                    return true;
                                }
                            },
                            Link.AGENT, Link.Q_AGENT, Link.COMPL, Link.N_COMPL,
                            Link.NA_COMPL, Link.PAN, Link.LINK/*, Link.ATTR*//*, Link.S,
                            Link.S_CONJ*/, Link.CONSTR, Link.ADVMOD, Link.TIME,
                            Link.REP_TIME, Link.DIST, Link.SUBJ_ADVMOD, Link.OBJ_ADVMOD,
                            Link.AN, Link.PRED
                    );

                    phraseWords.addAll(phrase);
                    extrs.add(new Chunk(phrase, head, ChunkType.VP, sent));
                }
            }
        }
        extrs = removeSubPhrases(extrs);
        return extrs;
    }

    List<Chunk> extractNPChunks(Sentence sent, Set<Word> stopWords) {
        List<Chunk> extrs = new ArrayList<Chunk>();
        List<Word> words = sent.getWords();
        Set<Word> phraseWords = new HashSet<Word>();
        phraseWords.addAll(stopWords);

        for (Word head : words) {
            if (!phraseWords.contains(head)) {
                Features feats = head.getFeatures();
                Link link = head.getLink();

                boolean POSCond = (feats.getPOS() == POS.S || feats.getPOS() == POS.NID);
                /*boolean featsCond = ((feats.getCase() == Case.NOM) || (feats.getCase() == Case.ACC));*/
                boolean linkCond = (
                    link == Link.PRED  || link == Link.AGENT    || link == Link.Q_AGENT ||
                    link == Link.COMPL || link == Link.NA_COMPL || link == Link.N_COMPL ||
                    link == Link.PREP  || link == Link.ADVMOD   || link == Link.DAT_SUBJ ||
                    link == null
                );

                if (POSCond && linkCond) {
                    Set<Word> phrase = ExtrUtil.closure(head, stopWords,
                            Link.COMPL,     Link.N_COMPL,   Link.NA_COMPL,  Link.LINK,
                            Link.AGENT,     Link.Q_AGENT,   Link.PREP,      Link.CONSTR,
                            Link.ATTR,      Link.NUM,       Link.DEF,       Link.DESCR_DEF,
                            Link.S,         Link.S_CONJ,    Link.APP,       Link.D_APP,
                            Link.NUM_APP,   Link.NOM_APP,   Link.PREP,      Link.APPROX_NUM,
                            Link.ADVMOD,    Link.ADJOIN,    Link.ELECT,     Link.REP,
                            Link.COMPL_APP
                    );
                    phrase.addAll(ExtrUtil.backwardClosure(head, stopWords, Link.PREP, Link.ELECT, Link.ATTR));

                    phraseWords.addAll(phrase);
                    extrs.add(new Chunk(phrase, head, ChunkType.NP, sent));
                }
            }
        }
        extrs = removeSubPhrases(extrs); //[[...] ... ]
        return extrs;
    }

    private List<Chunk> removeSubPhrases(List<Chunk> extrs) {
        List<Chunk> result = new ArrayList<Chunk>();
        for (int i = 0; i < extrs.size(); i++) {
            boolean sub = false;
            Chunk testPhrase = extrs.get(i);
            for (int j = i + 1; j < extrs.size(); j++) {
                Chunk phrase = extrs.get(j);
                if (phrase.getWords().containsAll(testPhrase.getWords())) {
                    sub = true;
                    break;
                }
            }
            if (!sub) {
                result.add(testPhrase);
            }
        }
        return result;
    }
}
