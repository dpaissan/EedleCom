package com.cronam.eedlecom.executors;

import com.cronam.eedlecom.Logger;

import java.util.concurrent.Semaphore;

public abstract class NetworkThread extends SocketMgr
{
    private boolean cont = true;

    protected abstract void begin()throws Throwable;
    protected abstract void repeat()throws Throwable;

    protected Runnable thread() {
        return new Runnable() {public void run()
        {

            log.Log(getIdentifier()+":[NetworkThread] thread started, creating socket if necessary");

            if(!setSocket())
                onError();
            else
            {
                try
                {
                    log.Log(getIdentifier()+":[NetworkThread] calling begin() of thread loop");
                    begin();
                    while (cont)
                        repeat();
                    log.Log(getIdentifier()+":[NetworkThread] thread loop finishing without error -> cont = false");
                }
                catch(Throwable t)
                {
                    t.printStackTrace();
                    log.Log(getIdentifier()+":[NetworkThread] error occurred in begin() or repeat() msg:"+t.getMessage()+" lmes: "+t.getLocalizedMessage()+" cause: "+t.getCause());
                    onError();
                }
                log.Log(getIdentifier()+":[NetworkThread] thread almost ended, stop calling!");
                stop();
            }
            log.Log(getIdentifier()+":[NetworkThread] thread ended!");
        }};
    }

    public void stop() {
        log.Log(getIdentifier()+":[NetworkThread] stop called");
        cont = false;
        super.stop();
    }

    public NetworkThread(Logger l, SocketCreator sc, SocketHolder s, Semaphore csM, Runnable onErr) {
        super(l,sc, s, csM, onErr);
        log.Log(getIdentifier()+":[NetworkThread] NetworkThread(...)");
        log.Log(getIdentifier()+":[NetworkThread] starting thread");
        new Thread(thread()).start();
    }
}
