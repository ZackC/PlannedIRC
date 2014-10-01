package com.irc.project.Framework.ColorPlugin;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.irc.project.Client.Client;
import com.irc.project.Framework.PlugInInterface;
import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Message.Message;
import com.irc.project.Server.ServerConnection;

public class ColorPlugin implements PlugInInterface {

	JComboBox<String> fontColor = null;
	JComboBox<String> backgroundColor;

	@Override
	public void logInAction(Object type) {
		// doesn't do anything for this plugin

	}

	@Override
	public GeneralMessageType sendMessageAction(GeneralMessageType msg) {
		if (fontColor != null) {
			String oldHeader = msg.getHeader();
			String newHeader;
			if (oldHeader == null || oldHeader.trim() == "") {
				newHeader = "font=" + ((String) fontColor.getSelectedItem())
						+ ", background=" + ((String) backgroundColor.getSelectedItem());
			} else {
				newHeader = "font=" + ((String) fontColor.getSelectedItem())
						+ ", background=" + ((String) backgroundColor.getSelectedItem())
						+ ", " + oldHeader;
			}
			msg = new Message(newHeader, msg.getContent());
			System.out.println("new color header: " + newHeader);
		}
		return msg;
	}

	@Override
	public GeneralMessageType recieveMessageAction(Object type,
			GeneralMessageType msg, ServerConnection sc) {
		if (type instanceof Client) {
			String header = msg.getHeader();
			System.out.println("color plugin received header: " + header);

			if (header.startsWith("font=")) {
				int pos = header.indexOf(',');
				String fontColorString = header.substring(5, pos);
				System.out.println("font color selection: " + fontColorString);
				// change font color in gui
				Color fontColor = getColor(fontColorString);
				((Client) type).getGui().getTextArea().setForeground(fontColor);
				msg.setHeader(header.substring(pos + 2));
			} else if (header.startsWith("background=")) {
				int pos = header.indexOf(',');
				String backgroundColorString = header.substring(11, pos);
				System.out.println("background color selection: "
						+ backgroundColorString);
				// change background in gui
				Color backgroundColor = getColor(backgroundColorString);
				((Client) type).getGui().getTextArea().setBackground(backgroundColor);
				msg.setHeader(header.substring(pos + 2));
			}
		}
		return msg;
	}

	private Color getColor(String colorString) {
		if (colorString.equals("white")) {
			return Color.WHITE;
		} else if (colorString.equals("black")) {
			return Color.BLACK;
		} else if (colorString.equals("red")) {
			return Color.RED;
		} else if (colorString.equals("green")) {
			return Color.GREEN;
		} else if (colorString.equals("blue")) {
			return Color.BLUE;
		}
		return null;
	}

	@Override
	public void drawSelectionOption(Panel panel, TextArea ta) {
		String[] selectableColors = { "black", "white", "red", "green", "blue" };
		fontColor = new JComboBox<String>(selectableColors);
		fontColor.setSelectedIndex(0);
		backgroundColor = new JComboBox<String>(selectableColors);
		backgroundColor.setSelectedIndex(1);
		Panel fontColorPanel = new Panel();
		fontColorPanel.setLayout(new FlowLayout());
		fontColorPanel.add(new JLabel("Font Color:"));
		fontColorPanel.add(fontColor);
		Panel backgroundColorPanel = new Panel();
		backgroundColorPanel.setLayout(new FlowLayout());
		backgroundColorPanel.add(new JLabel("Background Color:"));
		backgroundColorPanel.add(backgroundColor);
		panel.add(fontColorPanel);
		panel.add(backgroundColorPanel);

	}

	@Override
	public HashSet<ServerConnection> alterBroadCastGroup(
			HashSet<ServerConnection> connections) {
		return connections; // makes no change
	}

	@Override
	public List<String> getHeaders() {
		List<String> headers = new LinkedList<String>();
		headers.add("font=");
		headers.add("background=");
		return headers;
	}

	@Override
	public void logAction(Object type, GeneralMessageType msg) {
		// doesn't do anything for this plugin

	}

}
