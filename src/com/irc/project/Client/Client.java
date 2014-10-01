package com.irc.project.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.irc.project.Client.Framework.PlugInInterface;
import com.irc.project.Client.Framework.Authentication.AuthenticationPlugin;
import com.irc.project.Client.Framework.ColorPlugin.ColorPlugin;
import com.irc.project.Client.Framework.LogPlugin.LogPlugin;
import com.irc.project.Client.Framework.ReverseEnryptionPlugin.ReverseEncryptionPlugin;
import com.irc.project.Client.Framework.Rot13Plugin.Rot13EncryptionPlugin;
import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Message.Message;

/**
 * simple chat client
 */
public class Client implements Runnable {
	public static void main(String args[]) throws IOException {
		if (args.length != 2)
			throw new RuntimeException("Syntax: ChatClient <host> <port>");

		Client client = new Client(args[0], Integer.parseInt(args[1]));

	}

	protected ObjectInputStream inputStream;

	protected ObjectOutputStream outputStream;

	protected Thread thread;

	protected Gui g;

	/**
	 * array list of all plug-ins
	 */
	ArrayList<PlugInInterface> plugIns = new ArrayList<PlugInInterface>();

	HashMap<List<String>, PlugInInterface> headersOfPlugins = new HashMap<List<String>, PlugInInterface>();

	public Client(String host, int port) {
		// add the plugins to the system here
		plugIns.add(new AuthenticationPlugin());
		plugIns.add(new Rot13EncryptionPlugin());
		plugIns.add(new ReverseEncryptionPlugin());
		plugIns.add(new ColorPlugin());
		plugIns.add(new LogPlugin());

		try {

			System.out.println("Connecting to " + host + " (port " + port + ")...");
			Socket s = new Socket(host, port);
			this.outputStream = new ObjectOutputStream((s.getOutputStream()));
			this.inputStream = new ObjectInputStream((s.getInputStream()));
			for (PlugInInterface pi : plugIns) {
				if (pi.getHeaders() != null) {
					headersOfPlugins.put(pi.getHeaders(), pi);
				}
			}
			for (PlugInInterface pi : plugIns) {
				pi.logInAction(this);
			}
			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		g = new Gui("Chat " + host + ":" + new Integer(port).toString(), this);
	}

	public Gui getGui() {
		return g;
	}

	public ObjectInputStream getInputStream() {
		return this.inputStream;
	}

	/**
	 * main method. waits for incoming messages.
	 */
	@Override
	public void run() {
		try {
			while (true) {
				try {
					Object msg = inputStream.readObject();
					handleIncomingMessage(msg);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			thread = null;
			try {
				outputStream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * decides what to do with incoming messages
	 * 
	 * @param msg
	 *          the message (Object) received from the sockets
	 */
	protected void handleIncomingMessage(Object msg) {
		if (msg instanceof GeneralMessageType) {
			String currentHeader = ((GeneralMessageType) msg).getHeader();
			System.out.println("received message with header: " + currentHeader);
			int pos = 0;
			while (pos != -1) {
				pos = currentHeader.indexOf(',');
				String frontOfCurrentHeader;
				if (pos != -1) {
					frontOfCurrentHeader = currentHeader.substring(0, pos);
				} else {
					frontOfCurrentHeader = currentHeader;
				}
				System.out.println("front of current header: " + frontOfCurrentHeader);
				System.out.println("original header: "
						+ ((GeneralMessageType) msg).getHeader());
				if (!frontOfCurrentHeader.equals("msg")) {
					for (List<String> headerList : headersOfPlugins.keySet()) {
						System.out.println("List" + headerList.toString());
						System.out.println("front: " + frontOfCurrentHeader);
						for (String header : headerList) {
							if (frontOfCurrentHeader.startsWith(header)) {
								System.out.println("found match");
								// assuming headers don't conflict at the moment
								PlugInInterface pi = headersOfPlugins.get(headerList);
								msg = pi.recieveMessageAction(this, (GeneralMessageType) msg,
										null);
							}
						}
					}
				}
				if (pos != -1) {
					currentHeader = currentHeader.substring(pos + 2);
				}

			}
		}
		if (msg instanceof Message) {
			for (PlugInInterface pi : plugIns) {
				pi.logAction(this, (Message) msg);
			}
			fireAddLine(((Message) msg).getContent() + "\n");
		}
	}

	public void send(String line) {
		send(new Message("msg", line)); // will need to change this header later
	}

	public void send(GeneralMessageType msg) {
		for (PlugInInterface pi : plugIns) {
			msg = pi.sendMessageAction(msg);
		}
		try {
			System.out.println("sending message with header: " + msg.getHeader());
			System.out.println("sending message with content: " + msg.getContent());
			outputStream.writeObject(msg);
			outputStream.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
			thread.stop();
		}
	}

	/**
	 * listener-list for the observer pattern
	 */
	private ArrayList listeners = new ArrayList();

	/**
	 * addListner method for the observer pattern
	 */
	public void addLineListener(ChatLineListener listener) {
		listeners.add(listener);
	}

	/**
	 * removeListner method for the observer pattern
	 */
	public void removeLineListener(ChatLineListener listner) {
		listeners.remove(listner);
	}

	/**
	 * fire Listner method for the observer pattern
	 */
	public void fireAddLine(String line) {
		for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
			ChatLineListener listener = (ChatLineListener) iterator.next();
			listener.newChatLine(line);
		}
	}

	public void stop() {
		thread.stop();
	}

}
