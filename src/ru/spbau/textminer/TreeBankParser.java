package ru.spbau.textminer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.spbau.textminer.text.*;
import ru.spbau.textminer.text.feature.Features;
import ru.spbau.textminer.text.feature.Link;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TreeBankParser implements FilesParser {
    public TreeBankParser() {
        builderFactory = DocumentBuilderFactory.newInstance();
    }

	public List<Sentence> parseFiles(Collection<File> files) throws ParserException {
        List<Sentence> sentences = new ArrayList<Sentence>();
        for(File file : files) {
            sentences.addAll(parseFile(file));
        }
        return sentences;
    }
    private List<Sentence> parseFile(File xmlFile) throws ParserException {
        try {
            if (docBuilder == null) {
                docBuilder = builderFactory.newDocumentBuilder();
            }
            Document doc = docBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList sList = doc.getElementsByTagName(S_TAG);

            List<Sentence> sentences = new ArrayList<Sentence>();
            for (int i = 0; i < sList.getLength(); i++) {
                Element sElement = (Element) sList.item(i);
                try {
                    sentences.add(parseSentence(sElement, xmlFile.getAbsolutePath()));
                }
                catch (InvalidSentenceException e) {
                    logger.warn(e.getMessage());
                }
            }
            return sentences;
        } catch (SAXException e) {
            throw new ParserException("XML parser error", e);
        } catch (IOException e) {
            throw new ParserException("I/O error", e);
        } catch (ParserConfigurationException e) {
            throw new ParserException("Failed to create document builder", e);
        }
    }

    private Sentence parseSentence(Element sElement, String filePath) throws InvalidSentenceException {
        int sentenceId = readSentenceId(sElement, filePath);
        List<XMLWord> xmlWords = readWords(sElement, sentenceId, filePath);
        XMLWord root = findRoot(xmlWords, sentenceId, filePath);
        buildTree(root, xmlWords);

        List<Word> words = new ArrayList<Word>();
        for (XMLWord xmlWord : xmlWords) {
            words.add(xmlWord.getWord());
        }
        return new Sentence(root.getWord(), words);
    }

    private int readSentenceId(Element sElement, String filePath) throws InvalidSentenceException {
        try {
            return Integer.parseInt(sElement.getAttribute(ID_ATTR));
        } catch (NumberFormatException e) {
            throw new InvalidSentenceException("Invalid sentence ID: '" + sElement.getAttribute(ID_ATTR) +
                    "' (file path: " + filePath + ")", e);
        }
    }

    private List<XMLWord> readWords(Element sElement, int sentenceId, String filePath) throws InvalidSentenceException {
        NodeList wList = sElement.getElementsByTagName(W_TAG);
        if (wList.getLength() == 0) {
            throw new InvalidSentenceException("Sentence has no words (sentence ID: " + sentenceId + ", " + filePath + ")");
        }

        List<XMLWord> words = new ArrayList<XMLWord>();
        for (int i = 0; i < wList.getLength(); i++) {
            Element wElement = (Element) wList.item(i);
            try {
                XMLWord xmlWord = parseWord(wElement, i);
                words.add(xmlWord);
            } catch (InvalidWordException e) {
                throw new InvalidSentenceException("Sentence has invalid word '" + wElement.getTextContent() +
                        "': " + e.getMessage() + " (sentence ID: " + sentenceId + ", " + filePath + ")", e);
            }
        }
        return words;
    }

    public void buildTree(XMLWord root, List<XMLWord> words) throws InvalidSentenceException {
        buildTree(null, root, words, new HashSet<XMLWord>());
    }

    private void buildTree(XMLWord parent, XMLWord node, List<XMLWord> words, Set<XMLWord> visited) throws InvalidSentenceException {
        if (visited.contains(node)) {
            throw new InvalidSentenceException("Invalid tree structure");
        }

        visited.add(node);
        node.getWord().setParent((parent != null)?parent.getWord():null);
        for (XMLWord word : words) {
            if ((word != node) && (word.getParentId() != null) && (word.getParentId() == node.getId()) && !word.isRoot()) {
                node.getWord().addChild(word.getWord().getLink(), word.getWord());
                buildTree(node, word, words, visited);
            }
        }
    }

    private XMLWord parseWord(Element wElement, int wordNum) throws InvalidWordException {
        int wordId = readWordId(wElement);
        Integer parentId = readParentId(wElement, wordId);

        String lemma = readLemma(wElement, wordId);
        String text = wElement.getTextContent().trim().replace(' ', '_');
        Link link = WordUtil.getLink(wElement.getAttribute(LINK_ATTR));
        if ((link == null) && (parentId != null)) {
            throw new InvalidWordException("Invalid link: '" + wElement.getAttribute(LINK_ATTR) +
                    "' (word ID: " + wordId + ")");
        }
        Features features = readFeatures(wElement, wordId);
        if (features.getPOS() == null) {
            throw new InvalidWordException("No POS specified (word ID: " + wordId + ")");
        }

        Word word = new Word(lemma, text, link, features, wordNum);
        return new XMLWord(word, parentId, wordId);
    }

    private int readWordId(Element wElement) throws InvalidWordException {
        try {
            return Integer.parseInt(wElement.getAttribute(ID_ATTR));
        } catch (NumberFormatException e) {
            throw new InvalidWordException("Invalid word ID: '" + wElement.getAttribute(ID_ATTR) +
                    "' (word: '" + wElement.getTextContent() + "')");
        }
    }

    private Integer readParentId(Element wElement, int wordId) throws InvalidWordException {
        try {
            String domAttr = wElement.getAttribute(DOM_ATTR).trim();
            if (domAttr.equals(DOM_ROOT)) {
                return null;
            } else {
                return Integer.parseInt(domAttr);
            }
        } catch (NumberFormatException e) {
            throw new InvalidWordException("Invalid DOM attribute: '" + wElement.getAttribute(DOM_ATTR) +
                    "' (word ID: " + wordId + ")", e);
        }
    }

    private String readLemma(Element wElement, int wordId) throws InvalidWordException {
        String lemma = wElement.getAttribute(LEMMA_ATTR).trim().replace(' ', '_');
        if (lemma.length() == 0) {
            throw new InvalidWordException("No word's lemma specified (word ID: " + wordId + ")");
        }
        return lemma;
    }

    private Features readFeatures(Element wElement, int wordId) throws InvalidWordException {
        try {
            return new Features(wElement.getAttribute(FEAT_ATTR));
        } catch (InvalidFeatureException e) {
            throw new InvalidWordException("Invalid word's features: '" +
                    wElement.getAttribute(FEAT_ATTR) + "' (word ID: " + wordId + ")", e);
        }
    }

    private XMLWord findRoot(List<XMLWord> words, int sentenceId, String filePath) throws InvalidSentenceException {
        XMLWord root = null;
        for (XMLWord word : words) {
            if (word.isRoot()) {
                if (root == null) {
                    root = word;
                } else {
                    throw new InvalidSentenceException("Duplicate root word '" + word.getWord().getText() +
                            "' (sentence ID: " + sentenceId + ", " + filePath + ")");
                }
            }
        }
        if (root == null) {
            throw new InvalidSentenceException("No root word specified (sentence ID: " +
                    sentenceId + ", " + filePath + ")");
        }
        return root;
    }

    private final DocumentBuilderFactory builderFactory;
    private DocumentBuilder docBuilder;

    private static final String S_TAG = "S";
    private static final String ID_ATTR = "ID";
    private static final String W_TAG = "W";

    private static final String DOM_ATTR = "DOM";
    private static final String FEAT_ATTR = "FEAT";
    private static final String LEMMA_ATTR = "LEMMA";
    private static final String LINK_ATTR = "LINK";
    private static final String DOM_ROOT = "_root";

    private static final Logger logger = LoggerFactory.getLogger(TreeBankParser.class);

    private static class XMLWord {
        public XMLWord(Word word, Integer parentId, int wordId) {
            this.word = word;
            this.parentId = parentId;
            this.wordId = wordId;
        }

        public Word getWord() {
            return word;
        }

        public int getId() {
            return wordId;
        }

        public Integer getParentId() {
            return parentId;
        }

        public boolean isRoot() {
            return parentId == null;
        }

        private Word word;
        private Integer parentId;
        private int wordId;
    }
}
