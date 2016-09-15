package com.cronam.eedlecom.executors.queueimpl;

import com.cronam.eedlecom.executors.messages.Message;

import java.util.concurrent.Semaphore;

public abstract class QueueImpl
{
    protected Semaphore mutex = new Semaphore(1, true);

    public Message pull() throws InterruptedException {
        Message m = null;
        mutex.acquire();
        try{
            m = _pull();
            if(!m.mustBeAcked())
                _deleteLast();
        }
        finally{mutex.release();}
        return m;
    }

    public void add(Message m) throws InterruptedException {
        mutex.acquire();
        try{_add(m);}
        finally{mutex.release();}
    }

    public void deleteLast() throws InterruptedException {
        mutex.acquire();
        try{_deleteLast();}
        finally{mutex.release();}
    }

    protected abstract Message _pull();
    public abstract void _deleteLast();
    public abstract void _add(Message b);
}
