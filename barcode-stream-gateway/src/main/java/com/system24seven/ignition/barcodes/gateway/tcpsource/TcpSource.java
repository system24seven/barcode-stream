package com.system24seven.ignition.barcodes.gateway.tcpsource;

import static com.system24seven.ignition.barcodes.BarcodeStreamModule.TCP_ID;
import static com.system24seven.ignition.barcodes.BarcodeStreamModule.TCP_NAME;

import com.inductiveautomation.eventstream.SourceDescriptor;
import com.inductiveautomation.eventstream.gateway.api.EventStreamContext;
import com.inductiveautomation.eventstream.gateway.api.EventStreamSource;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.system24seven.ignition.barcodes.TcpSourceConfig;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Starts a listener for MQTT Topics and converts them to events
 */
public class TcpSource implements EventStreamSource {

    public static Factory createFactory() {
        return new Factory() {
            @Override
            public SourceDescriptor getDescriptor() {
                return new SourceDescriptor(
                        TCP_ID,
                        TCP_NAME,
                    "Listens to TCP Port 1000 and emits messages as events."
                );
            }

            @Override
            public EventStreamSource create(EventStreamContext context, JsonObject jsonConfig) {
                return new TcpSource(context, TcpSourceConfig.fromJson(jsonConfig));
            }
        };
    }

    private final EventStreamContext context;
    private final AtomicReference<Subscriber> subscriber = new AtomicReference<>();

    private TcpListener TcpManager;
    private final TcpSourceConfig config;

    public TcpSource(EventStreamContext context, TcpSourceConfig config) {
        this.context = context;
        this.config = config;
    }

    @Override
    public void onStartup(Subscriber subscriber) {
        context.logger().infof("Starting TCP Server");
        try {
            TcpManager = new TcpListener(config,subscriber,context);
            TcpManager.startTcpListener();
        } catch (Exception e){
            context.logger().errorf("Error loading TCP manager: " + e.getMessage(), e);
        }
        context.logger().infof("Started TcpServer on %s", config.getTCPHostPort());
    }

    @Override
    public void onShutdown() {
        context.logger().infof("Shutting down %s", TCP_ID);
        TcpManager.shutdown();
        subscriber.set(null);
    }
}