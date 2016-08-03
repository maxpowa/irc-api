package com.ircclouds.irc.api.om;

import com.ircclouds.irc.api.domain.ChannelModeA;
import com.ircclouds.irc.api.domain.ChannelModeB;
import com.ircclouds.irc.api.domain.ChannelModeC;
import com.ircclouds.irc.api.domain.ChannelModeD;
import com.ircclouds.irc.api.domain.ChannelModes;
import com.ircclouds.irc.api.domain.IRCServerOptions;
import com.ircclouds.irc.api.domain.IRCUserStatus;
import com.ircclouds.irc.api.domain.IRCUserStatuses;
import com.ircclouds.irc.api.domain.messages.ChannelMode;
import com.ircclouds.irc.api.domain.messages.GenericMessage;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public class TestCaseChanModeBuilder extends TestCase
{
	private AbstractChanModeBuilder chanModeBuilder;
	
	public void setUp()
	{
		final IRCUserStatuses _ucs = new IRCUserStatuses(new HashSet<IRCUserStatus>() {{ add(new IRCUserStatus('o', '@', 1)); }});
		
		chanModeBuilder = new AbstractChanModeBuilder()
		{
			@Override
			protected IRCServerOptions getIRCServerOptions()
			{
				return new IRCServerOptions(new Properties())
				{
					public IRCUserStatuses getUserChanStatuses()
					{
						return _ucs;
					}
					
					public ChannelModes getChannelModes()
					{
						return new ChannelModes(
							new HashSet<Character> () {{ add('e'); add('I'); add('b'); add('q'); }}, 
							new HashSet<Character> () {{ add('k'); }}, 
							new HashSet<Character> () {{ add('f'); add('l'); add('j'); }}, 
							new HashSet<Character> () {{ add('C'); add('F'); add('L'); add('M'); add('P'); add('Q'); add('c'); add('g'); add('i'); add('m'); add('n'); add('p'); add('r'); add('s'); add('t'); add('z'); }}
						);
					}
				};
			}		
		};
	}
	
	public void testAddModesOnly()
	{
		ChannelMode _msg = chanModeBuilder.build(new GenericMessage(":krad!~k@unaffiliated/krad MODE #r0b0t +kt key"));

		List<com.ircclouds.irc.api.domain.ChannelMode> _addedModes = _msg.getAddedModes();
		List<com.ircclouds.irc.api.domain.ChannelMode> _removedModes = _msg.getRemovedModes();
		
		assertTrue("+kt key".equals(_msg.getModeStr()));
		assertTrue(_addedModes != null);
		assertTrue(_addedModes.size() == 2);
		assertTrue(_addedModes.contains(new ChannelModeD('t')));
		assertTrue(_addedModes.contains(new ChannelModeB('k', "key")));
		
		assertTrue(_removedModes != null);
		assertTrue(_removedModes.size() == 0);
	}
	
	public void testRemoveModesOnly()
	{
		ChannelMode _msg = chanModeBuilder.build(new GenericMessage(":krad!~k@unaffiliated/krad MODE #r0b0t -kts *"));

		List<com.ircclouds.irc.api.domain.ChannelMode> _addedModes = _msg.getAddedModes();
		List<com.ircclouds.irc.api.domain.ChannelMode> _removedModes = _msg.getRemovedModes();
		
		assertTrue("-kts *".equals(_msg.getModeStr()));
		assertTrue(_removedModes != null);
		assertTrue(_removedModes.size() == 3);
		assertTrue(_removedModes.contains(new ChannelModeD('t')));
		assertTrue(_removedModes.contains(new ChannelModeD('s')));
		assertTrue(_removedModes.contains(new ChannelModeB('k', "*")));
		
		assertTrue(_addedModes != null);
		assertTrue(_addedModes.size() == 0);
	}
	
	public void testAddModesWith2Params()
	{
		ChannelMode _msg = chanModeBuilder.build(new GenericMessage(":krad!~k@unaffiliated/krad MODE #r0b0t +kltn key 4"));

		List<com.ircclouds.irc.api.domain.ChannelMode> _addedModes = _msg.getAddedModes();
		List<com.ircclouds.irc.api.domain.ChannelMode> _removedModes = _msg.getRemovedModes();
		
		assertTrue("+kltn key 4".equals(_msg.getModeStr()));
		assertTrue(_addedModes != null);
		assertTrue(_addedModes.size() == 4);
		assertTrue(_addedModes.contains(new ChannelModeD('t')));
		assertTrue(_addedModes.contains(new ChannelModeD('n')));
		assertTrue(_addedModes.contains(new ChannelModeB('k', "*")));
		assertTrue(_addedModes.contains(new ChannelModeC('l', "6")));
		
		assertTrue(_removedModes != null);
		assertTrue(_removedModes.size() == 0);
	}
	
	public void testAddRemoveModes()
	{
		ChannelMode _msg = chanModeBuilder.build(new GenericMessage(":krad!~k@unaffiliated/krad MODE #r0b0t +kt-ln key"));

		List<com.ircclouds.irc.api.domain.ChannelMode> _addedModes = _msg.getAddedModes();
		List<com.ircclouds.irc.api.domain.ChannelMode> _removedModes = _msg.getRemovedModes();
		
		assertTrue("+kt-ln key".equals(_msg.getModeStr()));
		assertTrue(_addedModes != null);
		assertTrue(_addedModes.size() == 2);
		assertTrue(_addedModes.contains(new ChannelModeD('t')));

		assertTrue(_addedModes.contains(new ChannelModeB('k', "*")));
		
		assertTrue(_removedModes != null);
		assertTrue(_removedModes.size() == 2);
		assertTrue(_removedModes.contains(new ChannelModeC('l')));
		assertTrue(_removedModes.contains(new ChannelModeD('n')));

		_msg = chanModeBuilder.build(new GenericMessage(":krad!~k@unaffiliated/krad MODE #r0b0t -e+ln key key"));

		_addedModes = _msg.getAddedModes();
		_removedModes = _msg.getRemovedModes();

		assertTrue("-e+ln key key".equals(_msg.getModeStr()));
		assertTrue(_addedModes != null);
		assertTrue(_addedModes.size() == 2);
		assertTrue(_addedModes.contains(new ChannelModeC('l')));
		assertTrue(_addedModes.contains(new ChannelModeD('n')));

		assertTrue(_removedModes != null);
		assertTrue(_removedModes.size() == 1);
		assertTrue(_removedModes.contains(new ChannelModeA('e', "*")));
	}
	
	public void testRemoveLimitOnly()
	{
		ChannelMode _msg = chanModeBuilder.build(new GenericMessage(":krad!~k@unaffiliated/krad MODE #r0b0t -l"));
		
		assertTrue(_msg.getRemovedModes().contains(new ChannelModeC('l')));			
	}
	
	public void testAddRemoveAddRemove()
	{
		ChannelMode _msg = chanModeBuilder.build(new GenericMessage(":krad!~k@unaffiliated/krad MODE #r0b0t +nto-o+o-o+o-o goraaab goraaab goraaab goraaab goraaab goraaab"));

		List<com.ircclouds.irc.api.domain.ChannelMode> _addedModes = _msg.getAddedModes();
		List<com.ircclouds.irc.api.domain.ChannelMode> _removedModes = _msg.getRemovedModes();
		
		assertTrue(_addedModes != null);
		assertTrue(_addedModes.size() == 5);
		
		assertTrue(_removedModes != null);
		assertTrue(_removedModes.size() == 3);
	}

	public void testSourceCapture() {
		String input = ":krad!~k@unaffiliated/krad MODE #r0b0t +t-l";
		ChannelMode _msg = chanModeBuilder.build(new GenericMessage(input));

		assertEquals("krad!~k@unaffiliated/krad", _msg.getSource().toString());
		assertEquals("#r0b0t", _msg.getChannelName());
		assertEquals(input, _msg.asRaw());
	}
}
