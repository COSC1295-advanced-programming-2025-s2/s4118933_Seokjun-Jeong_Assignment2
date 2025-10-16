package main.exceptions;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String msg){ super(msg); }
}
