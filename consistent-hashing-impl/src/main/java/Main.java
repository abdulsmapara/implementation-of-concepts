import model.Request;
import model.Server;
import service.ServerManagerService;
import service.impl.RequestHandlerServiceImpl;
import service.impl.ServerManagerServiceImpl;

import java.util.*;

public class Main {
    private static final ServerManagerService serverManagerService = ServerManagerServiceImpl.getInstance();
    private static final RequestHandlerServiceImpl requestHandlerService = new RequestHandlerServiceImpl();
    private static final int NUM_REQUESTS = 30;
    private static final int NUM_SERVERS_TO_ADD = 5;
    private static final int NUM_SERVERS_TO_REMOVE = 2;
    private static final int MAX_NUM_KEYS = 26;

    public static void main(String[] args) {
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < MAX_NUM_KEYS; i++) {
            keys.add(String.valueOf(i));
        }

        // add one server initially to serve all requests
        serverManagerService.addServer(new Server(getRandomID(), keys));

        System.out.println("Servers: " + serverManagerService.getServers());


        Request[] requests = new Request[NUM_REQUESTS];
        List<String> keysList = new ArrayList<>(keys);
        for (int i = 0; i < NUM_REQUESTS; i++) {
            requests[i] = new Request(keysList.get(i % keys.size()));
            requestHandlerService.handleRequest(requests[i]);
        }

        // Add few servers
        for (int i = 0; i < NUM_SERVERS_TO_ADD; i++) {
            serverManagerService.addServer(new Server(getRandomID()));
        }

        for (int i = 0; i < NUM_REQUESTS; i++) {
            requestHandlerService.handleRequest(requests[i]);
        }

        for (int i = 0; i < NUM_SERVERS_TO_REMOVE; i++) {
            serverManagerService.removeServer(serverManagerService.getServers().get(0));
        }

        for (int i = 0; i < NUM_REQUESTS; i++) {
            requestHandlerService.handleRequest(requests[i]);
        }


    }

    private static String getRandomID() {
        return UUID.randomUUID().toString();
    }

}
