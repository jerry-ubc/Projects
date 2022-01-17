package cpen221.mp3.server;

import java.io.*;
import java.net.Socket;

/**
 * WikiMediatorClient is a client that sends requests to the WikiMediatorServer
 * and interprets its replies.
 * A new WikiMediatorClient is "open" until the close() method is called,
 * at which point it is "closed" and may not be used further.
 */
public class WikiMediatorClient {
    private static final int N = 100;
    private Socket socket;
    private BufferedReader in;
    // Rep invariant: socket, in, out != null
    private PrintWriter out;

    /**
     * Make a WikiMediatorClient and connect it to a server running on
     * hostname at the specified port.
     *
     * @throws IOException if can't connect
     */
    public WikiMediatorClient(String hostname, int port) throws IOException {
        System.out.println("entered client constructor");
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Send a request to the server. Requires this is "open".
     *
     * @param command JSON string that contains instructions for WikiMediator
     * @throws IOException if network or server failure
     */
    public void sendRequest(String command) throws IOException {
        System.out.println("entered send request");
        out.print(command);
        out.flush(); // important! make sure x actually gets sent
    }

    /**
     * Get a reply from the next request that was submitted.
     * Requires this is "open".
     *
     * @return the JSON string with that contains the output of the request
     * @throws IOException if network or server failure
     */
    public String getReply() throws IOException {
        System.out.println("entered get reply");
        String reply = "";

        while(in.readLine() != null) {
            reply = in.readLine();
        }

        if (reply.equals("")) {
            throw new IOException("connection terminated unexpectedly");
        }

        try {
            return reply;
        }
        catch (NumberFormatException nfe) {
            throw new IOException("misformatted reply: " + reply);
        }
    }

    /**
     * Closes the client's connection to the server.
     * This client is now "closed". Requires this is "open".
     *
     * @throws IOException if close fails
     */
    public void close() throws IOException {
        System.out.println("entered close");
        in.close();
        out.close();
        socket.close();
    }
}
