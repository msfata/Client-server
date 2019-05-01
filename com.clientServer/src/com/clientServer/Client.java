package com.clientServer;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;

	// constructor
	public Client(String host) {
		super("Client");
		getContentPane().setBackground(Color.BLACK);
		setResizable(false);
		setType(Type.UTILITY);
		setBackground(Color.WHITE);
		setTitle("By MOHAMMED SHAFIQ FATA (CLIENT)");
		serverIP = host;
		userText = new JTextField();
		userText.setBackground(Color.WHITE);
		userText.setBounds(0, 0, 633, 42);
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
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 42, 633, 244);
		getContentPane().add(scrollPane);
		chatWindow = new JTextArea();
		chatWindow.setForeground(Color.BLACK);
		chatWindow.setBorder(null);
		chatWindow.setBackground(Color.WHITE);
		chatWindow.setLineWrap(true);
		chatWindow.setFont(new Font("Monospaced", Font.PLAIN, 15));
		scrollPane.setViewportView(chatWindow);

		JButton btnExit_1 = new JButton("Exit");
		btnExit_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit_1.setBounds(505, 299, 97, 25);
		getContentPane().add(btnExit_1);
		setBounds(0, 0, 639, 376); // Sets the window size
		setVisible(true);
	}

	// connect to server
	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		} catch (EOFException eofException) {
			showMessage("\n Client terminated the connection");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
//		finally {
//			if (message.equalsIgnoreCase("")) {
//				closeConnection();
//			}
//		}
	}

	// connect to server
	private void connectToServer() throws IOException {
		try {
			showMessage("Attempting connection... \n");
			connection = new Socket(InetAddress.getByName(serverIP), 5000);
			showMessage("Connection Established! Connected to: " + connection.getInetAddress().getHostName());
		} catch (Exception e) {
		}
	}

	// set up streams
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n The streams are now set up! \n");
	}

	// while chatting with server
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("Unknown data received!");
			}
		} while (!message.equals("SERVER - END"));
	}

	// send message to server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		} catch (IOException ioException) {
		}
	}

	// update chat window
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(message);
			}
		});
	}

	// allows user to type
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(tof);
			}
		});
	}
}
