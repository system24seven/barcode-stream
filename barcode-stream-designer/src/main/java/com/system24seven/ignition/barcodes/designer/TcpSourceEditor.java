package com.system24seven.ignition.barcodes.designer;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.source.SourceEditor;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import javax.swing.*;
import com.system24seven.ignition.barcodes.TcpSourceConfig;
import net.miginfocom.swing.MigLayout;

public class TcpSourceEditor extends SourceEditor {

    private final JTextField tcpHostPort = new JTextField();
    private static final LoggerEx logger = LoggerEx.newBuilder().build("tcpSource");

    public TcpSourceEditor() {
        super();
        setLayout(new MigLayout(
            "ins 0, fillx, gapy 4, wrap 1",
            "[fill, grow]", "")
        );
        add(new JLabel("TCP Listener Port:"));
        add(tcpHostPort, "width 20:400:400, wrap 16");
    }

    /**
     * This method is executed on the Event Dispatcher Thread (EDT).
     */
    @Override
    public void initialize(EventStreamContext context, JsonObject json) {
        TcpSourceConfig config = TcpSourceConfig.fromJson(json);
        tcpHostPort.setText(String.valueOf(config.tcpHostPort()));
    }

    /**
     * This method is executed on the Event Dispatcher Thread (EDT).
     */
    @Override
    public JsonObject getConfig() {
        new TcpSourceConfig("");
        return new TcpSourceConfig(
                tcpHostPort.getText()).toJson();
    }
}

