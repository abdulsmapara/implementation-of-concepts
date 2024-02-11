package service.impl;

import model.Request;
import model.Server;
import service.RequestHandlerService;
import service.OnServersChangesHandlerService;
import service.ServerManagerService;

import java.util.*;

public class RequestHandlerServiceImpl implements RequestHandlerService, OnServersChangesHandlerService {
    private final ServerManagerService serverManagerService;
    private final Map<Integer, Server> serversOnRing;
    private final static int CONSISTENT_HASH_RING_SIZE = 32;
    public RequestHandlerServiceImpl() {
        serverManagerService = ServerManagerServiceImpl.getInstance();
        serverManagerService.subscribe(this);
        serversOnRing = placeServersOnRing(serverManagerService.getServers());
    }

    @Override
    public void handleRequest(Request request) {
        System.out.println("Handling request with id: " + request.getId() + " with " + serversOnRing.values());
        Server server = getServerForRequest(request);
        if (!server.getKeys().contains(request.getId())) {
            throw new RuntimeException("Server " + server + " cannot handle request with id: " + request.getId());
        }
        System.out.println("Request " + request.getId() + " handled by server " + server.getId());

    }

    private Map<Integer, Server> placeServersOnRing(List<Server> servers) {
        Map<Integer, Server> serversOnRing = new HashMap<>();
        servers.forEach(server -> {
            serversOnRing.put(this.getHashValueOnRing(server.getId()), server);
        });
        return serversOnRing;
    }

    private Server getServerForRequest(Request request) {
        int requestIndexOnRing = this.getHashValueOnRing(request.getId());
        Server server = (serversOnRing.get(requestIndexOnRing) != null)
                        ? serversOnRing.get(requestIndexOnRing)
                        : getNextServer(requestIndexOnRing);
        if (server == null) {
            throw new RuntimeException("No server available to process request with id: " + request.getId());
        }
        return server;
    }

    private int getHashValueOnRing(String id) {
        int hashValue = ((id.hashCode()) % CONSISTENT_HASH_RING_SIZE + CONSISTENT_HASH_RING_SIZE) % CONSISTENT_HASH_RING_SIZE;
        return hashValue;
    }

    @Override
    public void onServerAdded(Server server) {

        // get previous server
        // move all keys that map to indexes between previous and this server into this server from next server
        int indexOfNewServerOnRing = this.getHashValueOnRing(server.getId());
        if (serversOnRing.containsKey(indexOfNewServerOnRing)) {
            System.out.println("Removing server with id: " + server.getId() + " as server exists at same location on ring");
            serverManagerService.removeServer(server);
            return;
        }
        Server nextServer = this.getNextServer(indexOfNewServerOnRing);
        Server prevServer = this.getPrevServer(indexOfNewServerOnRing);
        if (nextServer != null && prevServer != null) {
            int indexOfPrevServer = this.getHashValueOnRing(prevServer.getId());
            Set<Integer> indexesBetweenPrevAndNewServer = new HashSet<>();
            indexesBetweenPrevAndNewServer.add(indexOfNewServerOnRing);
            int currIndex = (CONSISTENT_HASH_RING_SIZE + indexOfNewServerOnRing - 1) % CONSISTENT_HASH_RING_SIZE;
            while (currIndex != indexOfPrevServer) {
                indexesBetweenPrevAndNewServer.add(currIndex);
                currIndex = (CONSISTENT_HASH_RING_SIZE + currIndex - 1) % CONSISTENT_HASH_RING_SIZE;
            }
            List<String> keysToRebalance = nextServer.getKeys().stream().filter(key-> indexesBetweenPrevAndNewServer.contains(this.getHashValueOnRing(key))).toList();
            keysToRebalance.forEach(nextServer::removeKey);
            keysToRebalance.forEach(server::addKey);
            System.out.println("Keys to rebalance " + keysToRebalance.size() + " for server with id: " + server.getId());

            serverManagerService.updateServer(nextServer.getId(), nextServer);
            serverManagerService.updateServer(server.getId(), server);
            serversOnRing.put(this.getHashValueOnRing(nextServer.getId()), nextServer);
        }
        serversOnRing.put(indexOfNewServerOnRing, server);

        System.out.println("Rebalancing complete for server with id: " + server.getId());
    }

    private Server getNextServer(Integer indexOnRing) {
        int nextIndex = (indexOnRing + 1) % CONSISTENT_HASH_RING_SIZE;
        while (nextIndex != indexOnRing) {
            if (serversOnRing.get(nextIndex) != null) {
                return serversOnRing.get(nextIndex);
            }
            nextIndex += 1;
            nextIndex %= CONSISTENT_HASH_RING_SIZE;
        }
        return null;
    }

    private Server getPrevServer(Integer indexOnRing) {
        int prevIndex = (CONSISTENT_HASH_RING_SIZE + indexOnRing - 1) % CONSISTENT_HASH_RING_SIZE;
        while (prevIndex != indexOnRing) {
            if (serversOnRing.get(prevIndex) != null) {
                return serversOnRing.get(prevIndex);
            }
            prevIndex = (CONSISTENT_HASH_RING_SIZE + prevIndex - 1) % CONSISTENT_HASH_RING_SIZE;
        }
        return null;
    }

    @Override
    public void onServerRemoved(Server server) {
        // get previous and next server
        // copy all keys of this server into next server
        int indexOfThisServerOnRing = this.getHashValueOnRing(server.getId());

        Server nextServer = getNextServer(indexOfThisServerOnRing);

        if (nextServer != null) {
            List<String> keysToRebalance = server.getKeys().stream().toList();
            keysToRebalance.forEach(nextServer::addKey);
            System.out.println("Keys to rebalance " + keysToRebalance.size() + " for server with id: " + server.getId());

            serverManagerService.updateServer(nextServer.getId(), nextServer);
            serversOnRing.put(this.getHashValueOnRing(nextServer.getId()), nextServer);

        }
        if (serversOnRing.get(indexOfThisServerOnRing).getId().equals(server.getId())) {
            serversOnRing.remove(indexOfThisServerOnRing);
        }
        System.out.println("Rebalancing complete for server with id: " + server.getId());
    }
}
