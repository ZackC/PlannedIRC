package com.irc.project.Client.Framework.LogPlugin;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.irc.project.Client.Client;
import com.irc.project.Client.Framework.PlugInInterface;
import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Server.Server;
import com.irc.project.Server.ServerConnection;

public class LogPlugin implements PlugInInterface {

	TextArea outputArea = null;
	JButton printLogButton;
	ArrayList<String> log = new ArrayList<String>();

	@Override
	public void logInAction(Object type) {
		if (type instanceof Server) {
			JFrame frame = new JFrame("log output");
			frame.setLayout(new BorderLayout());
			outputArea = new TextArea();
			frame.add("Center", outputArea);
			printLogButton = new JButton("print server log");
			printLogButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (outputArea != null) {
						for (String line : log) {
							outputArea.append(line + "\n");
						}
					}

				}
			});
			frame.add("South", printLogButton);
			frame.pack();
			frame.setVisible(true);
		}

	}

	@Override
	public GeneralMessageType sendMessageAction(GeneralMessageType msg) {
		return msg;
	}

	@Override
	public GeneralMessageType recieveMessageAction(Object type,
			GeneralMessageType msg, ServerConnection sc) {
		return msg;
	}

	@Override
	public void drawSelectionOption(Panel panel, TextArea ta) {
		printLogButton = new JButton("print client log");
		outputArea = ta;
		printLogButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (outputArea != null) {
					for (String line : log) {
						outputArea.append(line + "\n");
					}
				}

			}
		});
		panel.add(printLogButton);
	}

	@Override
	public HashSet<ServerConnection> alterBroadCastGroup(
			HashSet<ServerConnection> connections) {
		return connections;
	}

	@Override
	public List<String> getHeaders() {
		return null;
	}

	@Override
	public void logAction(Object type, GeneralMessageType msg) {
		if (type instanceof Client) {
			log.add(msg.getContent());

		} else if (type instanceof Server) {
			log.add("Header: " + msg.getHeader() + "; Message: " + msg.getContent());
		}

	}

}
