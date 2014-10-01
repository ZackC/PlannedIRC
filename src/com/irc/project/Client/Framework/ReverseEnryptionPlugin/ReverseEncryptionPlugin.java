package com.irc.project.Client.Framework.ReverseEnryptionPlugin;

import java.awt.Panel;
import java.awt.TextArea;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;

import com.irc.project.Client.Client;
import com.irc.project.Client.Framework.PlugInInterface;
import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Message.Message;
import com.irc.project.Server.ServerConnection;

public class ReverseEncryptionPlugin implements PlugInInterface {

	JCheckBox reverseEncyrptionCheckBox = null;

	@Override
	public void logInAction(Object type) {
		// not used in this plugin

	}

	@Override
	public GeneralMessageType sendMessageAction(GeneralMessageType msg) {
		if (reverseEncyrptionCheckBox != null
				&& reverseEncyrptionCheckBox.isSelected()) {
			System.out.println("adding reverse encyryption");
			GeneralMessageType resultMessage = new ReverseMessage(msg);
			System.out.println("reverse encrypted message: "
					+ resultMessage.getContent());
			System.out.println("reverse encrypted header: "
					+ resultMessage.getHeader());
			return resultMessage;
		} else {
			if (reverseEncyrptionCheckBox != null) {
				System.out.println(reverseEncyrptionCheckBox.isSelected());
			}
			return msg;
		}

	}

	@Override
	public GeneralMessageType recieveMessageAction(Object type,
			GeneralMessageType msg, ServerConnection sc) {
		if (type instanceof Client) {
			System.out.println("reverse header recieved: " + msg.getHeader());
			if (msg.getHeader().startsWith("reverse")) {
				if (msg.getHeader().length() > 9) {
					System.out.println("found reverse answers");
					String header = msg.getHeader().substring(9); // removing the
																												// "reverse, "
																												// encryption tag so the
																												// next tag can be
																												// handled.
					String content = ReverseMessage.decrypt(msg.getContent());
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
		resultList.add("reverse");
		return resultList;
	}

	@Override
	public void drawSelectionOption(Panel panel, TextArea ta) {
		reverseEncyrptionCheckBox = new JCheckBox("reverse encryption:");
		panel.add(reverseEncyrptionCheckBox);

	}

	@Override
	public void logAction(Object type, GeneralMessageType msg) {
		// doesn't do anything for this plugin

	}

}
