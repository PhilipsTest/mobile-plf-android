/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.datasync.characteristics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.philips.platform.core.Eventing;
import com.philips.platform.core.datatypes.Characteristics;
import com.philips.platform.core.datatypes.OrmTableType;
import com.philips.platform.core.events.BackendDataRequestFailed;
import com.philips.platform.core.events.SyncBitUpdateRequest;
import com.philips.platform.core.events.UCDBUpdateFromBackendRequest;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;
import com.philips.platform.datasync.UCoreAccessProvider;
import com.philips.platform.datasync.UCoreAdapter;
import com.philips.platform.datasync.synchronisation.DataFetcher;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

public class UserCharacteristicsFetcher extends DataFetcher {
    private static final int API_VERSION = 9;
    private GsonConverter mGsonConverter;
    @Inject
    UCoreAccessProvider mUCoreAccessProvider;
    @Inject
    UserCharacteristicsConverter mUserCharacteristicsConverter;
    @Inject
    Eventing eventing;

    @Inject
    public UserCharacteristicsFetcher(@NonNull final UCoreAdapter uCoreAdapter,
                                      @NonNull final GsonConverter gsonConverter) {
        super(uCoreAdapter);
        this.mGsonConverter = gsonConverter;
        DataServicesManager.getInstance().getAppComponant().injectUserCharacteristicsFetcher(this);
    }

    @Nullable
    @Override
    public RetrofitError fetchDataSince(@Nullable DateTime sinceTimestamp) {
        try {
            DSLog.d(DSLog.LOG, "Inder = Inside UC Fetcher");
            final UserCharacteristicsClient userCharacteristicsClient = uCoreAdapter.getAppFrameworkClient(UserCharacteristicsClient.class,
                    mUCoreAccessProvider.getAccessToken(), mGsonConverter);

            if (userCharacteristicsClient != null) {
                UCoreUserCharacteristics uCoreUserCharacteristics = userCharacteristicsClient.getUserCharacteristics(mUCoreAccessProvider.getUserId(),
                        mUCoreAccessProvider.getUserId(), API_VERSION);


                List<Characteristics> characteristicsList = mUserCharacteristicsConverter.convertToCharacteristics(uCoreUserCharacteristics,
                        mUCoreAccessProvider.getUserId());

                eventing.post(new SyncBitUpdateRequest(OrmTableType.CHARACTERISTICS, true));
                DSLog.d(DSLog.LOG, "Inder = Inside UC Fetcher " + characteristicsList);
                eventing.post(new UCDBUpdateFromBackendRequest(characteristicsList, null));
            }
            return null;
        } catch (RetrofitError exception) {
            eventing.post(new BackendDataRequestFailed(exception));
            return exception;
        }
    }
}
