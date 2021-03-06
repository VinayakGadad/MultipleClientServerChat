/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multipleclientchat;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Vinayak
 */
public class MultipleServerChat extends Frame{
	// Text area for displaying contents
  private JTextArea jta = new JTextArea();

  // Mapping of sockets to output streams
  private Hashtable Streams = new Hashtable();

  // Server socket
  private ServerSocket serverSocket;

  public static void main(String[] args) {
    new MultipleServerChat();
  }

  public MultipleServerChat() {
    // Place text area on the frame
    setLayout(new BorderLayout());
    add(new JScrollPane(jta), BorderLayout.CENTER);

    setTitle("Multi Chat Server");
    setSize(500, 300);
//    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Center the frame
    setVisible(true); // It is necessary to show the frame here!

    jta.setEditable(false); // Disable editing of server log

    // Listen for connections
    listen();
  }

  private void listen() {
      try {
        // Create a server socket
        serverSocket = new ServerSocket(8000);
        jta.append("Multi Thread Server started at " + new Date() + '\n');

      while (true) {
        // Listen for a new connection request
        Socket socket = serverSocket.accept();

        // Display the client number
        jta.append("Connection from " + socket + " at " + new Date() + '\n');

        // Create output stream
        DataOutputStream dataout = new DataOutputStream(socket.getOutputStream());

        // Save output stream to hashtable
        Streams.put(socket, dataout);

        // Create a new thread for the connection
        new ServerThread(this, socket);
      }
    }
    catch(IOException ex) {
      System.err.println(ex);
    }
  }

  // Used to get the output streams
  Enumeration getOutputStreams(){
      return Streams.elements();
  }

  // Used to send message to all clients
  void sendToAll(String message){
      // Go through hashtable and send message to each output stream
      for (Enumeration e = getOutputStreams(); e.hasMoreElements();){
          DataOutputStream dout = (DataOutputStream)e.nextElement();
          try {
              // Write message
              dout.writeUTF(message);
          } catch (IOException ex) {
              System.err.println(ex);
          }
      }
  }


  class ServerThread extends Thread {

      private MultipleServerChat server;

      private Socket socket;

      /** Construct a thread */
      public ServerThread(MultipleServerChat server, Socket socket) {
          this.socket = socket;
          this.server = server;
          start();
      }

      /** Run a thread */
      public void run() {
          try {
              // Create data input and output streams
              DataInputStream din = new DataInputStream(socket.getInputStream());

              // Continuously serve the client
              while (true) {
                  String string = din.readUTF();

                  // Send text back to the clients
                  server.sendToAll(string);

                  // Add chat to the server jta
                  jta.append(string + '\n');
              }
          }
          catch(IOException e) {
              System.err.println(e);
          }
      }
  }
    
}
