package com.clientServer;


import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class ClientTest {
	public static void main(String[] args) {
		Client msfata;
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			msfata = new Client(ip);
			msfata.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			msfata.startRunning();
		} catch (UnknownHostException e1) {
		         
		}
	}
}