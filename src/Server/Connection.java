package Server;


import java.io.*;
import java.net.*;

class Connection implements Runnable {

  ChatServer server = null;
  private Socket communicationSocket = null;
  private OutputStreamWriter out = null;
  private BufferedReader in = null;

  public Connection(ChatServer server, Socket s) {
    this.server = server;
    this.communicationSocket = s;
  }       

  public void sendMessage(String message) {
    try {
      out.write(message);
      out.flush();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {
    OutputStream socketOutput = null;
    InputStream socketInput = null;
    String magic = server.getMagicPassphrase();

    try {
      socketOutput = communicationSocket.getOutputStream();
      out = new OutputStreamWriter(socketOutput);
      socketInput = communicationSocket.getInputStream();
      in = new BufferedReader(new InputStreamReader(socketInput));

      String welcome = "Connection successful \nSay hello to everybody!\n";
   
      server.sendToAllClients(welcome);
      System.out.println("Connection made ");
      sendMessage("Welcome the passphrase is "+magic+"\n");
      String input = null;

      while ((input = in.readLine()) != null) {
	if (input.indexOf(magic) != -1) {
	  server.playMagicSound();
	  sendMessage("Congratulations, you sent the passphrase!\n");
	} else {
	  server.sendToAllClients(input+"\n");
	}
      }
    } catch(Exception e) {
      e.printStackTrace(System.err);
    } finally {
      try {
	if (in != null) in.close();
	if (out != null) out.close();
	communicationSocket.close();
      }  catch(Exception e) {
	e.printStackTrace();
      }
      server.closeConnection(this);
    }
  }
}