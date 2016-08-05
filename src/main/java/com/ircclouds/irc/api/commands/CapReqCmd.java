package com.ircclouds.irc.api.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * CAP command to request 1 or more capabilities.
 *
 * @author Danny van Heumen
 */
public class CapReqCmd extends CapCmd {
	private final List<String> extensions = new LinkedList<String>();

	public CapReqCmd(String extension, String... extensions)
	{
		this.extensions.add(extension);
		this.extensions.addAll(Arrays.asList(extensions));
	}

	@Override
	public String toString()
	{
		final StringBuilder req = new StringBuilder("CAP REQ :");
		for (String ext : extensions) {
			req.append(ext).append(" ");
		}
		// Remove trailing space
		return req.deleteCharAt(req.length() - 1).append(CRNL).toString();
	}
}
