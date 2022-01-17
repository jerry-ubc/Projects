package cpen221.mp3.server;

import com.google.gson.JsonObject;
import cpen221.mp3.wikimediator.WikiMediator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;

public class WikiMediatorServer {
    private static final long SECONDS_TO_MILLISECONDS = 1000;
    private final ServerSocket serverSocket;
    private final int maxRequests;
    private final WikiMediator wiki;
    private static final AtomicInteger requestCount = new AtomicInteger(0);
    private final ExecutorService pool;

    /*
    Representation Invariant:
    maxRequests > 0
    serverSocket != null
    wiki  != null
     */

    /*
    Abstraction Function:
    Represents a server connected to port port, serving a maximum of n requests, and operating on WikiMediator
    wikiMediator. interact w jwiki api
     */

    /**
     * Start a server at a given port number, with the ability to process
     * up to n requests concurrently.
     *  @param port         the port number to bind the server to, 9000 <= {@code port} <= 9999, otherwise throws IOException
     * @param n            the number of concurrent requests the server can handle, 0 < {@code n} <= 32
     * @param wikiMediator the WikiMediator instance to use for the server, {@code wikiMediator} is not {@code null}
     */
    public WikiMediatorServer(int port, int n, WikiMediator wikiMediator) {
        try {
            maxRequests = n;
            serverSocket = new ServerSocket(port);
            wiki = wikiMediator;
            pool = Executors.newFixedThreadPool(n);
        } catch (IOException ioe) {
            throw new RuntimeException();
        }
    }

    /**
     * Run server, infinitely waiting for connections.
     *
     * @throws RuntimeException If socket is invalid
     */
    public void serve() {
        while (true) {
            try {
                if (requestCount.intValue() < maxRequests) {
                    //block socket until client connects
                    final Socket socket = serverSocket.accept();
                    //create thread to handle current client
                    final int[] timeout = new int[1];
                    Future<?> future = pool.submit(new Runnable() {
                        public void run() {
                            timeout[0] = findTimeouts(socket);
                            try {
                                try {
                                    handle(socket);
                                } finally {
                                    socket.close();
                                }
                            } catch (IOException ioe) {
                                throw new RuntimeException();
                            }
                        }
                    });
                    try {
                        future.get(timeout[0], TimeUnit.SECONDS);
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        future.cancel(true);
                    }

//                    Thread handler = new Thread(new Runnable() {
//                        public void run() {
//                            try {
//                                try {
//                                    handle(socket);
//                                } finally {
//                                    socket.close();
//                                }
//                            } catch (IOException ioe) {
//                                throw new RuntimeException();
//                            }
//                        }
//                    });
//                    handler.start();
                }
            } catch (IOException ioe) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * Parses JSON string to find if timeout property exists.
     * @param socket Socket client uses to connect
     * @return -1 if property does not exist, otherwise return property as integer.
     */
    private synchronized int findTimeouts(Socket socket) {
        Gson gson = new Gson();
        int timeout = -1;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                JsonObject command = gson.fromJson(line, JsonObject.class);
                if (command.has("timeout")) {
                    timeout = command.get("timeout").getAsInt();
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException();
        }
        return timeout;
    }

    /**
     * Parses Json String inputs to commands for WikiMediator to resolve depending on the String contents.
     *
     * @param socket Socket client uses to connect
     * @throws RuntimeException If any errors arise in dealing with the socket
     */
    //NOTE TO TA: lengthy method is due to tightness on time and inability to find a better implementation
    private synchronized void handle(Socket socket) {
        requestCount.getAndSet(requestCount.intValue() + 1);
        JsonObject response = new JsonObject();
        Gson gson = new Gson();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()), true);

            try {
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    long startTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
                    int timeout = 0;
                    JsonObject command = gson.fromJson(line, JsonObject.class);
                    if (command.has("type")) {
                        if (command.get("type").getAsString().equals("stop")) {
                            //STOP
                            if (command.has("id")) {
                                response.addProperty("id", command.get("id").getAsString());
                            }
                            response.addProperty("response", "bye");
                            requestCount.getAndSet(requestCount.intValue() - 1);
                            out.print(gson.toJson(response));
                            out.close();
                            in.close();
                        } else if (command.get("type").getAsString().equals("search")) {
                            //SEARCH
                            if (command.has("query") && command.has("limit")) {
                                List<String> reply = wiki.search(command.get("query").getAsString(),
                                        command.get("limit").getAsInt());
                                response.addProperty("response", reply.toString());
                            }
                            if (command.has("timeout"))
                                timeout = Integer.parseInt(command.get("timeout").getAsString());
                        } else if (command.get("type").getAsString().equals("getPage")) {
                            //GETPAGE
                            if (command.has("pageTitle")) {
                                String reply = wiki.getPage(command.get("pageTitle").getAsString());
                                response.addProperty("response", reply);
                            }
                            if (command.has("timeout"))
                                timeout = Integer.parseInt(command.get("timeout").getAsString());
                        } else if (command.get("type").getAsString().equals("zeitgeist")) {
                            //ZEITGEIST
                            if (command.has("limit")) {
                                List<String> reply = wiki.zeitgeist(command.get("limit").getAsInt());
                                response.addProperty("response", reply.toString());
                            }
                            if (command.has("timeout"))
                                timeout = Integer.parseInt(command.get("timeout").getAsString());
                        } else if (command.get("type").getAsString().equals("trending")) {
                            //TRENDING
                            if (command.has("timeLimitInSeconds") && command.has("maxItems")) {
                                List<String> reply = wiki.trending(command.get("timeLimitInSeconds").getAsInt(),
                                        command.get("maxItems").getAsInt());
                                response.addProperty("response", reply.toString());
                            }
                            if (command.has("timeout"))
                                timeout = Integer.parseInt(command.get("timeout").getAsString());
                        } else if (command.get("type").getAsString().equals("windowedPeakLoad")) {
                            //WINDOWEDPEAKLOAD
                            if (command.has("timeWindowInSeconds")) {
                                int reply = wiki.windowedPeakLoad(command.get("timeWindowInSeconds").getAsInt());
                                response.addProperty("response", reply);
                            } else {
                                int reply = wiki.windowedPeakLoad();
                                response.addProperty("response", reply);
                            }
                            if (command.has("timeout"))
                                timeout = Integer.parseInt(command.get("timeout").getAsString());
                        }
                        long endTime;
                        boolean timedOut = false;
                        if (timeout != 0) {
                            endTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
                            if (startTime - endTime > timeout) {
                                //Timed out
                                timedOut = true;
                            }
                        }
                        if (timedOut) {
                            if (response.has("response"))
                                response.remove("response");
                            response.addProperty("response", "Operation timed out");
                            response.addProperty("status", "failed");
                        } else if (response.has("response")) {
                            response.addProperty("status", "success");
                        } else {
                            response.addProperty("status", "failed");
                        }
                        if (command.has("id")) {
                            response.addProperty("id", command.get("id").getAsString());
                        }

                    }
                    out.print(gson.toJson(response));
                }
            } finally {
                requestCount.getAndSet(requestCount.intValue() - 1);
                out.close();
                in.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException();
        }
        wiki.shutdown();
    }
}
