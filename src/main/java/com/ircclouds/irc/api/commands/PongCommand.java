package com.ircclouds.irc.api.commands;

import com.ircclouds.irc.api.commands.interfaces.ICommand;

public class PongCommand implements ICommand
{
    private String text;

    public PongCommand(String aText) {
        text = aText;
    }

    public String toString() {
        return "PONG :" + text + CRNL;
    }

}
