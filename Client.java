package lilja.kiiski.gomoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

public class Client {
	public Socket socket;
	public BufferedReader br;
	public BufferedWriter bw;
	public Main main;

   	public boolean connected = false;

    	public Client(Main main) {
		this.main = main;
		try {
                	socket = new Socket("10.0.0.185", 1234);
                	br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                	bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                	connected = true;
	       	} catch (IOException e) {
                  	System.out.println("COULD NOT CONNECT TO SERVER");
                }
	}

    	public void sendMessage(String message) {
		try {
    			bw.write(message);
			bw.newLine();
                	bw.flush();
                } catch (IOException e) {
			System.out.println("ERROR SENDING MESSAGE");
                    	e.printStackTrace();
                }
    	}
}
