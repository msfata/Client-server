package com.clientServer;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Server extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private JButton btnExit;
	private int port = 5000;
	private JTextField tfActivePort;
	private JLabel lblActinvePort;
	@SuppressWarnings("unused")
	private JButton btnChangePort;

	// constructor
	public Server() {
		super("BY MSFATA");

		getContentPane().setBackground(Color.BLACK);
		setResizable(false);
		setType(Type.UTILITY);
		setBackground(Color.BLACK);
		setTitle("BY MOHAMMED SHAFIQ FATA (SERVER)");
		userText = new JTextField();
		userText.setBounds(0, 0, 670, 49);
		userText.setBackground(Color.WHITE);
		userText.setForeground(Color.BLACK);
		userText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		getContentPane().setLayout(null);
		getContentPane().add(userText);
		chatWindow = new JTextArea();
		chatWindow.setBorder(null);
		chatWindow.setBackground(Color.WHITE);
		chatWindow.setFont(new Font("Monospaced", Font.PLAIN, 15));
		chatWindow.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(chatWindow);
		scrollPane.setBounds(0, 50, 670, 276);
		getContentPane().add(scrollPane);

		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setBounds(10, 345, 97, 25);
		getContentPane().add(btnExit);

		tfActivePort = new JTextField();
		tfActivePort.setBounds(528, 339, 130, 36);
		getContentPane().add(tfActivePort);
		tfActivePort.setColumns(10);

		lblActinvePort = new JLabel("Actinve Port");
		lblActinvePort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblActinvePort.setForeground(Color.WHITE);
		lblActinvePort.setBounds(426, 339, 90, 33);
		getContentPane().add(lblActinvePort);

		
		setBounds(650, 0, 676, 419); // Sets the window size
//		setUndecorated(true);
		setVisible(true);

	}

	public void startRunning() {
		try {

			server = new ServerSocket(port, 10); // 6789 is a dummy port for testing, this can be changed. The 100 is
			tfActivePort.setText("" + port);
			while (true) {
				try {
					// Trying to connect and have conversation
					waitForConnection();
					setupStreams();
					whileChatting();
				} catch (EOFException eofException) {
					showMessage("\n Server ended the connection! ");
				} finally {
					closeConnection(); // Changed the name to something more appropriate
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// wait for connection, then display connection information
	private void waitForConnection() throws IOException {
		showMessage(" Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage(" Now connected to " + connection.getInetAddress().getHostName());
	}

	// get stream to send and receive data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();

		input = new ObjectInputStream(connection.getInputStream());

		showMessage("\n Streams are now setup \n");
	}

	// during the chat conversation
	private void whileChatting() throws IOException {
		String message = " You are now connected! ";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("The user has sent an unknown object!");
			}
		} while (!message.equals("CLIENT - END"));
	}

	public void closeConnection() {
		showMessage("\n Closing Connections... \n");
		ableToType(false);
		try {
			output.close(); // Closes the output path to the client
			input.close(); // Closes the input path to the server, from the client.
			connection.close(); // Closes the connection between you can the client
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// Send a mesage to the client
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER -" + message);
		} catch (IOException ioException) {
			chatWindow.append("\n ERROR: CANNOT SEND MESSAGE, PLEASE RETRY");
		}
	}

	// update chatWindow
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(text);
			}
		});
	}

	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(tof);
			}
		});
	}
}