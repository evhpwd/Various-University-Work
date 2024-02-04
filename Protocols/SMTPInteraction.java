/************************************
 * Filename:  SMTPInteraction.java
 ************************************/
import java.net.*;
import java.io.*;
import java.util.*;

/*
 * Open an SMTP connection to mailserver and send one mail.
 */

public class SMTPInteraction {

    /* Socket to the SMTP server */
    private Socket connection;

    /* Streams for reading from and writing to socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPInteraction object. Create the socket and the 
       associated streams. Initialise SMTP connection. */
    public SMTPInteraction(EmailMessage mailmessage) throws IOException {
        // Open a TCP client socket with hostname and portnumber specified in
        // mailmessage.DestHost and  mailmessage.DestHostPort, respectively.
	    connection = new Socket(mailmessage.DestHost, mailmessage.DestHostPort);
	
        // attach the BufferedReader fromServer to read from the socket and
        // the DataOutputStream toServer to write to the socket
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    toServer =  new DataOutputStream(connection.getOutputStream());
        
        /* Read one line from server and check that the reply code is 220.
        If not, throw an IOException. */
        if (!fromServer.readLine().startsWith("220")) {
            throw new IOException("Reply code is not 220");
        }

        /* SMTP handshake. We need the name of the local machine.
        Send the appropriate SMTP handshake command. */
        String localhost = InetAddress.getLocalHost().getHostName();
        sendCommand("HELO " + localhost, 250);

        isConnected = true;
    }

    /* Send message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(EmailMessage mailmessage) throws IOException {

	/* Send all the necessary commands to send a message. Call
	   sendCommand() to do the dirty work. Do _not_ catch the
	   exception thrown from sendCommand(). */
        sendCommand("MAIL FROM: <" + mailmessage.Sender + ">", 250);
        sendCommand("RCPT TO: <" + mailmessage.Recipient + ">", 250);
        sendCommand("DATA", 354);
        toServer.writeBytes(mailmessage.Headers + CRLF);
        toServer.writeBytes(CRLF);
        toServer.writeBytes(mailmessage.Body + CRLF);
        toServer.writeBytes("." + CRLF);
    }

    /* Close SMTP connection. First, terminate on SMTP level, then
       close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT", 221);
            connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send the SMTP command to the server. Check that the reply code is
       what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        toServer.writeBytes(command + CRLF);
        if (!fromServer.readLine().startsWith(Integer.toString(rc))) {
            throw new IOException("Incorrect reply code");
        }
    }
} 
