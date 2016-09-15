package com.cronam.eedlecom.executors;

import com.cronam.eedlecom.Logger;
import com.cronam.eedlecom.exceptions.ConnectionException;
import com.cronam.eedlecom.executors.messages.Message;
import com.cronam.eedlecom.executors.queueimpl.QueueImpl;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

public class NetworkWriter extends NetworkThread
{
    private OutputStream os;

    QueueImpl qq;
    Semaphore qqS = new Semaphore(0, true);

    protected String getIdentifier() {
        return "writer-"+Thread.currentThread().getId();
    }

    public NetworkWriter(Logger l, SocketCreator sc, SocketHolder s, Semaphore csM, Runnable onErr, QueueImpl q){
        super(l, sc, s, csM, onErr);
        qq = q;


        Message m = null;
        try {m = qq.pull();} catch (Throwable t){t.printStackTrace();}
        if(m != null)
        {
            log.Log(getIdentifier()+":[NetworkWriter] must send a message!");
            send(m);
        }
        else
            log.Log(getIdentifier()+":[NetworkWriter] no messages must be sent");
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

    public void send(Message data) {
        log.Log(getIdentifier()+":[NetworkWriter] Adding data to queue");
        try
        {
            qq.add(data);
            qqS.release();
        }
        catch (Throwable t){t.printStackTrace();}
    }

    protected void begin() throws Throwable {
        log.Log(getIdentifier()+":[NetworkWriter] getting OutputStream");
        os = getOS();
    }

    public Message getLast() throws Throwable
    {
        return qq.pull();
    }
    protected void repeat() throws Throwable {
        qqS.acquire();
        _send(qq.pull().serialize());
    }
}
