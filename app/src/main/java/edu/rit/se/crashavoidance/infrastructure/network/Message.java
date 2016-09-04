package edu.rit.se.crashavoidance.infrastructure.network;

import java.io.Serializable;

/**
 * Created by Brett on 8/2/2016.
 */
public class Message implements Serializable {

    public final MessageType messageType;
    public final byte[] message;

    public Message(final MessageType messageType, final byte[] message) {
        this.messageType = messageType;
        this.message = message;
    }
}
