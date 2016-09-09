package com.cronam.eedlecom.executors;

import com.cronam.eedlecom.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Semaphore;

public abstract class SocketMgr
{
    private SocketHolder sharedSocket;
    private Semaphore csM;
    SocketCreator sc;
    private Runnable _onError;
    protected Logger log;

    protected void onError() {
        log.Log(getIdentifier() +":[SocketMgr] onError received");
        try
        {
            csM.acquire();
            try
            {
                if(!sharedSocket.errorShowed)
                {
                    log.Log(getIdentifier() +":[SocketMgr] onError showError");
                    sharedSocket.errorShowed = true;
                    _onError.run();
                }
                else
                    log.Log(getIdentifier()+":[SocketMgr] onError already called, so do nothing");
            }
            finally{csM.release();}
        }
        catch(InterruptedException e){e.printStackTrace();}
    }

    protected InputStream getIS() throws IOException {
        log.Log(getIdentifier()+":[SocketMgr] get inputstream");
        return sharedSocket.s.getInputStream();
    }
    protected OutputStream getOS() throws IOException {
        log.Log(getIdentifier()+":[SocketMgr] get outputstream");
        return sharedSocket.s.getOutputStream();
    }

    public SocketMgr(Logger l, SocketCreator sc, SocketHolder ss, Semaphore csM, Runnable onErr) {
        this.csM = csM;
        this.sharedSocket = ss;
        this.sc = sc;
        _onError = onErr;
        log = l;
        log.Log(getIdentifier()+":[SocketMgr] new SocketMgr(...)");
    }

    protected boolean setSocket() {
        log.Log(getIdentifier()+":[SocketMgr] set socket called");
        try
        {
            csM.acquire();
            try {
                if(sharedSocket.s == null && !sharedSocket.error)
                {
                    log.Log(getIdentifier()+":[SocketMgr] set socket never called, getSocket will be called");
                    Socket s = sc.getSocket();
                    if(s == null)
                    {
                        log.Log(getIdentifier()+":[SocketMgr] socket null -> error occurred in socket creation");
                        sharedSocket.setError();
                    }
                    else
                    {
                        log.Log(getIdentifier()+":[SocketMgr] socket well formed! -> all ok");
                        sharedSocket.s = s;
                    }
                }
                else
                    log.Log(getIdentifier()+":[SocketMgr] set socket already called, so do nothing");
            }
            finally{csM.release();}
        }
        catch(Throwable t){t.printStackTrace();}
        return sharedSocket != null && !sharedSocket.error;
    }

    public boolean isConnected() {
        boolean c = false;
        try
        {
            csM.acquire();
            try {
                c = sharedSocket.s != null && sharedSocket.s.isConnected();
            } finally {
                csM.release();
            }
        }
        catch(Throwable t) {t.printStackTrace();}
        log.Log(getIdentifier()+":[SocketMgr] is connected: "+c);
        return c;
    }

    public void stop() {
        log.Log(getIdentifier()+":[SocketMgr] stop called");
        try
        {
            csM.acquire();
            try {
                if(sharedSocket.s != null)
                {
                    log.Log(getIdentifier()+":[SocketMgr] socket not already closed, now i close it");
                    sharedSocket.s.close();
                }
                else
                    log.Log(getIdentifier()+":[SocketMgr] socket already closed");
                sharedSocket.s = null;
            } finally {
                csM.release();
            }
        }
        catch(Throwable t) {t.printStackTrace();}
    }

    protected abstract String getIdentifier();
}
