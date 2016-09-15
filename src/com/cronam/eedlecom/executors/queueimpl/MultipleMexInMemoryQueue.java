package com.cronam.eedlecom.executors.queueimpl;

import com.cronam.eedlecom.executors.messages.Message;

import java.util.LinkedList;
import java.util.Queue;

public class MultipleMexInMemoryQueue extends QueueImpl
{
    private Queue<Message> qq = new LinkedList<>();
    public Message _pull() {
        return qq.poll();
    }

    public void _deleteLast() {
        qq.remove();
    }

    public void _add(Message message) {
        qq.add(message);
    }
}
