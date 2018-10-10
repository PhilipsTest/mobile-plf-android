/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.pins.shinelib.dicommsupport.ports;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.philips.pins.shinelib.dicommsupport.DiCommPort;

import java.util.Map;

/**
 * @publicPluginApi
 */
public class DiCommFirmwarePort extends DiCommPort {

    public static final String FIRMWARE = "firmware";

    public static class Key {

        public static final String STATE = "state";
        public static final String MAX_CHUNK_SIZE = "maxchunksize";
        public static final String UPGRADE = "upgrade";
        public static final String CAN_UPGRADE = "canupgrade";
        public static final String STATUS_MESSAGE = "statusmsg";

        public static final String DATA = "data";
        public static final String PROGRESS = "progress";
        public static final String SIZE = "size";
    }

    public enum State {
        Idle("idle"),
        Preparing("preparing"),
        Downloading("downloading"),
        Checking("checking"),
        Ready("ready"),
        Programming("programming"),
        Canceling("canceling", "cancel", "cancelling"),
        Error("error"),
        Unknown("unknown");

        private String[] stateNames;

        State(String... stateNames) {
            this.stateNames = stateNames;
        }

        public static State fromString(String stateString) {
            for (State state : State.values()) {
                for (String stateName : state.stateNames) {
                    if (stateName.equalsIgnoreCase(stateString)) {
                        return state;
                    }
                }
            }
            return Unknown;
        }

    }

    public enum Command {
        Downloading("downloading"),
        DeployGo("go"),
        Cancel("cancel"),
        Idle("idle");

        private String commandName;

        Command(String commandName) {
            this.commandName = commandName;
        }

        public final String getName() {
            return commandName;
        }

    }

    public DiCommFirmwarePort(Handler internalHandler) {
        super(FIRMWARE, internalHandler);
    }

    public int getMaxChunkSize() {
        return getMaxChunkSize(getProperties());
    }

    public static int getMaxChunkSize(@NonNull Map<String, Object> properties) {
        Object maxChunkSize = properties.get(Key.MAX_CHUNK_SIZE);

        if (maxChunkSize instanceof Integer) {
            Integer maxChunkSizeInBase64 = (Integer) maxChunkSize;
            return (int) Math.floor(maxChunkSizeInBase64 * 0.75);
        } else if (maxChunkSize instanceof Double) {
            int maxChunkSizeInBase64 = ((Double) maxChunkSize).intValue();
            return (int) Math.floor(maxChunkSizeInBase64 * 0.75);
        }
        return Integer.MAX_VALUE;
    }

    public static int getProgressFromProps(@NonNull final Map<String, Object> properties) {
        Object progressValue = properties.get(Key.PROGRESS);

        return (progressValue instanceof Integer) ? (int) progressValue : -1;
    }

    public static State getStateFromProps(@NonNull final Map<String, Object> properties) {
        Object stateValue = properties.get(Key.STATE);

        return (stateValue instanceof String) ? State.fromString((String) stateValue) : State.Unknown;
    }

    public State getState() {
        return getStateFromProps(getProperties());
    }

    public String getStatusMessage() {
        return getString(Key.STATUS_MESSAGE);
    }

    public String getUploadedUpgradeVersion() {
        return getString(Key.UPGRADE);
    }

    public boolean getCanUpgrade() {
        Map<String, Object> properties = getProperties();
        Object canUpgrade = properties.get(Key.CAN_UPGRADE);

        if (canUpgrade instanceof Boolean) {
            return (boolean) canUpgrade;
        }
        return false;
    }

    @Nullable
    private String getString(String key) {
        Map<String, Object> properties = getProperties();

        Object state = properties.get(key);
        if (state instanceof String) {
            return (String) state;
        }
        return null;
    }
}
