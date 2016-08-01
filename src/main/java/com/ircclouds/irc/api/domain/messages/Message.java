package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.IRCException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class Message {

    private static final Logger LOG = LoggerFactory.getLogger(Message.class);

    public HashMap<String, Object> tags = new HashMap<String, Object>();
    public String prefix;
    public String command;
    public ArrayList<String> params = new ArrayList<String>();

    public String raw;

    public Message(String raw) {
        this.raw = raw;
        this.parse();
        LOG.debug("new " + this.toString());
    }

    public String getText() {
        return this.params.get(this.params.size() - 1);
    }

    public String toString() {
        return "Message(tags=" + tags +
                ", prefix=" + prefix +
                ", command=" + command +
                ", params=" + params + ")";
    }

    protected void parse() {
        int position = 0;
        int nextspace = 0;
        if (this.raw.charAt(0) == '@') {
            String[] rawTags;

            nextspace = raw.indexOf(" ");
            if (nextspace == -1) {
                LOG.error("Error parsing IRC message! (Expected space after tags)");
                return;
            }

            rawTags = raw.substring(1, nextspace).split(";");

            for (int i = 0; i < rawTags.length; i++) {
                String tag = rawTags[i];
                String[] pair = tag.split("=");

                if (pair.length == 2) {
                    tags.put(pair[0], pair[1]);
                } else {
                    tags.put(pair[0], true);
                }
            }
            position = nextspace + 1;
        }

        while (raw.charAt(position) == ' ') {
            position++;
        }

        if (raw.charAt(position) == ':') {
            nextspace = raw.indexOf(" ", position);
            if (nextspace == -1) {
                LOG.error("Error parsing IRC message! (Expected space after prefix)");
                return;
            }
            prefix = raw.substring(position + 1, nextspace);
            position = nextspace + 1;

            while (raw.charAt(position) == ' ') {
                position++;
            }
        }

        nextspace = raw.indexOf(" ", position);

        if (nextspace == -1) {
            if (raw.length() > position) {
                command = raw.substring(position);
            }
            return;
        }

        command = raw.substring(position, nextspace);

        position = nextspace + 1;

        while (raw.charAt(position) == ' ') {
            position++;
        }

        while (position < raw.length()) {
            nextspace = raw.indexOf(" ", position);

            if (raw.charAt(position) == ':') {
                String param = raw.substring(position + 1);
                params.add(param);
                break;
            }

            if (nextspace != -1) {
                String param = raw.substring(position, nextspace);
                params.add(param);
                position = nextspace + 1;

                while (raw.charAt(position) == ' ') {
                    position++;
                }
                continue;
            }

            if (nextspace == -1) {
                String param = raw.substring(position);
                params.add(param);
                break;
            }
        }
    }
}
