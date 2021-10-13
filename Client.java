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

	public boolean initialized = false;
   	public boolean sentMessage = false;
   	public boolean connected = false;

    	public Client(Main main) {
		this.main = main;
		try {
			main.lock.lock();
                	socket = new Socket("10.0.0.185", 2345);
                	br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                	bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                	connected = true;
	       	} catch (IOException e) {
                  	System.out.println("COULD NOT CONNECT TO SERVER");
			e.printStackTrace();
                } finally {
                	initialized = true;
                    	main.clientInitialized.signalAll();
                    	main.lock.unlock();
                }
	}

    	public void sendMessage(String message) {
		try {
        		main.lock.lock();
    			bw.write(message);
			bw.newLine();
                	bw.flush();
                } catch (Exception e) {
			System.out.println("ERROR SENDING MESSAGE"); //
                    	e.printStackTrace();
                } finally {
                    	sentMessage = true;
                    	main.sentMessage.signalAll();
                    	main.lock.unlock();
                }
    	}
}
