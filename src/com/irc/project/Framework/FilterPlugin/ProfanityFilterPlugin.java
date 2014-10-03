package com.irc.project.Framework.FilterPlugin;

import java.awt.Panel;
import java.awt.TextArea;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.irc.project.Client.Client;
import com.irc.project.Framework.PlugInInterface;
import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Message.Message;
import com.irc.project.Server.ServerConnection;

public class ProfanityFilterPlugin implements PlugInInterface {

	private static final HashMap<String, Boolean> profaneWords = new HashMap<String, Boolean>();
	static {
		profaneWords.put("ass", new Boolean(true));
		profaneWords.put("shit", new Boolean(true));
		profaneWords.put("fuck", new Boolean(true));
		profaneWords.put("dick", new Boolean(true));
	}

	@Override
	public void logInAction(Object type) {
		// doesn't do anything for this plugin

	}

	@Override
	public GeneralMessageType sendMessageAction(GeneralMessageType msg) {
		return msg;
	}

	@Override
	public GeneralMessageType recieveMessageAction(Object type,
			GeneralMessageType msg, ServerConnection sc) {
		if (type instanceof Client) {
			String content = msg.getContent();
			String newContent = filterLine(content.trim());
			return (new Message(msg.getHeader(), newContent));
		}
		return msg;
	}

	@Override
	public void drawSelectionOption(Panel panel, TextArea ta) {
		// doesn't do anything for this plugin

	}

	@Override
	public HashSet<ServerConnection> alterBroadCastGroup(
			HashSet<ServerConnection> connections) {
		return connections;
	}

	@Override
	public List<String> getHeaders() {
		List<String> headers = new LinkedList<String>();
		headers.add("msg");
		return headers;
	}

	@Override
	public void logAction(Object type, GeneralMessageType msg) {
		// does nothing for this plugin

	}

	private String filterLine(String line) {
		String[] wordsInLine = line.trim().split("\\s+");
		for (int i = 0; i < wordsInLine.length; i++) {
			if (profaneWords.containsKey(wordsInLine[i])) {
				String blockedWord = "";
				for (int j = 0; j < wordsInLine[i].length(); j++) {
					blockedWord += "*";
				}
				wordsInLine[i] = blockedWord;
			}
		}
		String resultString = "";
		for (int k = 0; k < wordsInLine.length; k++) {
			if (k != wordsInLine.length - 1) {
				resultString += wordsInLine[k] + " ";
			} else {
				resultString += wordsInLine[k] + "\n";
			}
		}
		return resultString;
	}

}
