package service.impl;

import model.Server;
import service.OnServersChangesHandlerService;
import service.ServerManagerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerManagerServiceImpl implements ServerManagerService {
    private final Map<String, Server> serversById;
    private final List<OnServersChangesHandlerService> serverChangeSubscribers;
    private static ServerManagerServiceImpl instance = null;

    private ServerManagerServiceImpl() {
        this.serversById = new HashMap<>();
        this.serverChangeSubscribers = new ArrayList<>();
    }

    public static ServerManagerServiceImpl getInstance() {
        if (instance == null) {
            synchronized (ServerManagerServiceImpl.class) {
                if (instance == null) {
                    instance = new ServerManagerServiceImpl();
                }
            }
            instance = new ServerManagerServiceImpl();
        }
        return instance;
    }

    @Override
    public void subscribe(OnServersChangesHandlerService subscriber) {
        this.serverChangeSubscribers.add(subscriber);
    }


    @Override
    public void addServer(Server server) {
        if (this.serversById.containsKey(server.getId())) {
            throw new RuntimeException("Server with id: " + server.getId() + " already exists");
        }
        this.serversById.put(server.getId(), server);
        System.out.println("Added server " + server);
        this.serverChangeSubscribers.forEach(subscriber -> {
            subscriber.onServerAdded(server);
        });
    }

    @Override
    public void removeServer(Server server) {
        this.serversById.remove(server.getId());
        System.out.println("Removed server " + server);
        this.serverChangeSubscribers.forEach(subscriber -> {
            subscriber.onServerRemoved(server);
        });
    }

    @Override
    public List<Server> getServers() {
        return this.serversById.values().stream().toList();
    }

    @Override
    public void updateServer(String id, Server server) {
        if (!this.serversById.containsKey(id)) {
            throw new UnsupportedOperationException("Unable to find server with id: " + id + ", please use addServer method to add new server");
        }
        this.serversById.put(id, server);
    }
}
