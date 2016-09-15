package com.cronam.eedlecom.executors.queueimpl;

import com.cronam.eedlecom.executors.messages.Message;

public class SingleMexInMemoryQueue extends QueueImpl {

    private Message mm = null;

    public Message _pull() {
        return mm;
    }

    public void _deleteLast() {
        mm = null;
    }

    public void _add(Message m) {
        mm = m;
    }
}
