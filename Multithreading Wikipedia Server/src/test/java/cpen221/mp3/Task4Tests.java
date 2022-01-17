package cpen221.mp3;

import com.google.gson.Gson;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.server.WikiMediatorClient;
import cpen221.mp3.wikimediator.WikiMediator;
import cpen221.mp3.wikimediator.Website;
import cpen221.mp3.server.WikiMediatorServer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Task4Tests {
    private static Gson gson;
    private static Gson gson1;
    private static Gson gson2;
    private static String searchCommand;
    private static String zeitgeistCommand;
    private static WikiMediator mediator;
    private static int WIKIMEDIATOR_PORT = 1234;

    @BeforeAll
    public static void setupTests() {

        gson  = new Gson();
        gson1 = new Gson();
        gson2 = new Gson();

        mediator = new WikiMediator();

        searchCommand = "{'id': '1', 'type': 'search', 'query': 'Barak Obama', 'limit': '12'}";
        zeitgeistCommand = "{'id': '2', 'type': 'zeitgeist', 'limit': '5'}";
    }

    @Test
    public void test() throws IOException {
        WikiMediatorServer server = new WikiMediatorServer(WIKIMEDIATOR_PORT, 10, mediator);
        server.serve();

        WikiMediatorClient client = new WikiMediatorClient("127.0.0.1", WIKIMEDIATOR_PORT);
        client.sendRequest(searchCommand);

        String serverReply = client.getReply();

        System.out.println("server reply: " + serverReply);

        client.close();
    }

}
