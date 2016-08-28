package com.cronam.eedlecom;

import com.cronam.eedlecom.exceptions.ConnectionException;

public interface RunnableMsgIn
{
    void run(byte[] m) throws ConnectionException;
}
