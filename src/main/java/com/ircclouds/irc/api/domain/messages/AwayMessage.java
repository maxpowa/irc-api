package com.ircclouds.irc.api.domain.messages;

import com.ircclouds.irc.api.domain.IRCUser;
import com.ircclouds.irc.api.domain.messages.interfaces.IUserMessage;
import com.ircclouds.irc.api.utils.ParseUtils;

/**
 * Away notification message. (IRCv3 "away-notify" capability)
 *
 * The away message is an update for a user's presence status (away, available).
 * In case the user is available, the message will be null and {@code #isAway()}
 * will be false. If the user is away, {@code #isAway()} will be true and a away
 * message is available.
 *
 * @author Danny van Heumen
 */
public class AwayMessage extends Message implements IUserMessage
{

	public AwayMessage(Message message) {
		super(message);

	}

	@Override
	public IRCUser getSource()
	{
		return ParseUtils.getUser(this.prefix);
	}

	@Override
	public String asRaw()
	{
		final StringBuilder raw = new StringBuilder(":");
		raw.append(this.getSource()).append(" AWAY");
		if (this.getText() != null)
		{
			raw.append(" :").append(this.getText());
		}
		return raw.toString();
	}

	/**
	 * Indicates whether an away message is present and the user is away.
	 *
	 * If user is away, this implies that a message is present. If user is
	 * available, this implies that message is null.
	 *
	 * @return False if user is available, true if user is away.
	 */
	public boolean isAway()
	{
		return this.getText() != null;
	}
}
