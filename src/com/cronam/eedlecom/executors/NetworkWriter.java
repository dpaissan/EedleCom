package com.cronam.eedlecom.executors;

import com.cronam.eedlecom.Logger;
import com.cronam.eedlecom.exceptions.ConnectionException;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

public class NetworkWriter extends NetworkThread
{
    private OutputStream os;

    Queue<byte[]> qq = new LinkedList<>();
    Semaphore qqM = new Semaphore(1, true);
    Semaphore qqS = new Semaphore(0, true);

    protected String getIdentifier() {
        return "writer-"+Thread.currentThread().getId();
    }

    public NetworkWriter(Logger l, SocketCreator sc, SocketHolder s, Semaphore csM, Runnable onErr){
        super(l, sc, s, csM, onErr);
        log.Log(getIdentifier()+":[NetworkWriter] NetworkWriter(...)");
    }

    private void _send(byte[] data) throws ConnectionException {
        try
        {
            if(data == null)
                throw new Exception("data o od = null!");
            log.Log(getIdentifier()+":[NetworkWriter] sending data: "+ data.length);
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(data.length);
            os.write(b.array());
            os.write(data);
            os.flush();
        }
        catch (Throwable t)
        {
            log.Log(getIdentifier()+":[NetworkWriter] Error on write!!!");
            throw new ConnectionException("Error on write.", t);
        }
    }

    public void send(byte[] data) {
        log.Log(getIdentifier()+":[NetworkWriter] Adding data to queue");
        try
        {
            qqM.acquire();
            try{qq.add(data);}
            finally{qqM.release();}
            qqS.release();
        }
        catch (Throwable t){t.printStackTrace();}
    }

    protected void begin() throws Throwable {
        log.Log(getIdentifier()+":[NetworkWriter] getting OutputStream");
        os = getOS();
    }

    protected void repeat() throws Throwable {
        qqS.acquire();
        qqM.acquire();
        try{_send(qq.remove());}
        finally{qqM.release();}
    }
}
