### Consistent Hashing implementation

This code contains implementation of basic consistent hashing algorithm. It simulates handling of requests by a server, and the handling of same after adding and removing some servers.

Every request is associated with an id, which is also called a key.
Initially, a server contains all the data (i.e. has all the keys) and handles those requests.

Then, we add a few servers to the set of servers. This makes the keys to be rebalanced and get distributed among the servers.
Then we check whether the requested data gets mapped to the server which actually should have the key.
We repeat the same after removing a few servers.


##### How to run ?

- Install `java` version 17
- Execute below commands:
```bash
javac Main.java
java Main
```


