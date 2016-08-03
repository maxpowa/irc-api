package com.ircclouds.irc.api.domain.messages;

/**
 * Generic Message object that allows instantiation of AbstractMessage
 */
public class GenericMessage extends AbstractMessage {
    public GenericMessage(String raw) {
        super(raw);
    }
}
