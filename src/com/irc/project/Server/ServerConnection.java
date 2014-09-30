package com.irc.project.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.irc.project.Message.GeneralMessageType;
import com.irc.project.Message.Message;

/**
 * class for an individual connection to a client. allows to send messages to
 * this client and handles incoming messages.
 */
public class ServerConnection extends Thread {
	protected Socket socket;

	protected ObjectInputStream inputStream;

	protected ObjectOutputStream outputStream;

	private Server server;
	private boolean connectionOpen = true;

	public ServerConnection(Socket s, Server server) {
		this.socket = s;
		try {
			inputStream = new ObjectInputStream((s.getInputStream()));
			outputStream = new ObjectOutputStream((s.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.server = server;
	}

	/**
	 * waits for incoming messages from the socket
	 */
	public void run() {
		String clientName = socket.getInetAddress().toString();
		try {
			server.broadcast(new Message("msg", clientName + " has joined."));
			while (connectionOpen) {
				try {
					System.out.println("waiting to recieve object");
					Object msg = inputStream.readObject();
					System.out.println("class type of message: "
							+ msg.getClass().toString());
					handleIncomingMessage(clientName, msg);

				} catch (ClassNotFoundException e) {
					// e.printStackTrace();
				}

			}
		} catch (IOException ex) {
			// ex.printStackTrace();
		} finally {
			server.removeServerConnection(this);
			server.broadcast(new Message("msg", clientName + " has left."));
			try {
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * decides what to do with incoming messages
	 * 
	 * @param name
	 *          name of the client
	 * @param msg
	 *          received message
	 */
	private void handleIncomingMessage(String name, Object msg) {
		if (msg instanceof GeneralMessageType)
			server.broadcast((GeneralMessageType) msg);
	}

	/**
	 * sends a message to the client
	 * 
	 * @param line
	 *          text of the message
	 */
	public void send(String line) {
		send(new Message("msg", line)); // will need to change this header later
	}

	public void send(GeneralMessageType msg) {
		try {
			synchronized (outputStream) {
				outputStream.writeObject(msg);
			}
			outputStream.flush();
		} catch (IOException ex) {
			connectionOpen = false;
		}

	}

}
