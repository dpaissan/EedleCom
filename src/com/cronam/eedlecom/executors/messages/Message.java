package com.cronam.eedlecom.executors.messages;

public class Message
{
    private byte cmd;
    private byte ack;
    private byte[] data;

    public Message(byte cmd){
        this.cmd = cmd;
        this.ack = 0;
    }
    public Message(byte cmd, byte ack){
        this.cmd = cmd;
        this.ack = ack;
    }
    public Message(byte[] blob){
        cmd = blob[0];
        ack = blob[1];

        data = new byte[blob.length-2];
        for(int i = 2; i < blob.length; i++)
            data[i-2] = blob[i];
    }

    public byte getCommand(){
        return cmd;
    }
    public void setNonAck(){
        ack = 0;
    }
    public byte getAck(){
        return ack;
    }
    public Message setAck(byte ack){
        this.ack = ack;
        return this;
    }
    public boolean mustBeAcked(){
        return cmd >= 0;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public byte[] getData() {
        return this.data;
    }
    public byte[] serialize() {
        byte[] blob = new byte[data.length+2];

        blob[0] = cmd;
        blob[1] = ack;

        for(int i = 0; i < data.length; i++)
            blob[i+2] = data[i];
        return blob;
    }
}
