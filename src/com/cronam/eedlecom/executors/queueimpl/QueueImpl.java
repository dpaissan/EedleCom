package com.cronam.eedlecom.executors.queueimpl;

import com.cronam.eedlecom.executors.messages.Message;

import java.util.concurrent.Semaphore;

public abstract class QueueImpl
{
    protected Semaphore mutex = new Semaphore(1, true);

    public abstract Message pull() throws InterruptedException;
    public abstract void deleteLast() throws InterruptedException;
    public abstract void add(Message b) throws InterruptedException;
}
