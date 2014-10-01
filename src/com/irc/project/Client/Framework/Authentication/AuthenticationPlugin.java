package com.irc.project.Client.Framework.Authentication;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.irc.project.Client.Client;
import com.irc.project.Client.Framework.PlugInInterface;
import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Message.Message;
import com.irc.project.Server.Server;
import com.irc.project.Server.ServerConnection;

public class AuthenticationPlugin implements PlugInInterface {

	private String password = null;
	private HashSet<ServerConnection> autheticatedConnections = new HashSet<ServerConnection>();

	@Override
	public void logInAction(Object type) {
		if (type instanceof Server) {
			JFrame frame = new JFrame("Enter Client Password");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(new FlowLayout());
			frame.getContentPane().add(new JLabel("Enter Client's password"));
			final JTextField passwordField = new JTextField();
			passwordField.setPreferredSize(new Dimension(100, 30));
			;
			frame.getContentPane().add(passwordField);
			passwordField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent evt) {
					if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
						if (!passwordField.getText().trim().equals("")) {
							password = passwordField.getText();
							passwordField.setText("");
						}
					}
				}
			});
			frame.setSize(300, 40);
			frame.pack();
			frame.setVisible(true);
			while (password == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			frame.setVisible(false);
			frame.dispose();
		} else if (type instanceof Client) {
			JFrame frame = new JFrame("Enter Server Password");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(new FlowLayout());
			frame.getContentPane().add(new JLabel("Enter Server's password"));
			final JTextField checkPasswordField = new JTextField();
			checkPasswordField.setPreferredSize(new Dimension(150, 30));
			frame.getContentPane().add(checkPasswordField);
			checkPasswordField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent evt) {
					if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
						if (!checkPasswordField.getText().trim().equals("")) {
							System.out.println("client entered a possible password");
							password = checkPasswordField.getText();
							checkPasswordField.setText("");
						}
					}
				}
			});
			frame.setSize(300, 60);
			frame.pack();
			frame.setVisible(true);
			boolean correctPassword = false;
			while (!correctPassword) {
				while (password == null) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("the entered password is: " + password);
				// need to send this to the server and have it check for users
				((Client) type).send(new Message("authentication", password));
				System.out.println("sent message");
				Object msg = null;
				while (msg == null) {
					try {
						msg = ((Client) type).getInputStream().readObject();
						System.out.println("received response");
					} catch (Exception e) {
					}
					if (msg == null) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				System.out.println(msg instanceof Message);
				System.out
						.println(((Message) msg).getHeader().equals("authentication"));
				System.out.println(((Message) msg).getContent().trim().equals("valid"));
				System.out.println(((Message) msg).getHeader());
				System.out.println(((Message) msg).getContent());
				if ((msg instanceof Message)
						&& ((Message) msg).getHeader().equals("authentication")) {
					if (((Message) msg).getContent().trim().equals("valid")) {
						correctPassword = true;
						System.out.println("was authenticated by server");
						frame.setVisible(false);
						frame.dispose();
					} else {

						password = null;
					}
				} else {
					msg = null;
				}
			}

		} else {
			new Exception("invalid component for authetication");
		}

	}

	@Override
	public GeneralMessageType recieveMessageAction(Object type,
			GeneralMessageType msg, ServerConnection sc) {
		if (type instanceof Server) {
			System.out.println("check 1" + msg.getHeader());
			if (msg.getHeader().equals("authentication")) {
				System.out.println("check 2:");
				if (msg.getContent().equals(password)) {
					System.out.println("check 3");
					autheticatedConnections.add(sc);
					System.out.println("authenticated client");
					sc.send(new Message("authentication", "valid"));
					System.out.println("Server sent confirmation message");
					return null;
				}
			}
		}
		return msg;

	}

	@Override
	public HashSet<ServerConnection> alterBroadCastGroup(
			HashSet<ServerConnection> connections) {
		return autheticatedConnections;

	}

	@Override
	public GeneralMessageType sendMessageAction(GeneralMessageType msg) {
		return msg;
	}

	@Override
	public List<String> getHeaders() {
		List<String> resultList = new LinkedList<String>();
		resultList.add("authentication");

		return resultList;
	}

	@Override
	public void drawSelectionOption(Panel panel, TextArea ta) {
		// not used in this plugin

	}

	@Override
	public void logAction(Object type, GeneralMessageType msg) {
		// not used in this plugin
	}

}
