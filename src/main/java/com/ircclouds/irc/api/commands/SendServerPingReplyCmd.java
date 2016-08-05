package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICommand;

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
