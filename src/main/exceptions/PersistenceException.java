package main.exceptions;

public class PersistenceException extends RuntimeException {
    public PersistenceException(String msg, Throwable t){ super(msg, t); }
}