package cn.chloeprime.commons.rpc;

/**
 * Direction of network operations.
 */
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
