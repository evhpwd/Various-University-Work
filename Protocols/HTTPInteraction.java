/*************************************
 *  Filename:  HTTPInteraction.java
 **+***+******************************/

import java.net.*;
import java.io.*;
import java.util.*;

/*
 * Class for downloading one object from a Http server.
 */

public class HTTPInteraction {
	private String host;
	private String path;
	private String requestMessage;
	
		
	private static final int HTTP_PORT = 80;
	private static final String CRLF = "\r\n";
	private static final int BUF_SIZE = 4096; 
	private static final int MAX_OBJECT_SIZE = 102400;

 	/* Create a HTTPInteraction object. */
	public HTTPInteraction(String url) {
		
		/* Split "URL" into "host name" and "path name", and
		 * set host and path class variables. 
		 * if the URL is only a host name, use "/" as path 
		 */		
		if (url.contains("/")) {
			String[] host_and_path = url.split("/", 2);
			host = host_and_path[0];
			path = "/" + host_and_path[1];
		} else {
			host = url;
			path = "/";
		}

	
		/* Construct requestMessage, add a header line so that
		 * server closes connection after one response. */		
		requestMessage = "GET " + path + " HTTP/1.1" + CRLF;
		requestMessage += "Host: " + host + CRLF;
		requestMessage += "Connection: close" + CRLF;
		requestMessage += CRLF;
	}
	
	
	/* Send HTTP request, parse response and return requested object 
	 * as a String (if no errors), 
	 * otherwise return meaningful error message. 
	 * Don't catch Exceptions. EmailClient will handle them. */		
	public String send() throws IOException {
		
		/* buffer to read object in 4kB chunks */
		char[] buf = new char[BUF_SIZE];

		/* Maximum size of object is 100kB, which should be enough for most objects. 
		 * Change constant if you need more. */		
		char[] body = new char[MAX_OBJECT_SIZE];
		
		String statusLine="";	// status line
		int status;		// status code
		String headers="";	// headers
		int bodyLength=-1;	// lenghth of body
				
		String[] tmp;
		
		/* The socket to the server */
		Socket connection;
		
		/* Streams for reading from and writing to socket */
		BufferedReader fromServer;
		DataOutputStream toServer;
		
		System.out.println("Connecting server: " + host+CRLF);
		
		/* Connect to http server on port 80.
		 * Assign input and output streams to connection. */		
		connection = new Socket(host, 80);
		fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		toServer = new DataOutputStream(connection.getOutputStream());
		
		System.out.println("Send request:\n" + requestMessage);

		/* Send requestMessage to http server */
		toServer.writeBytes(requestMessage);
		
		/* Read the status line from response message */
		statusLine = fromServer.readLine();
		System.out.println("Status Line:\n"+statusLine+CRLF);
		
		/* Extract status code from status line. If status code is not 200,
		 * close connection and return an error message. 
		 * Do NOT throw an exception */
		if (!statusLine.startsWith("HTTP/1.1 200")) {
			connection.close();;
			return "ERROR: Status code was not 200";
		}

		/* Read header lines from response message, convert to a string, 
 		 * and assign to "headers" variable. 
		 * Recall that an empty line indicates end of headers.
		 * Extract length  from "Content-Length:" (or "Content-length:") 
		 * header line, if present, and assign to "bodyLength" variable. 
		*/
		while (true) {
			String line = fromServer.readLine();
			if (line.isEmpty()) break;
			headers += line;
			if (line.toLowerCase().startsWith("content-length")) {
				String value = line.split(": ")[1];
				bodyLength = Integer.parseInt(value);
			}
		}
		System.out.println("Headers:\n"+headers+CRLF);
	

		/* If object is larger than MAX_OBJECT_SIZE, close the connection and 
		 * return meaningful message. */
		if (bodyLength > MAX_OBJECT_SIZE) {
			connection.close();
			return("Body length " + bodyLength + " exceeds maximum");
		}
					    
		/* Read the body in chunks of BUF_SIZE using buf[] and copy the chunk
		 * into body[]. Stop when either we have
		 * read Content-Length bytes or when the connection is
		 * closed (when there is no Content-Length in the response). 
		 * Use one of the read() methods of BufferedReader here, NOT readLine().
		 * Make sure not to read more than MAX_OBJECT_SIZE characters.
		 */				
		int bytesRead = 0;

		while (bytesRead < bodyLength || bodyLength == -1) {
			if (bytesRead + BUF_SIZE > MAX_OBJECT_SIZE) break;
			if (connection.isClosed()) break;
			int amount = fromServer.read(buf);
			if (amount <= 0) break;
			for (int i = 0; i < amount; i++) {
				body[bytesRead + i] = buf[i];
			}
			bytesRead += amount;
		}

		/* At this points body[] should hold to body of the downloaded object and 
		 * bytesRead should hold the number of bytes read from the BufferedReader
		 */
		
		/* Close connection and return object as String. */
		System.out.println("Done reading file. Closing connection.");
		connection.close();
		return(new String(body, 0, bytesRead));
	}
}
