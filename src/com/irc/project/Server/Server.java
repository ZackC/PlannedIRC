package com.irc.project.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.irc.project.Client.Framework.PlugInInterface;
import com.irc.project.Client.Framework.Authentication.AuthenticationPlugin;
import com.irc.project.Client.Framework.ColorPlugin.ColorPlugin;
import com.irc.project.Client.Framework.LogPlugin.LogPlugin;
import com.irc.project.Client.Framework.ReverseEnryptionPlugin.ReverseEncryptionPlugin;
import com.irc.project.Client.Framework.Rot13Plugin.Rot13EncryptionPlugin;
import com.irc.project.Message.GeneralMessageType;

/**
 * server's main class. accepts incoming ServerConnections and allows
 * broadcasting
 */
public class Server {

	public static void main(String args[]) throws IOException {
		if (args.length != 1)
			throw new RuntimeException("Syntax: ChatServer <port>");
		new Server(Integer.parseInt(args[0]));
	}

	/**
	 * array list of all plug-ins
	 */
	ArrayList<PlugInInterface> plugIns = new ArrayList<PlugInInterface>();
	HashMap<List<String>, PlugInInterface> headersOfPlugins = new HashMap<List<String>, PlugInInterface>();

	/**
	 * list of all known ServerConnections
	 */
	protected HashSet ServerConnections = new HashSet();

	/**
	 * awaits incoming ServerConnections and creates ServerConnection objects
	 * accordingly.
	 * 
	 * @param port
	 *          port to listen on
	 */
	public Server(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		// add the plugins to the system here
		plugIns.add(new AuthenticationPlugin());
		plugIns.add(new Rot13EncryptionPlugin());
		plugIns.add(new ReverseEncryptionPlugin());
		plugIns.add(new ColorPlugin());
		plugIns.add(new LogPlugin());
		for (PlugInInterface pi : plugIns) {
			headersOfPlugins.put(pi.getHeaders(), pi);
		}
		for (PlugInInterface pi : plugIns) {
			pi.logInAction(this);
		}
		while (true) {
			System.out.println("Waiting for ServerConnections...");
			Socket client = server.accept();
			System.out.println("Accepted from " + client.getInetAddress());
			ServerConnection c = connectTo(client);
			c.start();
		}
	}

	public ArrayList<PlugInInterface> getPlugIns() {
		return this.plugIns;
	}

	public HashMap<List<String>, PlugInInterface> getHeadersOfPlugins() {
		return headersOfPlugins;
	}

	/**
	 * creates a new ServerConnection for a socket
	 * 
	 * @param socket
	 *          socket
	 * @return the ServerConnection object that handles all further communication
	 *         with this socket
	 */
	public ServerConnection connectTo(Socket socket) {
		ServerConnection ServerConnection = new ServerConnection(socket, this);
		ServerConnections.add(ServerConnection);
		return ServerConnection;
	}

	/**
	 * send a message to all known ServerConnections
	 * 
	 * @param msg
	 *          the message
	 */
	public void broadcast(GeneralMessageType msg) {
		synchronized (ServerConnections) {
			HashSet latestConnections = (HashSet) ServerConnections.clone(); // doing
																																				// a
																																				// shallow
																																				// copy
			for (PlugInInterface pi : plugIns) {
				latestConnections = pi.alterBroadCastGroup(latestConnections);
			}
			if (latestConnections != null) {
				for (PlugInInterface pi : plugIns) {
					pi.logAction(this, msg);
				}
			}
			Iterator iterator = latestConnections.iterator();
			while (iterator.hasNext()) {
				ServerConnection ServerConnection = (ServerConnection) iterator.next();
				ServerConnection.send(msg);
			}
		}
	}

	/**
	 * remove a ServerConnection so that broadcasts are no longer sent there.
	 * 
	 * @param ServerConnection
	 *          ServerConnection to remove
	 */
	public void removeServerConnection(ServerConnection ServerConnection) {
		ServerConnections.remove(ServerConnection);
	}

}
