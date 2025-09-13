package cn.chloeprime.commons.rpc;

public enum RPCFlow {
    /**
     * Client to server.
     */
    CLIENT_TO_SERVER,

    /**
     * Server to client.
     */
    SERVER_TO_CLIENT,

    /**
     * Both direction is allowed.
     */
    BIDIRECTIONAL,
}
