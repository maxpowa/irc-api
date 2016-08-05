package com.ircclouds.irc.api.commands;

public class SendServerPingReplyCmd implements ICommand
{
    private String text;

    public SendServerPingReplyCmd(String aText) {
        text = aText;
    }

    public String toString() {
        return "PONG :" + text + CRNL;
    }

}
