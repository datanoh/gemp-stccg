package com.gempukku.stccg.async;

public interface LongPollingResource {
    boolean wasProcessed();

    void processIfNotProcessed();
}
