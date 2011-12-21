package ru.spbau.textminer.extraction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ru.spbau.textminer.text.LinkWordPair;
import ru.spbau.textminer.text.Word;
import ru.spbau.textminer.text.feature.Case;
import ru.spbau.textminer.text.feature.Features;
import ru.spbau.textminer.text.feature.Link;
import ru.spbau.textminer.text.Sentence;
import ru.spbau.textminer.text.feature.POS;

import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.*;

public class RelationExtractor {
    public RelationExtractor() {
        chunkExtractor = new ChunkExtractor();
    }

    public Multimap<Sentence, Relation> extract(List<Sentence> sentences) {
        Multimap<Sentence, Relation> relations = HashMultimap.create();
        Multimap<Sentence, Chunk> chunksMap = chunkExtractor.extract(sentences);
        for (Sentence sentence : chunksMap.keySet()) {
            relations.putAll(sentence, extractRelations(sentence, chunksMap.get(sentence)));
        }

        return relations;
    }

    private List<Relation> extractRelations(Sentence sentence, Collection<Chunk> sentChunks) {
        List<Relation> relationList = new ArrayList<Relation>();
        if (!checkChunks(sentence, sentChunks)) {
            return relationList;
        }
        List<Chunk> chunks = new ArrayList<Chunk>(sentChunks);

        List<Chunk> npChunks = new ArrayList<Chunk>();
        List<Chunk> vpChunks = new ArrayList<Chunk>();
        for (Chunk chunk : chunks) {
            if (chunk.getType() == ChunkType.NP) {
                npChunks.add(chunk);
            }
            if (chunk.getType() == ChunkType.VP) {
                vpChunks.add(chunk);
            }
        }

        for (Chunk vpChunk : vpChunks) {
            if (isValidVPChunk(vpChunk)) {
                List<Chunk> leftChunks = new ArrayList<Chunk>();
                List<Chunk> rightChunks = new ArrayList<Chunk>();
                for (Chunk npChunk : npChunks) {
                    if (npChunk.getHead().getNumber() < vpChunk.getHead().getNumber()) {
                        leftChunks.add(npChunk);
                    } else {
                        rightChunks.add(npChunk);
                    }
                }

                List<Chunk> subjChunks = chunkClosure(vpChunk, leftChunks, Link.PRED, Link.DAT_SUBJ);
                Chunk firstArg = shortestPathChunk(subjChunks, vpChunk);

                //List<Chunk> otherChunks = new ArrayList<Chunk>(npChunks);
                //otherChunks.removeAll(subjChunks);

                List<Chunk> objChunks = chunkClosure(vpChunk, rightChunks,
                        Link.ADVMOD, Link.TIME, Link.REP_TIME, Link.DIST, Link.SUBJ_ADVMOD,
                        Link.OBJ_ADVMOD, Link.COMPL, Link.N_COMPL, Link.NA_COMPL,
                        Link.PREP, Link.COMP,  Link.COMP_CONJ, Link.ELECT, Link.S, Link.S_CONJ, Link.SENT_S);
                Chunk secondArg = shortestPathChunk(objChunks, vpChunk);

                if (firstArg != null && secondArg != null && isValidRelation(firstArg, vpChunk, secondArg)) {
                    Set<Word> leftWords = new HashSet<Word>(firstArg.getWords());
                    Set<Word> relWords = new HashSet<Word>();
                    Set<Word> rightWords = new HashSet<Word>(secondArg.getWords());

                    List<Word> leftPath = getPath(firstArg.getHead(), vpChunk.getHead());
                    leftPath.remove(firstArg.getHead());
                    List<Word> rightPath = getPath(secondArg.getHead(), vpChunk.getHead());
                    rightPath.remove(secondArg.getHead());
                    relWords.addAll(leftPath);
                    relWords.addAll(rightPath);
                    relWords.addAll(vpChunk.getWords());

                    leftWords.removeAll(relWords);
                    rightWords.removeAll(relWords);

                    if (leftWords.size() > 0 && relWords.size() > 1 && rightWords.size() > 0) {
                        relationList.add(new Relation(firstArg, secondArg, leftWords, relWords, rightWords, sentence, secondArg.getWords()));
                    }
                }
            }
        }
        return relationList;
    }

    private List<Chunk> chunkClosure(Chunk chunk, List<Chunk> chunks, Link... linkArr) {
        return chunkClosure(chunk, chunks, null, linkArr);
    }

    private List<Chunk> chunkClosure(Chunk chunk, List<Chunk> chunks, ExtrUtil.Constraint constraint, Link... linkArr) {
        List<Chunk> resChunks = new ArrayList<Chunk>();
        Set<Word> headsList = ExtrUtil.closureWithConstraint(chunk.getHead(), constraint, linkArr);
        for (Chunk testChunk : chunks) {
            if (headsList.contains(testChunk.getHead())) {
                resChunks.add(testChunk);
            }
        }
        return resChunks;
    }

    /*
    private List<Word> combinePaths(List<Word> firstPath, List<Word> secondPath) {
        List<Word> path = new ArrayList<Word>();
        for (int i = 0; i < firstPath.size(); i++) {
            path.add(firstPath.get(i));
        }
        for (int i = secondPath.size() - 2; i >= 0; i--) {
            path.add(secondPath.get(i));
        }

        return path;
    }
    */
    private List<Chunk> removeBadChunks(Sentence sentence, Collection<Chunk> chunks) {
        List<Chunk> resChunks = new ArrayList<Chunk>();
        for (Chunk chunk : chunks) {
            int max = getMaxNumber(chunk);
            int min = getMinNumber(chunk);
            if (chunk.getWords().size() == (max - min + 1)) {
                resChunks.add(chunk);
            }
        }
        return resChunks;
    }

    private Chunk shortestPathChunk(List<Chunk> fromChunks, Chunk toChunk) {
        Chunk resChunk = null;
        Integer minLength = null;
        Integer minDistance = null;

        for (Chunk fromChunk : fromChunks) {
            List<Word> path = getPath(fromChunk.getHead(), toChunk.getHead());
            if (path != null) {
                int length = path.size();
                int distance = Math.abs(fromChunk.getHead().getNumber() - toChunk.getHead().getNumber());
                if (minLength != null) {
                    if ((minLength > length) ||
                            ((minLength == length) && (minDistance > distance))) {
                        minLength = length;
                        minDistance = distance;
                        resChunk = fromChunk;
                    }
                } else {
                    minLength = length;
                    minDistance = distance;
                    resChunk = fromChunk;
                }
            }
        }

        return resChunk;
    }

    private List<Word> getPath(Word childWord, Word parentWord) {
        List<Word> words = new ArrayList<Word>();
        Word word = childWord;
        while (word != null && word != parentWord) {
            words.add(word);
            word = word.getParent();
        }
        if (word == null) {
            return null;
        } else {
            words.add(parentWord);
            return words;
        }
    }

    private boolean checkChunks(Sentence sentence, Collection<Chunk> chunks) {
        Map<Word, Chunk> chunkMap = new HashMap<Word, Chunk>();
        Set<Chunk> visitedChunks = new HashSet<Chunk>();
        for (Chunk chunk : chunks) {
            for (Word word : chunk.getWords()) {
                chunkMap.put(word, chunk);
            }
        }
        Chunk prevChunk = null;
        for (Word word : sentence.getWords()) {
            Chunk chunk = chunkMap.get(word);
            if (chunk != null) {
                if (visitedChunks.contains(chunk)) {
                    return false;
                }
                if (prevChunk != null && prevChunk != chunk) {
                    visitedChunks.add(prevChunk);
                }
                prevChunk = chunk;
            }
        }
        return true;
    }

    private List<Chunk> linearize(Sentence sentence, Collection<Chunk> chunks) {
        List<Chunk> result = new ArrayList<Chunk>();
        Map<Word, ChunkType> chunkMap = new HashMap<Word, ChunkType>();
        Set<Word> headSet = new HashSet<Word>();
        for (Chunk chunk : chunks) {
            for (Word word : chunk.getWords()) {
                chunkMap.put(word, chunk.getType());
            }
            headSet.add(chunk.getHead());
        }

        int i = 0;
        List<Word> words = sentence.getWords();
        while (i < words.size()) {
            while ((i < words.size()) && (chunkMap.get(words.get(i)) == null)) {
                i++;
            }
            if (i < words.size()) {
                Set<Word> chunkWords = new HashSet<Word>();
                Word head = null;
                ChunkType type = chunkMap.get(words.get(i));
                while ((i < words.size()) && (chunkMap.get(words.get(i)) == type)) {
                    chunkWords.add(words.get(i));
                    if (headSet.contains(words.get(i))) {
                        head = words.get(i);
                    }
                    i++;
                }
                if (head != null) {
                    result.add(new Chunk(chunkWords, head, type, sentence));
                }
            }
        }
        return result;
    }

    private boolean isValidRelation(Chunk leftChunk, Chunk vpChunk, Chunk rightChunk) {
        if (!isFirstArgumentValid(leftChunk) || !isSecondArgumentValid(rightChunk)) {
            return false;
        }
        int maxNumber = getMaxNumber(leftChunk);
        int minNumber = getMinNumber(rightChunk);
        return (Math.abs(minNumber - maxNumber) <= MAX_DISTANCE);
    }

    private int getMaxNumber(Chunk chunk) {
        int maxNumber = -1;
        for (Word word : chunk.getWords()) {
            if (maxNumber < word.getNumber()) {
                maxNumber = word.getNumber();
            }
        }
        return maxNumber;
    }

    private int getMinNumber(Chunk chunk) {
        Integer minNumber = null;
        for (Word word : chunk.getWords()) {
            if (minNumber == null) {
                minNumber = word.getNumber();
            } else if (minNumber > word.getNumber()) {
                minNumber = word.getNumber();
            }
        }
        return minNumber;
    }

    private boolean isFirstArgumentValid(Chunk chunk) {
        Features headFeats = chunk.getHead().getFeatures();
        return (chunk.getType() == ChunkType.NP) &&
                (chunk.getWords().size() <= MAX_WORDS_IN_NP_CHUNK) &&
                (headFeats.getPOS() == POS.S || headFeats.getPOS() == POS.NID) &&
                (headFeats.getCase() == Case.NOM || headFeats.getCase() == Case.ACC);
    }

    private boolean isSecondArgumentValid(Chunk chunk) {
        return (chunk.getType() == ChunkType.NP) &&
                (chunk.getWords().size() <= MAX_WORDS_IN_NP_CHUNK);
    }

    private boolean isValidVPChunk(Chunk vpChunk) {
        return (vpChunk.getType() == ChunkType.VP) &&
                (vpChunk.getWords().size() <= MAX_WORDS_IN_VP_CHUNK);/* &&
                checkPOS(vpChunk.getWords(), POS.V);                  */
    }

    private boolean checkPOS(Set<Word> words, POS pos) {
        for (Word word : words) {
            if (word.getFeatures().getPOS() == pos) {
                return true;
            }
        }
        return false;
    }

    private ChunkExtractor chunkExtractor;
    private static final int MAX_WORDS_IN_VP_CHUNK = 10;
    private static final int MAX_WORDS_IN_NP_CHUNK = 10;
    private static final int MAX_DISTANCE = 15;
}
