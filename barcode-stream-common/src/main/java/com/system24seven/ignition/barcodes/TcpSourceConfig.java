package com.system24seven.ignition.barcodes;

import com.inductiveautomation.ignition.common.gson.JsonObject;

public record TcpSourceConfig(String tcpHostPort) {

    public static final String TCPHostPort = "brokerPort";

    public JsonObject toJson() {
        var json = new JsonObject();
        json.addProperty(TCPHostPort, tcpHostPort);
        return json;
    }

    public static TcpSourceConfig fromJson(JsonObject config) {
        if (config == null || config.isEmpty()) {
            return defaultConfig();
        }
        return new TcpSourceConfig(
                config.get(TCPHostPort).getAsString()
        );
    }

    public static TcpSourceConfig defaultConfig() {
        return new TcpSourceConfig("1000");
    }

    public Integer getTCPHostPort() {
        return Integer.valueOf(tcpHostPort.replaceAll("\\p{Punct}", ""));
    }
}
