package com.cronam.eedlecom.executors;

import com.cronam.eedlecom.Logger;
import com.cronam.eedlecom.exceptions.ConnectionException;
import com.cronam.eedlecom.RunnableMsgIn;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

public class NetworkReader extends NetworkThread
{
    private RunnableMsgIn runnable;
    private InputStream is;
    private int soTimeout = 30_000;

    protected String getIdentifier() {
        return "reader-"+Thread.currentThread().getId();
    }

    public void setSoTimeout(int tmt) {
        log.Log(getIdentifier()+":[NetworkWriter] set So Timeout");
        soTimeout = tmt;
    }
    public int getSoTimeout() {
        log.Log(getIdentifier()+":[NetworkWriter] get So Timeout");
        return soTimeout;
    }

    protected void begin() throws Throwable {
        log.Log(getIdentifier()+":[NetworkWriter] getting InputStream");
        is = getIS();
    }

    protected void repeat() throws Throwable {
        byte[] data = recvOrNorifyDisconnection();
        log.Log(getIdentifier() +":[NetworkReader] data received: "+ (data != null ? data.length : "<null>")+", run RMI");
        runnable.run(data);
    }

    private int recv_positiveBytes(byte[] r) throws ConnectionException {
        int tmp;
        try {
            setConnSocketTimeout(soTimeout);
            tmp = is.read(r, 0, r.length);
            if(tmp <= 0)
                throw new ConnectionException("Negative or Zero bytes read: '"+tmp+"', I assume connection down.");
        }
        catch (Throwable t) {
            stop();
            throw new ConnectionException("Error on read.", t);
        }
        return tmp;
    }
    private byte[] readByteArray(int l) throws ConnectionException {
        byte[] r = new byte[l], buf;
        int iR = 0, read;

        while(iR < l)
        {
            buf = new byte[l - iR];
            read = recv_positiveBytes(buf);

            for(int i = 0; i < read; i++)
                r[iR++] = buf[i];
        }

        return r;
    }
    public byte[] recvOrNorifyDisconnection() throws ConnectionException {
        return readByteArray(ByteBuffer.wrap(readByteArray(4)).getInt());
    }

    public NetworkReader(Logger l,SocketCreator sc, SocketHolder s, Semaphore csM, RunnableMsgIn rmi, Runnable onError) {
        super(l,sc, s, csM, onError);
        runnable = rmi;
        log.Log(getIdentifier() +":[NetworkReader] new NetworkReader(...)");
    }
}
