package ru.spbau.textminer.text;

public class InvalidSentenceException extends Exception {
    public InvalidSentenceException(String message) {
        super(message);
    }
    public InvalidSentenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
