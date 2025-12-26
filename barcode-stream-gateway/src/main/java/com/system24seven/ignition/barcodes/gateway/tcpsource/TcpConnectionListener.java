package com.system24seven.ignition.barcodes.gateway.tcpsource;

import com.inductiveautomation.eventstream.gateway.api.EventStreamContext;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TcpConnectionListener implements Runnable {
    private final ServerSocket serverSocket;
    private volatile Boolean running;
    private AtomicReference<EventStreamSource.Subscriber> subscriber = new AtomicReference<>();
    private final EventStreamContext context;
    private final List<TcpClientHandler> handlers = new ArrayList<>();

        @Override
    public void run() {
        // Accept connections in a loop
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();

                // Handle each client in a separate thread
                TcpClientHandler clientHandler = new TcpClientHandler(clientSocket, this.subscriber,context,running);
                Thread clientThread = new Thread(clientHandler);
                handlers.add(clientHandler);
                clientThread.setDaemon(true);
                clientThread.start();

            } catch (SocketException e) {
                if (!running) {
                    // Socket closed intentionally
                    break;
                }
                context.logger().errorf("Socket error: " + e);
                break;
            } catch (IOException e) {
                context.logger().errorf("Socket IO error: " + e);
                break;
            }
        }
    }

    public void shutdown() {
        running = false;
        for (TcpClientHandler handler : handlers) {
            handler.shutdown();
        }

    }

    public TcpConnectionListener(EventStreamContext context, ServerSocket serverSocket, AtomicReference<EventStreamSource.Subscriber> subscriber, Boolean running ) {
        this.context = context;
        this.running = running;
        this.subscriber = subscriber;
        this.serverSocket = serverSocket;
    }
}
