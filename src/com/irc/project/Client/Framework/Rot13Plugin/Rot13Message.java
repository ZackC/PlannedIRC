package com.irc.project.Client.Framework.Rot13Plugin;

import java.io.Serializable;

import com.irc.project.Message.GeneralMessageType;

public class Rot13Message implements GeneralMessageType, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3987256046051208372L;
	GeneralMessageType msg;

	public Rot13Message(GeneralMessageType msg) {
		this.msg = msg;
	}

	@Override
	public String getHeader() {
		String oldHeader = msg.getHeader();
		if (oldHeader == null || oldHeader.trim() == "") {
			return "rot13";
		} else {
			return "rot13, " + oldHeader;
		}
	}

	@Override
	public String getContent() {
		String result = alterString(msg.getContent().toLowerCase());
		return result;
	}

	private static String alterString(String message) {
		char[] oldContentArray = message.toCharArray();
		for (int i = 0; i < oldContentArray.length; i++) {
			// System.out.println("old char: " + oldContentArray[i]);
			int newValue = oldContentArray[i] + 13;
			if (newValue > 122) {
				newValue = newValue - 26;
			}
			oldContentArray[i] = (char) (newValue);
			// System.out.println("new char: " + oldContentArray[i]);
		}
		System.out.println("trasformed result: " + new String(oldContentArray));
		return new String(oldContentArray);
	}

	public void setHeader(String newHeader) {
		msg.setHeader(newHeader);
	}

	public static String decrypt(String message) {
		return alterString(message);
	}
}