package ru.spbau.textminer.extraction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ru.spbau.textminer.text.Word;
import ru.spbau.textminer.text.feature.Features;
import ru.spbau.textminer.text.feature.Link;
import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.feature.POS;
import ru.spbau.textminer.text.feature.VerbForm;

import java.util.*;

public class ChunkExtractor {
    public Multimap<Sentence, Chunk> extract(List<Sentence> sentences) {
        Multimap<Sentence, Chunk> result = HashMultimap.create(sentences.size(), MAX_CHUNKS_EXPECTED);
        for (Sentence sentence : sentences) {
            List<Chunk> vpChunks = extractVPChunks(sentence);
            List<Chunk> npChunks = extractNPChunks(sentence, getChunkWords(vpChunks));
            if (vpChunks.size() > 0) {
                result.putAll(sentence, vpChunks);
            }
            if (npChunks.size() > 0) {
                result.putAll(sentence, npChunks);
            }
        }

        return result;
    }

    private Set<Word> getChunkWords(List<Chunk> chunks) {
        Set<Word> result = new HashSet<Word>();
        for (Chunk chunk : chunks) {
            result.addAll(chunk.getWords());
        }
        return result;
    }

    List<Chunk> extractVPChunks(Sentence sentence) {
        List<Chunk> chunks = new ArrayList<Chunk>();
        Set<Word> takenWords = new HashSet<Word>();

        for (Word head : sentence.getWords()) {
            if (!takenWords.contains(head)) {
                if (isValidVPHead(head)) {
                    Set<Word> chunkWords = ExtrUtil.closureWithConstraint(head,
                            new ExtrUtil.Constraint() {
                                public boolean checkConstraint(Word word) {
                                    if (ExtrUtil.isAdvMod(word)) {
                                        Features feats = word.getFeatures();
                                        return (feats.getPOS() != POS.PR && feats.getPOS() != POS.V);
                                    } else if (ExtrUtil.isCompl(word)) {
                                        return /*(feats.getPOS() == POS.S)||*/ ExtrUtil.isVerbInf(word);
                                    } else if (ExtrUtil.isPred(word)) {
                                        return ExtrUtil.isVerbInf(word);
                                    }
                                    return true;
                                }
                            },
                            // VP chunk valid links:
                            Link.AGENT, Link.Q_AGENT, Link.COMPL, Link.N_COMPL,
                            Link.NA_COMPL, Link.PAN, Link.LINK/*, Link.ATTR*//*, Link.S,
                            Link.S_CONJ*/, Link.CONSTR, Link.ADVMOD, Link.TIME,
                            Link.REP_TIME, Link.DIST, Link.SUBJ_ADVMOD, Link.OBJ_ADVMOD,
                            Link.AN, Link.PRED
                    );

                    takenWords.addAll(chunkWords);
                    chunks.add(new Chunk(chunkWords, head, ChunkType.VP, sentence));
                }
            }
        }
        return removeSubChunks(chunks);
    }

    private boolean isValidVPHead(Word head) {
        Features feats = head.getFeatures();
        return (feats.getPOS() == POS.V && (feats.getVerbForm() == VerbForm.INF || ExtrUtil.isPred(head))) ||
                        (feats.getPOS() == POS.A && ExtrUtil.isPred(head));
    }

    List<Chunk> extractNPChunks(Sentence sentence, Set<Word> stopWords) {
        List<Chunk> chunks = new ArrayList<Chunk>();
        Set<Word> takenWords = new HashSet<Word>();
        takenWords.addAll(stopWords);

        for (Word head : sentence.getWords()) {
            if (!takenWords.contains(head)) {
                if (isValidNPHead(head)) {
                    Set<Word> chunkWords = ExtrUtil.closure(head, stopWords,
                            Link.COMPL,     Link.N_COMPL,   Link.NA_COMPL,  Link.LINK,
                            Link.AGENT,     Link.Q_AGENT,   Link.PREP,      Link.CONSTR,
                            Link.ATTR,      Link.NUM,       Link.DEF,       Link.DESCR_DEF,
                            Link.S,         Link.S_CONJ,    Link.APP,       Link.D_APP,
                            Link.NUM_APP,   Link.NOM_APP,   Link.PREP,      Link.APPROX_NUM,
                            Link.ADVMOD,    Link.ADJOIN,    Link.ELECT,     Link.REP,
                            Link.COMPL_APP
                    );
                    chunkWords.addAll(ExtrUtil.backwardClosure(head, stopWords, Link.PREP, Link.ELECT, Link.ATTR));

                    takenWords.addAll(chunkWords);
                    chunks.add(new Chunk(chunkWords, head, ChunkType.NP, sentence));
                }
            }
        }
        return removeSubChunks(chunks); //[[...] ... ]
    }

    private boolean isValidNPHead(Word head) {
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

        return POSCond && linkCond;
    }

    private List<Chunk> removeSubChunks(List<Chunk> chunks) {
        List<Chunk> result = new ArrayList<Chunk>();
        for (int i = 0; i < chunks.size(); i++) {
            boolean isSubChunk = false;
            Chunk testChunk = chunks.get(i);
            for (int j = i + 1; j < chunks.size(); j++) {
                Chunk chunk = chunks.get(j);
                if (chunk.getWords().containsAll(testChunk.getWords())) {
                    isSubChunk = true;
                    break;
                }
            }
            if (!isSubChunk) {
                result.add(testChunk);
            }
        }
        return result;
    }

    private static final int MAX_CHUNKS_EXPECTED = 32; // per sentence
}
