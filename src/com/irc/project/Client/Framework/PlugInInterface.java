package com.irc.project.Client.Framework;

import java.awt.Panel;
import java.util.HashSet;
import java.util.List;

import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Server.ServerConnection;

public interface PlugInInterface {

	// type is a client or server to let the system
	// know which side of the system the client is checking
	public abstract void logInAction(Object type);

	public abstract GeneralMessageType sendMessageAction(GeneralMessageType msg);

	// type is for how to handle the client or server differently.
	// msg is the received message
	public abstract GeneralMessageType recieveMessageAction(Object type,
			GeneralMessageType msg, ServerConnection sc);

	public abstract void drawSelectionOption(Panel panel);

	public abstract HashSet<ServerConnection> alterBroadCastGroup(
			HashSet<ServerConnection> connections);

	// public abstract void getSelectionOption();

	// this is the list of headers the plug handles
	public List<String> getHeaders();

}
