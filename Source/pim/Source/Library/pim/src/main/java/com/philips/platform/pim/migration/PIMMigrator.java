package com.philips.platform.pim.migration;

import android.content.Context;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.listeners.UserMigrationListener;
import com.philips.platform.pim.errors.PIMErrorEnums;
import com.philips.platform.pim.listeners.PIMUserMigrationListener;
import com.philips.platform.pim.listeners.RefreshUSRTokenListener;
import com.philips.platform.pim.manager.PIMSettingManager;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

public class PIMMigrator implements RefreshUSRTokenListener, PIMUserMigrationListener {

    private final LoggingInterface mLoggingInterface;
    private Context context;
    private final String TAG = PIMMigrator.class.getSimpleName();
    private USRTokenManager usrTokenManager;
    private UserMigrationListener userMigrationListener;
    private AtomicBoolean migrationInProgress = new AtomicBoolean();

    private PIMMigrator(Context context) {
        this.context = context;
        PIMSettingManager pimSettingManager = PIMSettingManager.getInstance();
        mLoggingInterface = pimSettingManager.getLoggingInterface();
        usrTokenManager = new USRTokenManager(pimSettingManager.getAppInfraInterface());
    }

    public PIMMigrator(Context context, UserMigrationListener userMigrationListener) {
        this(context);
        this.userMigrationListener = userMigrationListener;
    }

    public boolean isMigrationRequired() {
        return usrTokenManager.isUSRUserAvailable();
    }

    public void migrateUSRToPIM() {
        setMigrationsStatus(true);
        mLoggingInterface.log(DEBUG, TAG, "migrateUSRToPIM called");
        if (usrTokenManager.isUSRUserAvailable()) {
            mLoggingInterface.log(DEBUG, TAG, "migrateUSRToPIM isUSRUserAvailable : "+usrTokenManager.isUSRUserAvailable());
            usrTokenManager.fetchRefreshedAccessToken(this);
        } else {
            setMigrationsStatus(false);
            mLoggingInterface.log(DEBUG, TAG, "USR user is not available so assertion not required");
            if (userMigrationListener != null)
                userMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.getLocalisedErrorDesc(context, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
        }
    }

    @Override
    public void onRefreshTokenSuccess(String accessToken) {
        PIMMigrationManager pimMigrationManager = new PIMMigrationManager(context, this);
        pimMigrationManager.migrateUser(accessToken);
    }

    @Override
    public void onRefreshTokenFailed(Error error) {
        setMigrationsStatus(false);
        mLoggingInterface.log(DEBUG, TAG, "Refresh access token failed.");
        if (userMigrationListener != null) {
            userMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.getLocalisedErrorDesc(context, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
        }
    }

    @Override
    public void onUserMigrationSuccess() {
        setMigrationsStatus(false);
        usrTokenManager.deleteUSRFromSecureStorage();
        mLoggingInterface.log(DEBUG, TAG, "User is migrated to PIM Successfully");
        if (userMigrationListener != null)
            userMigrationListener.onUserMigrationSuccess();
    }

    @Override
    public void onUserMigrationFailed(Error error) {
        setMigrationsStatus(false);
        PIMSettingManager.getInstance().getTaggingInterface().trackActionWithInfo("setError", "technicalError", "migration");
        mLoggingInterface.log(DEBUG, TAG, "User migration failed! " + error.getErrDesc());
        if (userMigrationListener != null)
            userMigrationListener.onUserMigrationFailed(error);
    }

    private void setMigrationsStatus(boolean migrationsStatus){
        migrationInProgress.set(migrationsStatus);
    }

    public boolean isMigrationInProgress(){
        return migrationInProgress.get();
    }
}
