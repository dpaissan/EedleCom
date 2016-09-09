package com.cronam.eedlecom;

import com.cronam.eedlecom.exceptions.ConnectionException;
import com.cronam.eedlecom.executors.messages.Message;

public interface RunnableMsgIn
{
    void run(Message m) throws ConnectionException;
}
