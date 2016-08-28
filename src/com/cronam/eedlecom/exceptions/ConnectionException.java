package com.cronam.eedlecom.exceptions;

public class ConnectionException extends Exception {
    public ConnectionException(String m)
    {
        super(m);
    }
    public ConnectionException(String m, Throwable t)
    {
        super(m,t);
    }
    public ConnectionException(Throwable t)
    {
        super(t);
    }
}
