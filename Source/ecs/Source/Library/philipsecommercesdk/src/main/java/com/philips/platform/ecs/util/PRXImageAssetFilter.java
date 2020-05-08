package com.philips.platform.ecs.util;

import com.philips.platform.ecs.model.asset.Asset;

import java.util.ArrayList;
import java.util.List;

public class PRXImageAssetFilter {

    private static final int RTP = 1;
    private static final int APP = 2;
    private static final int DPP = 3;
    private static final int MI1 = 4;
    private static final int PID = 5;


    public List<Asset> getValidPRXAssets(List<Asset> assets) {
        List<Asset> validAssets = new ArrayList<>();
        for (Asset asset : assets) {
            int assetType = getAssetType(asset);
            if (assetType != -1) {
                validAssets.add(asset);
            }
        }
        return validAssets;
    }

    private int getAssetType(Asset asset) {
        switch (asset.getType()) {
            case "RTP":
                return RTP;
            case "APP":
                return APP;
            case "DPP":
                return DPP;
            case "MI1":
                return MI1;
            case "PID":
                return PID;
            default:
                return -1;
        }
    }
}
