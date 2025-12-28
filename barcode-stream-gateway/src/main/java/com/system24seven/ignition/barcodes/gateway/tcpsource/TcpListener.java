package com.system24seven.ignition.barcodes.gateway.tcpsource;

import com.inductiveautomation.eventstream.gateway.api.EventStreamContext;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;
import com.system24seven.ignition.barcodes.TcpSourceConfig;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicReference;


/**
 * TcpListener is responsible for creating and managing a TCP server socket
 * that listens for incoming connections on a specified port and host. It handles
 * the initialization, startup, and shutdown of the server, as well as the delegation
 * of client connection handling to a TcpConnectionListener.
 */
public class TcpListener {
    private final TcpSourceConfig settingsRecord;
    private ServerSocket serverSocket;
    private volatile Boolean running = true;
    private final AtomicReference<EventStreamSource.Subscriber> subscriber = new AtomicReference<>();
    private final int port;
    private final String host;
    private final EventStreamContext context;
    private TcpConnectionListener listener;

    /**
     * Constructs a new TcpListener instance, which is responsible for initializing
     * a TCP server socket to listen for incoming connections, configured with a specific
     * host and port. The instance also sets up a subscriber to handle incoming events
     * and associates the listener with a specific application context.
     *
     * @param settingsRecord the configuration object containing the host and port
     *                        settings for the TCP server.
     * @param subscriber      the subscriber event handler that processes incoming events
     *                        from the TCP connections.
     * @param context         the application-specific context providing logging and other
     *                        utilities required for the listener's operations.
     */
    public TcpListener(TcpSourceConfig settingsRecord, EventStreamSource.Subscriber subscriber, EventStreamContext context){
        this.settingsRecord = settingsRecord;
        this.port = settingsRecord.getTCPHostPort();
        this.host = "0.0.0.0";
        this.subscriber.set(subscriber);
        this.context = context;
    }

    /**
     * Start listening for connections
     */
    public void startTcpListener() {
        try {
            // Create server socket
            InetAddress bindAddr = host.equals("0.0.0.0") ? null : InetAddress.getByName(host);
            serverSocket = new ServerSocket(port, 5, bindAddr);
            context.logger().infof("Listening on " + host + ":" + port);

            //Start TCP port server
            listener = new TcpConnectionListener(context, serverSocket, subscriber, running);
            Thread serverThread = new Thread(listener);
            serverThread.setDaemon(true);
            serverThread.start();

        } catch (IOException e) {
            context.logger().errorf("Error starting server: " + e);
        }
    }

    /**
     * Stop the server
     */
    public void shutdown() {
        running = false;
        listener.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            context.logger().debugf("\nServer stopped.");
        } catch (IOException e) {
            context.logger().errorf("Error closing server socket: " + e.getMessage());
        }
    }
}