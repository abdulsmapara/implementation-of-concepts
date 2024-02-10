package service;

import model.Server;
import java.util.List;

public interface ServerManagerService {

    void addServer(Server server);
    void removeServer(Server server);
    List<Server> getServers();
    void updateServer(String id, Server server);
    void subscribe(OnServersChangesHandlerService subscriber);

}
