package ru.spbau.textminer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.spbau.textminer.text.InvalidSentenceException;
import ru.spbau.textminer.text.ParserException;
import ru.spbau.textminer.text.Sentence;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class TreeBankParser {
    public TreeBankParser() throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        docBuilder = dbFactory.newDocumentBuilder();
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
            Document doc = docBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList sList = doc.getElementsByTagName(S_TAG);

            List<Sentence> sentences = new ArrayList<Sentence>();
            for (int i = 0; i < sList.getLength(); i++) {
                Element sElement = (Element) sList.item(i);
                try {
                    sentences.add(new Sentence(sElement, xmlFile.getAbsolutePath()));
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
        }
    }

    private DocumentBuilder docBuilder;
    private static final String S_TAG = "S";
    private static final Logger logger = LoggerFactory.getLogger(TreeBankParser.class);
}
