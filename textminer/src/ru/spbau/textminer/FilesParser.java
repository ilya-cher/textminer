package ru.spbau.textminer;

import ru.spbau.textminer.text.ParserException;
import ru.spbau.textminer.text.Sentence;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface FilesParser {
    List<Sentence> parseFiles(Collection<File> files) throws ParserException;
}
