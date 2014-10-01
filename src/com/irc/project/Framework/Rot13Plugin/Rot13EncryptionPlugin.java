package com.irc.project.Framework.Rot13Plugin;

import java.awt.Panel;
import java.awt.TextArea;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;

import com.irc.project.Client.Client;
import com.irc.project.Framework.PlugInInterface;
import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Message.Message;
import com.irc.project.Server.ServerConnection;

public class Rot13EncryptionPlugin implements PlugInInterface {

	JCheckBox rot13EncyrptionCheckBox = null;

	@Override
	public void logInAction(Object type) {
		// not used in this plugin

	}

	@Override
	public GeneralMessageType sendMessageAction(GeneralMessageType msg) {
		if (rot13EncyrptionCheckBox != null && rot13EncyrptionCheckBox.isSelected()) {
			System.out.println("adding rot13 encyryption");
			GeneralMessageType resultMessage = new Rot13Message(msg);
			System.out.println("rot13 encrypted message: "
					+ resultMessage.getContent());
			System.out
					.println("rot13 encrypted header: " + resultMessage.getHeader());
			return resultMessage;
		} else {
			System.out.println("not using rot13 encryption");
			return msg;
		}

	}

	@Override
	public GeneralMessageType recieveMessageAction(Object type,
			GeneralMessageType msg, ServerConnection sc) {
		if (type instanceof Client) {
			System.out.println("rot13 header recieved: " + msg.getHeader());
			if (msg.getHeader().startsWith("rot13")) {
				if (msg.getHeader().length() > 7) {
					System.out.println("found rot13 answers");
					String header = msg.getHeader().substring(7); // removing the
																												// "rot13, "
																												// encryption tag so the
																												// next tag can be
																												// handled.
					String content = Rot13Message.decrypt(msg.getContent());
					return (new Message(header, content));
				}
			}
		}
		return msg;

	}

	@Override
	public HashSet<ServerConnection> alterBroadCastGroup(
			HashSet<ServerConnection> connections) {
		// TODO Auto-generated method stub
		return connections;
	}

	@Override
	public List<String> getHeaders() {
		List<String> resultList = new LinkedList<String>();
		resultList.add("rot13");
		return resultList;
	}

	@Override
	public void drawSelectionOption(Panel panel, TextArea ta) {
		rot13EncyrptionCheckBox = new JCheckBox("rot13 encryption:");
		panel.add(rot13EncyrptionCheckBox);

	}

	@Override
	public void logAction(Object type, GeneralMessageType msg) {
		// doesn't do anything for this plugin

	}

}
