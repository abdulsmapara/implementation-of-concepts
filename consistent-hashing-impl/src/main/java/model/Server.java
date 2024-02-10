package model;

import java.util.*;

public class Server {
    private String id;
    private Set<String> keys;

    public Server(String id){
        this.id = id;
        this.keys = new HashSet<>();
    }

    public Server(String id, Set<String> keys){
        this.id = id;
        this.keys = keys;
    }

    public String getId() {
        return this.id;
    }

    public Set<String> getKeys() {
        return this.keys;
    }

    public void setKeys(Set<String> keys) {
        this.keys = keys;
    }

    public void addKey(String key) {
        this.keys.add(key);
    }

    public void removeKey(String key) {
        this.keys.remove(key);
    }

    @Override
    public String toString() {
        return "Server with ID: " + this.getId() + ", has keys: " + this.getKeys();
    }

}
