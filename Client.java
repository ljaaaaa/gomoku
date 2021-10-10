package lilja.kiiski.candyclicker;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

/*
CLIENT CLASS
- Acts as client to server run on eclipse/terminal
- Uses NetworkPage for locks and conditions
 */
public class Client implements Parcelable {
    Socket socket;
    BufferedReader br;
    BufferedWriter bw;
    NetworkPage thisPage;

    boolean initialized = false;
    boolean sentMessage = false;
    boolean connected = false;

    public Client(NetworkPage thisPage) {
        this.thisPage = thisPage;

        new Thread(new Runnable(){ //Initializes stuff
            @Override
            public void run() {
                try {
                    thisPage.lock.lock();
                    socket = new Socket("10.0.0.185", 2345);
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    connected = true;
                } catch (IOException e) { //Server not available
                    System.out.println("COULD NOT CONNECT TO SERVER");
                } finally {
                    initialized = true;
                    thisPage.clientInitialized.signalAll();
                    thisPage.lock.unlock();
                }
            }
        }).start();
    }

    public void sendMessage(String message) { //Sends a message
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    thisPage.lock.lock();
                    bw.write(message);
                    bw.newLine();
                    bw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    sentMessage = true;
                    thisPage.sentMessage.signalAll();
                    thisPage.lock.unlock();
                }
            }
        }).start();
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() { //Stuff for parcelable below
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    protected Client(Parcel in) {
        initialized = in.readByte() != 0;
        sentMessage = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (initialized ? 1 : 0));
        dest.writeByte((byte) (sentMessage ? 1 : 0));
    }
}