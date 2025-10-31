package com.zizto.exception;

public class DAOException extends RuntimeException {
    public DAOException(String msg, Throwable thr) {
        super(msg, thr);
    }
}
