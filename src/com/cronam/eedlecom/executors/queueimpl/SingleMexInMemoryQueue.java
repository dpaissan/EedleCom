package com.cronam.eedlecom.executors.queueimpl;

import com.cronam.eedlecom.executors.messages.Message;

public class SingleMexInMemoryQueue extends QueueImpl {

    private Message bb = null;

    public Message pull() throws InterruptedException {
        Message b;
        mutex.acquire();
        try{b = bb;}
        finally{mutex.release();}
        return b;
    }

    public void deleteLast() throws InterruptedException {
        mutex.acquire();
        try{bb = null;}
        finally{mutex.release();}
    }

    public void add(Message b) throws InterruptedException {
        mutex.acquire();
        try{bb = b;}
        finally{mutex.release();}
    }
}
