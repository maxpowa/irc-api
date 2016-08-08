package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.ChannelModeA;
import com.ircclouds.irc.api.domain.ChannelModeB;
import com.ircclouds.irc.api.domain.ChannelModeC;
import com.ircclouds.irc.api.domain.ChannelModeD;
import com.ircclouds.irc.api.domain.ChannelModes;
import com.ircclouds.irc.api.domain.IRCUserStatusMode;
import com.ircclouds.irc.api.domain.IRCUserStatuses;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ChannelMode extends AbstractChannelMessage {
    private final ChannelModes chanModes;
    private final IRCUserStatuses userModes;

    private List<com.ircclouds.irc.api.domain.ChannelMode> addedModes;
    private List<com.ircclouds.irc.api.domain.ChannelMode> removedModes;

    public ChannelMode(AbstractMessage aMsg, ChannelModes chanModes, IRCUserStatuses userModes) {
        super(aMsg);

        this.chanModes = chanModes;
        this.userModes = userModes;

        this.populateModes();
    }

    public void populateModes() {
        Stack<String> _params = new Stack<String>();
        _params.addAll(this.params.subList(2, this.params.size()));

        List<com.ircclouds.irc.api.domain.ChannelMode> _addedModes = new ArrayList<com.ircclouds.irc.api.domain.ChannelMode>();
        List<com.ircclouds.irc.api.domain.ChannelMode> _removedModes = new ArrayList<com.ircclouds.irc.api.domain.ChannelMode>();

        String _modesStr = this.params.get(1);
        for (int _i = 0; _i < _modesStr.length(); _i++) {
            int _plusIndex = _modesStr.indexOf("+", _i + 1);
            int _minusIndex = _modesStr.indexOf("-", _i + 1);
            int _end = 0;
            if (_plusIndex < _minusIndex) {
                if (_plusIndex != -1) {
                    _end = _plusIndex;
                } else {
                    _end = _minusIndex;
                }
            } else if (_minusIndex < _plusIndex) {
                if (_minusIndex != -1) {
                    _end = _minusIndex;
                } else {
                    _end = _plusIndex;
                }
            } else {
                _end = _modesStr.length();
            }

            char _m = _modesStr.charAt(_i);
            String _mode = _modesStr.substring(_i + 1, _end);
            _i = _end - 1;
            if (_m == '+') {
                parseModes(_params, _addedModes, _mode, true);
            } else {
                parseModes(_params, _removedModes, _mode, false);
            }
        }

        this.addedModes = _addedModes;
        this.removedModes = _removedModes;
    }

    public String getModeStr() {
        StringBuilder _sb = new StringBuilder();

        int _i = this.params.size();
        for (int _j = 1; _j < _i; _j++) {
            _sb.append(this.params.get(_j)).append(" ");
        }

        return _sb.substring(0, _sb.length() - 1).toString();
    }

    private void parseModes(Stack<String> aParams, List<com.ircclouds.irc.api.domain.ChannelMode> aModes, String aModesStr, boolean aAddFlag) {
        for (int _i = 0; _i < aModesStr.length(); _i++) {
            char _mode = aModesStr.charAt(_i);

            if (chanModes.isOfTypeA(_mode)) {
                aModes.add(new ChannelModeA(_mode, aParams.pop()));
            } else if (chanModes.isOfTypeB(_mode)) {
                aModes.add(new ChannelModeB(_mode, aParams.pop()));
            } else if (chanModes.isOfTypeC(_mode)) {
                if (aAddFlag) {
                    aModes.add(new ChannelModeC(_mode, aParams.pop()));
                } else {
                    aModes.add(new ChannelModeC(_mode));
                }
            } else if (chanModes.isOfTypeD(_mode)) {
                aModes.add(new ChannelModeD(_mode));
            } else if (userModes.contains(_mode)) {
                aModes.add(new IRCUserStatusMode(userModes.getUserStatus(_mode), aParams.pop()));
            }
        }
    }

    public List<com.ircclouds.irc.api.domain.ChannelMode> getAddedModes() {
        return addedModes;
    }

    public List<com.ircclouds.irc.api.domain.ChannelMode> getRemovedModes() {
        return removedModes;
    }
}
