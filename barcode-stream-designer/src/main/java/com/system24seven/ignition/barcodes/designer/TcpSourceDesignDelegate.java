package com.system24seven.ignition.barcodes.designer;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.source.EventStreamSourceDesignDelegate;
import com.inductiveautomation.eventstream.designer.api.source.SourceEditor;
import com.system24seven.ignition.barcodes.BarcodeStreamModule;

public class TcpSourceDesignDelegate implements EventStreamSourceDesignDelegate {

    @Override
    public SourceEditor getEditor(EventStreamContext context) {
        return new TcpSourceEditor();
    }

    @Override
    public String getType() {
        return BarcodeStreamModule.TCP_ID;
    }

}
