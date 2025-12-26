package com.system24seven.ignition.barcodes.gateway.tcpsource;

import com.inductiveautomation.eventstream.gateway.api.EventStreamContext;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;
import com.system24seven.ignition.barcodes.TcpSourceConfig;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simple TCP Socket Listener
 *
 * Listens on a specified port and prints any strings received to the console.
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
     * Represents a manager for handling MQTT operations.
     *
     * @param settingsRecord - The MQTT settings record containing broker configuration details
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