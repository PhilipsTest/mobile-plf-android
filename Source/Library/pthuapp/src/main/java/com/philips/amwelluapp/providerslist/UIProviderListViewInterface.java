package com.philips.amwelluapp.providerslist;

import com.americanwell.sdk.entity.provider.ProviderInfo;

import java.util.List;

public interface UIProviderListViewInterface {
    void updateProviderAdapterList(List<ProviderInfo> providerInfos);
}
