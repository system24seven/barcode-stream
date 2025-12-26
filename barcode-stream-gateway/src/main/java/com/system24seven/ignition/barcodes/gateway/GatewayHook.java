package com.system24seven.ignition.barcodes.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.ModuleState;
import com.inductiveautomation.ignition.gateway.rpc.GatewayRpcImplementation;
import com.system24seven.ignition.barcodes.BarcodeStreamModule;

import java.util.Optional;

public class GatewayHook extends AbstractGatewayModuleHook {
    private static final LoggerEx logger = LoggerEx.newBuilder().build(GatewayHook.class);

    @Override
    public void setup(GatewayContext context) {
        if (eventStreamLoaded(context)) {
            EventStreamInstaller.setup(context);
        } else {
            var warningMessage = "The Event Stream module is not installed or not active. "
                                 + "The tools will not be available. "
                                 + "Please install or enable the Event Stream module to use this module.";
            logger.warn(warningMessage);
            throw new IllegalStateException(warningMessage);
        }
    }

    @Override
    public boolean isFreeModule(){
        return true;
    }

    private boolean eventStreamLoaded(GatewayContext context) {
        var eventStreamModule = context.getModuleManager()
            .getModule(BarcodeStreamModule.EVENT_STREAM_MODULE_ID);

        return eventStreamModule != null
               && (eventStreamModule.getState() == ModuleState.PENDING
                   || eventStreamModule.getState() == ModuleState.ACTIVE);
    }

    @Override
    public void startup(LicenseState activationState) {
    }

    @Override
    public void shutdown() {
    }

    public static LoggerEx getLogger() {
        return logger;
    }
}
