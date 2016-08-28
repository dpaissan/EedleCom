package com.cronam.eedlecom.executors;

import java.net.Socket;

public class SocketHolder
{
    public Socket s = null;
    public boolean error = false;
    public boolean errorShowed = false;

    public void setError(){
        error = true;
        s = null;
    }
}
