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
    boolean ackSystem;

    public void setSoTimeout(int tmt) {
        nr.setSoTimeout(tmt);
    }
    public int getSoTimeout() {
        return nr.getSoTimeout();
    }

    public EedleCom(boolean ackSystem, Logger l, final RunnableMsgIn rmi, final Runnable onError, QueueImpl qq, final Socket s) {
        SocketCreator sc = new SocketCreator() {
            public Socket getSocket() {
                return s;
            }
        };

        _connect(ackSystem, l,rmi, sc, onError, qq);
    }
    public EedleCom(boolean ackSystem, Logger l, final RunnableMsgIn rmi, Runnable onError, QueueImpl qq, final String ip, final int port) {

        SocketCreator sc = new SocketCreator(){public Socket getSocket()
        {
            Socket s = null;
            try{s = new Socket(ip,port);}
            catch(IOException e){e.printStackTrace();}
            return s;
        }};

        _connect(ackSystem, l,rmi,sc, onError, qq);
    }
    private void _connect(boolean ackSystem, Logger l, RunnableMsgIn rmi, SocketCreator sc, Runnable onError, QueueImpl qq) {
        SocketHolder shared = new SocketHolder();
        Semaphore csM = new Semaphore(1, true);
        this.ackSystem = ackSystem;
        nr = new NetworkReader(l,sc, shared, csM, rmi, onError, qq);
        nw = new NetworkWriter(l,sc, shared, csM, onError, qq);
    }

    public void send(Message data) {
        nw.send(data);
        //Do nothing otherwise, disconnection has already spotted in onError runnable, also client will know if message
        //arrived by receiving an acknowledgment by server, it is the only secure way to be sure the message has arrived.
    }

    public boolean isSending() {
        boolean b = false;
        try {
            b = nw.isSending();
        }catch (Throwable t){t.printStackTrace();}
        return ackSystem && b;
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
