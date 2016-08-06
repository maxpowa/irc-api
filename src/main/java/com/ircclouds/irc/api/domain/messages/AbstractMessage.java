package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.messages.interfaces.IMessage;
import com.ircclouds.irc.api.domain.messages.interfaces.ISource;
import com.ircclouds.irc.api.utils.ParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractMessage implements IMessage {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMessage.class);

    private HashMap<String, Object> tags = new HashMap<String, Object>();
    public ArrayList<String> params = new ArrayList<String>();
    public String prefix;
    public String command;

    private boolean text = false;

    public String raw;

    public AbstractMessage(String raw) {
        this.raw = raw;
        this.parse();
        LOG.trace("new " + this.toString());
    }

    public AbstractMessage(AbstractMessage msg) {
        this.raw = msg.raw;
        this.tags = msg.tags;
        this.prefix = msg.prefix;
        this.command = msg.command;
        this.text = msg.text;
        this.params = msg.params;
        LOG.trace("map " + this.toString());
    }

    public ISource getSource() {
        if (this.prefix != null) {
            return ParseUtils.getSource(this.prefix);
        }
        return null;
    }

    public List<String> getParams() {
        return this.params;
    }

    public String asRaw() {
        return this.raw;
    }

    public String getText() {
        if (this.params.size() <= 0 || !this.text) {
            return null;
        }
        return this.params.get(this.params.size() - 1);
    }

    public HashMap<String, Object> getTags() {
        return this.tags;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(tags=" + tags +
                ", prefix=" + prefix +
                ", command=" + command +
                ", params=" + params + ")";
    }

    protected void parse() {
        if (this.raw.length() <= 0) throw new ParseError("Expected non-zero input length");

        int position = 0;
        int nextspace = 0;
        if (this.raw.charAt(0) == '@') {
            String[] rawTags;

            nextspace = raw.indexOf(" ");
            if (nextspace == -1) {
                throw new ParseError("Expected space following tag string");
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
                throw new ParseError("Expected space following prefix string");
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
                text = true;
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

    public static class ParseError extends RuntimeException {
        public ParseError(String reason) {
            super(reason);
        }
    }
}
