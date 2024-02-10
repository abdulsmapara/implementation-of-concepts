package service;

import model.Server;

public interface OnServersChangesHandlerService {
    void onServerAdded(Server server);
    void onServerRemoved(Server server);
}
