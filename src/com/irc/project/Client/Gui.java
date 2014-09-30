package com.irc.project.Client;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * simple AWT gui for the chat client
 */
public class Gui extends Frame implements ChatLineListener {

	private static final long serialVersionUID = 1L;

	protected TextArea outputTextbox;

	protected final TextField inputField;

	private Client chatClient;

	protected Panel optionFrame;

	/**
	 * creates layout
	 * 
	 * @param title
	 *          title of the window
	 * @param chatClient
	 *          chatClient that is used for sending and receiving messages
	 */
	public Gui(String title, final Client chatClient) {
		super(title);
		this.chatClient = chatClient;
		System.out.println("starting gui...");
		setLayout(new BorderLayout());
		outputTextbox = new TextArea();
		add("Center", outputTextbox);
		outputTextbox.setEditable(false);
		inputField = new TextField();
		add("South", inputField);
		optionFrame = new OptionsPanel(this);
		add("East", optionFrame);

		// register listener so that we are informed whenever a new chat message
		// is received (observer pattern)
		chatClient.addLineListener(this);
		// sends the message in the text box when enter is pressed if the text is
		// not empty
		inputField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!inputField.getText().trim().equals("")) {
						chatClient.send((String) inputField.getText());
						inputField.setText("");
					}
				}
			}
		});

		pack();
		setVisible(true);
		inputField.requestFocus();

	}

	public Client getClient() {
		return chatClient;
	}

	/**
	 * this method gets called every time a new message is received (observer
	 * pattern)
	 */
	public void newChatLine(String line) {
		outputTextbox.append(line);
	}

	/**
	 * handles AWT events (closing window)
	 */
	public boolean handleEvent(Event e) {

		if ((e.target == this) && (e.id == Event.WINDOW_DESTROY)) {
			if (chatClient != null)
				chatClient.stop();
			setVisible(false);
			System.exit(0);
			return true;
		}
		return super.handleEvent(e);
	}

}
