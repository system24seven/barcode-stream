package com.system24seven.ignition.barcodes.gateway.tcpsource;

import com.inductiveautomation.eventstream.EventPayload;
import com.inductiveautomation.eventstream.gateway.api.EventStreamContext;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Dictionary;
import java.util.concurrent.atomic.AtomicReference;

public class TcpClientHandler implements Runnable{

    private final Socket clientSocket;
    private final String clientAddress;
    private final int clientPort;
    private final AtomicReference<EventStreamSource.Subscriber> subscriber;
    private final EventStreamContext context;
    private Boolean running;

    public TcpClientHandler(Socket socket, AtomicReference<EventStreamSource.Subscriber> subscriber, EventStreamContext context,Boolean running) {
        this.clientSocket = socket;
        this.subscriber = subscriber;
        this.clientAddress = socket.getInetAddress().getHostAddress();
        this.clientPort = socket.getPort();
        this.context = context;
        this.running = running;
    }

    public void run() {
        context.logger().debugf("[+] Connection from " + clientAddress + ":" + clientPort);

        try (
                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                )
        ) {
            String line;

            // Read lines from the client
            while ((line = reader.readLine()) != null && running) {
                context.logger().tracef("[" + clientAddress + ":" + clientPort + "] " + line);
                long timestamp = System.currentTimeMillis();
                this.subscriber.get().submitEvent(EventPayload.builder((String)line).withMetadata(meta -> {
                    meta.add("sourceIp",clientAddress )
                            .add("timestamp",timestamp);
                }).build());
            }

        } catch (IOException e) {
            // Only log if it's not a normal connection close
            if (!e.getMessage().contains("Connection reset") &&
                    !e.getMessage().contains("Socket closed")) {
                context.logger().warnf("Error handling client " + clientAddress + ": " + e.getMessage());
            }
        } finally {
            try {
                running = false;
                clientSocket.close();
                context.logger().debugf("[-] Connection closed by " + clientAddress + ":" + clientPort + "\n");
            } catch (IOException e) {
                context.logger().errorf("Error closing client socket: " + e.getMessage());
            }
        }
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public Boolean isRunning() {
        return running;
    }

    public void shutdown() {
        running = false;
    }
}

