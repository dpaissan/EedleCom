package com.cronam.eedlecom;

import com.cronam.eedlecom.exceptions.ConnectionException;
import com.cronam.eedlecom.executors.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Queue;
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

    public EedleCom(Logger l, final RunnableMsgIn rmi, final Runnable onError, final Socket s) {
        SocketCreator sc = new SocketCreator() {
            public Socket getSocket() {
                return s;
            }
        };

        _connect(l,rmi, sc, onError);
    }
    public EedleCom(Logger l, final RunnableMsgIn rmi, Runnable onError, final String ip, final int port) {

        SocketCreator sc = new SocketCreator(){public Socket getSocket()
        {
            Socket s = null;
            try{s = new Socket(ip,port);}
            catch(IOException e){e.printStackTrace();}
            return s;
        }};

        _connect(l,rmi,sc, onError);
    }
    private void _connect(Logger l, RunnableMsgIn rmi, SocketCreator sc, Runnable onError) {
        SocketHolder shared = new SocketHolder();
        Semaphore csM = new Semaphore(1, true);
        nr = new NetworkReader(l,sc, shared, csM, rmi, onError);
        nw = new NetworkWriter(l,sc, shared, csM, onError);
    }

    public void send(byte[] data) throws ConnectionException {
        if(!isConnected())
            throw new ConnectionException("disconnesso");
        nw.send(data);
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
