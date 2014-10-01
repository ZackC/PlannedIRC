package com.irc.project.Framework.ReverseEnryptionPlugin;

import java.io.Serializable;

import com.irc.project.Message.GeneralMessageType;

public class ReverseMessage implements GeneralMessageType, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6795834445502670583L;
	/**
	 * 
	 */
	/**
	 * 
	 */

	GeneralMessageType msg;

	public ReverseMessage(GeneralMessageType msg) {
		this.msg = msg;
	}

	@Override
	public String getHeader() {
		System.out.println("");
		new Exception().printStackTrace();
		System.out.println("");
		String oldHeader = msg.getHeader();
		if (oldHeader == null || oldHeader.trim() == "") {
			return "reverse";
		} else {
			return "reverse, " + oldHeader;
		}
	}

	@Override
	public String getContent() {
		String result = alterString(msg.getContent().toLowerCase());
		return result;
	}

	private static String alterString(String message) {
		return (new StringBuilder(message).reverse().toString());
	}

	@Override
	public void setHeader(String newHeader) {
		msg.setHeader(newHeader);
	}

	public static String decrypt(String message) {
		System.out.println("original messge: " + message);
		System.out.println("reverse decryption result: " + alterString(message));
		return alterString(message);
	}
}
