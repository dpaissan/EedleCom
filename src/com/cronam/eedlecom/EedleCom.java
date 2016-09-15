package com.cronam.eedlecom;

import com.cronam.eedlecom.executors.*;
import com.cronam.eedlecom.executors.messages.Message;
import com.cronam.eedlecom.executors.queueimpl.QueueImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class EedleCom
{
    NetworkReader nr;
    NetworkWriter nw;

    public void setSoTimeout(int tmt) {
        nr.setSoTimeout(tmt);
    }
    public int getSoTimeout() {
        return nr.getSoTimeout();
    }

    public EedleCom(Logger l, final RunnableMsgIn rmi, final Runnable onError, QueueImpl qq, final Socket s) {
        SocketCreator sc = new SocketCreator() {
            public Socket getSocket() {
                return s;
            }
        };

        _connect(l,rmi, sc, onError, qq);
    }
    public EedleCom(Logger l, final RunnableMsgIn rmi, Runnable onError, QueueImpl qq, final String ip, final int port) {

        SocketCreator sc = new SocketCreator(){public Socket getSocket()
        {
            Socket s = null;
            try{s = new Socket(ip,port);}
            catch(IOException e){e.printStackTrace();}
            return s;
        }};

        _connect(l,rmi,sc, onError, qq);
    }
    private void _connect(Logger l, RunnableMsgIn rmi, SocketCreator sc, Runnable onError, QueueImpl qq) {
        SocketHolder shared = new SocketHolder();
        Semaphore csM = new Semaphore(1, true);
        nr = new NetworkReader(l,sc, shared, csM, rmi, onError, qq);
        nw = new NetworkWriter(l,sc, shared, csM, onError, qq);
    }

    public void send(Message data) {
        nw.send(data);
    }

    public boolean isSending(boolean requestsOnly) {
        boolean b = false;
        try {
            Message m = nw.getLast();
            if(m != null && (!requestsOnly || m.mustBeAcked()))
                b = true;
        }catch (Throwable t){t.printStackTrace();}
        return b;
    }

    public boolean isConnected() {
        return nr != null && nw != null && nr.isConnected() && nw.isConnected();
    }

    public void disconnect() {

        if(nr != null)
            nr.stop();
        if(nw != null)
            nw.stop();
        nr = null;
        nw = null;
    }
}
