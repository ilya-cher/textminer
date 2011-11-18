package ru.spbau.textminer.text;

public class InvalidFeatureException extends Exception {
    public InvalidFeatureException(String message) {
        super(message);
    }

    public InvalidFeatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
