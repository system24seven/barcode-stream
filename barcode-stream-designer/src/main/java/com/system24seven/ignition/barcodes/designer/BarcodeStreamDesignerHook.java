package com.system24seven.ignition.barcodes.designer;

import com.inductiveautomation.eventstream.designer.EventStreamDesignerHook;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.system24seven.ignition.barcodes.BarcodeStreamModule;

public class BarcodeStreamDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);

        // checks if the event stream module is installed
        if (context.getModule(BarcodeStreamModule.EVENT_STREAM_MODULE_ID) != null) {
            var hook = EventStreamDesignerHook.get(context);
            if (hook != null) {
                hook.getEventStreamManager().getSourceRegistry().register(
                        new TcpSourceDesignDelegate()
                );
            }
        }
    }
}
