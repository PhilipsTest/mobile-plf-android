package com.philips.platform.pim.migration;

import android.content.Context;
import android.content.res.Resources;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.rest.request.RequestQueue;
import com.philips.platform.appinfra.securestorage.SecureStorage;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UserMigrationListener;
import com.philips.platform.pim.listeners.RefreshUSRTokenListener;
import com.philips.platform.pim.manager.PIMSettingManager;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({PIMSettingManager.class, PIMMigrator.class})
@RunWith(PowerMockRunner.class)
public class PIMMigratorTest extends TestCase {

    @Mock
    private Context mockContext;
    @Mock
    private LoggingInterface mockLoggingInterface;
    @Mock
    private USRTokenManager mockUsrTokenManager;
    @Mock
    AppTaggingInterface mockTaggingInterface;
    @Mock
    private PIMMigrationManager mockPimMigrationManager;
    @Mock
    private UserMigrationListener mockUserMigrationListener;
    @Captor
    private ArgumentCaptor<RefreshUSRTokenListener> captor;

    private final String TAG = PIMMigrator.class.getSimpleName();
    private PIMMigrator pimMigrator;
    String accessToken = "vsu46sctqqpjwkbn";
    private static final String JR_CAPTURE_SIGNED_IN_USER = "jr_capture_signed_in_user";


    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMSettingManager.class);
        PIMSettingManager mockPimSettingManager = mock(PIMSettingManager.class);
        AppInfraInterface mockAppInfraInterface = mock(AppInfraInterface.class);
        SecureStorage mockSecureStorageInterface = mock(SecureStorage.class);
        SecureStorageInterface.SecureStorageError mockSecureStorageError = mock(SecureStorageInterface.SecureStorageError.class);

        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getTaggingInterface()).thenReturn(mockTaggingInterface);
        when(mockPimSettingManager.getAppInfraInterface()).thenReturn(mockAppInfraInterface);
        whenNew(USRTokenManager.class).withArguments(mockAppInfraInterface).thenReturn(mockUsrTokenManager);

        RestInterface mockRestInterface = mock(RestInterface.class);
        when(mockPimSettingManager.getRestClient()).thenReturn(mockRestInterface);
        when(mockRestInterface.getRequestQueue()).thenReturn(mock(RequestQueue.class));

        when(mockAppInfraInterface.getSecureStorage()).thenReturn(mockSecureStorageInterface);
        whenNew(SecureStorageInterface.SecureStorageError.class).withNoArguments().thenReturn(mockSecureStorageError);
        when(mockAppInfraInterface.getSecureStorage().fetchValueForKey(JR_CAPTURE_SIGNED_IN_USER, mockSecureStorageError)).thenReturn(signedUserData());

        pimMigrator = new PIMMigrator(mockContext, mockUserMigrationListener);

        whenNew(PIMMigrationManager.class).withArguments(mockContext, pimMigrator).thenReturn(mockPimMigrationManager);
        when(mockUsrTokenManager.isUSRUserAvailable()).thenReturn(true);
    }

    @Test
    public void testMigrateUSRToPIM_USRNotAvailbale() {
        Resources mockResources = mock(Resources.class);
        when(mockUsrTokenManager.isUSRUserAvailable()).thenReturn(false);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getString(anyInt())).thenReturn("Migration Failed");
        pimMigrator.migrateUSRToPIM();
        verify(mockLoggingInterface).log(DEBUG, TAG, "USR user is not available so assertion not required");
    }

    @Test
    public void testMigrateUSRToPIM_USRUserAvailbale() {
        Resources mockResources = mock(Resources.class);
        when(mockUsrTokenManager.isUSRUserAvailable()).thenReturn(false);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getString(anyInt())).thenReturn("Migration Failed");
        when(mockUsrTokenManager.isUSRUserAvailable()).thenReturn(true);
        pimMigrator.migrateUSRToPIM();
        verify(mockUsrTokenManager).fetchRefreshedAccessToken(captor.capture());
        RefreshUSRTokenListener refreshUSRTokenListener = captor.getValue();
        refreshUSRTokenListener.onRefreshTokenSuccess(accessToken);
        Error error = mock(Error.class);
        refreshUSRTokenListener.onRefreshTokenFailed(error);
    }

    @Test
    public void testUserMigrationSuccess() {
        pimMigrator.onUserMigrationSuccess();
        verify(mockUserMigrationListener).onUserMigrationSuccess();
    }

    @Test
    public void testUserMigrationFailed() {
        Error error = mock(Error.class);
        pimMigrator.onUserMigrationFailed(error);
        verify(mockUserMigrationListener).onUserMigrationFailed(any(Error.class));
    }

    private String signedUserData() {
        return "{r\"original\":{\"lastModifiedDate\":null,\"providerMergedLast\":null,\"preferredLanguage\":\"en\",\"cloudsearch\":{\"syncAttempts\":null,\"syncUpdated\":null},\"uuidHash\":null,\"personalDataUsageAcceptance\":null,\"optIn\":{\"status\":null,\"updated\":null},\"post_login_confirmation\":[],\"streamiumServicesTCAgreed\":null,\"birthday\":\"2019-01-26\",\"consumerPoints\":null,\"batchId\":null,\"familyName\":\"\",\"friends\":[],\"weddingDate\":null,\"interestStreamiumSurveys\":null,\"interestAvent\":null,\"lastModifiedSource\":null,\"medicalProfessionalRoleSpecified\":null,\"lastLoginMethod\":null,\"catalogLocaleItem\":null,\"mobileNumberSmsRequestedAt\":null,\"mobileNumber\":null,\"receiveMarketingEmail\":true,\"familyId\":null,\"children\":[],\"campaignID\":null,\"mobileNumberSmsEnvironment\":null,\"consumerInterests\":[],\"locale\":null,\"mobileNumberNeedVerification\":true,\"profiles\":[],\"sitecatalystIDs\":[],\"marketingOptIn\":{\"locale\":\"en_GB\",\"timestamp\":\"2019-03-18 12:02:14 +0000\"},\"legacyID\":null,\"lastUsedDevice\":{\"tokenType\":null,\"deviceId\":null,\"deviceToken\":null,\"deviceType\":null},\"controlField\":null,\"id\":1533260744,\"deactivatedAccount\":null,\"consentVerifiedAt\":null,\"avmTCAgreed\":null,\"mobileNumberVerified\":null,\"interestCommunications\":null,\"campaigns\":[],\"deviceIdentification\":[],\"badgeVillePlayerIDs\":[],\"nettvTCAgreed\":null,\"clients\":[{\"clientId\":\"yfvh9udgu9zkuse8jr2nah7uk6et6yas\",\"id\":1534874626,\"name\":null,\"firstLogin\":\"2019-07-08 11:54:15 +0000\",\"lastLogin\":\"2019-07-08 11:54:15 +0000\"},{\"clientId\":\"45fhu7hhxrhqx9sm78mgzx4afm9payau\",\"id\":1534924677,\"name\":null,\"firstLogin\":\"2019-07-03 09:04:05 +0000\",\"lastLogin\":\"2019-07-03 09:04:05 +0000\"}],\"visitedMicroSites\":[{\"microSiteID\":\"77000\",\"id\":1533260746,\"timestamp\":\"2019-03-12 11:07:00 +0000\"},{\"microSiteID\":\"81783\",\"id\":1533261026,\"timestamp\":\"2019-03-12 11:07:05 +0000\"}],\"middleName\":null,\"familyRole\":null,\"emailVerified\":\"2018-11-15 06:28:44.13826 +0000\",\"primaryAddress\":{\"company\":null,\"address2\":null,\"stateAbbreviation\":null,\"zipPlus4\":null,\"city\":null,\"state\":null,\"address1\":null,\"houseNumber\":null,\"phone\":null,\"dayTimePhoneNumber\":null,\"zip\":null,\"mobile\":null,\"country\":\"US\",\"address3\":null},\"gender\":\"Male\",\"identifierInformation\":{\"questionsThree\":null,\"questionOne\":null,\"questionTwo\":null,\"answerTwo\":null,\"answerOne\":null,\"questionThree\":null},\"lastUpdated\":\"2019-07-11 04:33:55.71651 +0000\",\"termsAndConditionsAcceptance\":null,\"roles\":[{\"id\":1533260745,\"role\":\"consumer\",\"expiryDate\":null,\"role_assigned\":\"2019-03-12 11:07:05 +0000\",\"verifier\":null}],\"requiresVerification\":null,\"wishList\":null,\"photos\":[],\"email\":\"shashi.ranjan@philips.com\",\"coppaCommunicationSentAt\":null,\"givenName\":\"Shashi Ranjan\",\"retentionConsentGivenAt\":null,\"nettvTermsAgreedDate\":null,\"currentLocation\":null,\"migration\":{\"source\":null,\"migratedAt\":null},\"deactivateAccount\":null,\"janrain\":{\"cloudsearch\":{\"syncAttempts\":null,\"syncUpdated\":null},\"controlFields\":{\"one\":\"false\",\"two\":\"false\",\"three\":\"false\"},\"controlField\":\"ControlFieldReset\",\"properties\":{\"managedBy\":[]}},\"lastLogin\":\"2019-07-11 04:33:55 +0000\",\"interestPromotions\":null,\"ssn\":null,\"NRIC\":null,\"interestCampaigns\":null,\"interestSurveys\":null,\"avmTermsAgreedDate\":null,\"interestWULsounds\":null,\"consentVerified\":null,\"interestCategories\":null,\"externalId\":null,\"created\":\"2018-11-15 06:27:57.120617 +0000\",\"interestStreamiumUpgrades\":null,\"nickName\":null,\"CPF\":null,\"displayName\":null,\"uuid\":\"5f920990-0e8d-49a0-8888-1e8cdb722555\",\"olderThanAgeLimit\":true,\"secondaryPassword\":null,\"aboutMe\":null,\"consents\":[],\"personalDataMarketingProfiling\":null,\"lastNamePronunciation\":null,\"personalDataTransferAcceptance\":null,\"salutation\":null,\"display\":null,\"statuses\":[],\"maritalStatus\":null,\"firstNamePronunciation\":null},\"accessToken\":\"ct5funwca5twwtra\",\"this\":{\"CPF\":null,\"NRIC\":null,\"aboutMe\":null,\"avmTCAgreed\":null,\"avmTermsAgreedDate\":null,\"badgeVillePlayerIDs\":[],\"batchId\":null,\"birthday\":\"2019-01-26\",\"campaignID\":null,\"campaigns\":[],\"catalogLocaleItem\":null,\"children\":[],\"clients\":[{\"clientId\":\"yfvh9udgu9zkuse8jr2nah7uk6et6yas\",\"id\":1534874626,\"name\":null,\"firstLogin\":\"2019-07-08 11:54:15 +0000\",\"lastLogin\":\"2019-07-08 11:54:15 +0000\"},{\"clientId\":\"45fhu7hhxrhqx9sm78mgzx4afm9payau\",\"id\":1534924677,\"name\":null,\"firstLogin\":\"2019-07-03 09:04:05 +0000\",\"lastLogin\":\"2019-07-03 09:04:05 +0000\"}],\"cloudsearch\":{\"syncAttempts\":null,\"syncUpdated\":null},\"consentVerified\":null,\"consentVerifiedAt\":null,\"consents\":[],\"consumerInterests\":[],\"consumerPoints\":null,\"controlField\":null,\"coppaCommunicationSentAt\":null,\"created\":\"2018-11-15 06:27:57.120617 +0000\",\"currentLocation\":null,\"deactivateAccount\":null,\"deactivatedAccount\":null,\"deviceIdentification\":[],\"display\":null,\"displayName\":null,\"email\":\"shashi.ranjan@philips.com\",\"emailVerified\":\"2018-11-15 06:28:44.13826 +0000\",\"externalId\":null,\"familyId\":null,\"familyName\":\"\",\"familyRole\":null,\"firstNamePronunciation\":null,\"friends\":[],\"gender\":\"Male\",\"givenName\":\"Shashi Ranjan\",\"id\":1533260744,\"identifierInformation\":{\"questionsThree\":null,\"questionOne\":null,\"questionTwo\":null,\"answerTwo\":null,\"answerOne\":null,\"questionThree\":null},\"interestAvent\":null,\"interestCampaigns\":null,\"interestCategories\":null,\"interestCommunications\":null,\"interestPromotions\":null,\"interestStreamiumSurveys\":null,\"interestStreamiumUpgrades\":null,\"interestSurveys\":null,\"interestWULsounds\":null,\"janrain\":{\"cloudsearch\":{\"syncAttempts\":null,\"syncUpdated\":null},\"controlFields\":{\"one\":\"false\",\"two\":\"false\",\"three\":\"false\"},\"controlField\":\"ControlFieldReset\",\"properties\":{\"managedBy\":[]}},\"lastLogin\":\"2019-07-11 04:33:55 +0000\",\"lastLoginMethod\":null,\"lastModifiedDate\":null,\"lastModifiedSource\":null,\"lastNamePronunciation\":null,\"lastUpdated\":\"2019-07-11 04:33:55.71651 +0000\",\"lastUsedDevice\":{\"tokenType\":null,\"deviceId\":null,\"deviceToken\":null,\"deviceType\":null},\"legacyID\":null,\"locale\":null,\"maritalStatus\":null,\"marketingOptIn\":{\"locale\":\"en_GB\",\"timestamp\":\"2019-03-18 12:02:14 +0000\"},\"medicalProfessionalRoleSpecified\":null,\"middleName\":null,\"migration\":{\"source\":null,\"migratedAt\":null},\"mobileNumber\":null,\"mobileNumberNeedVerification\":true,\"mobileNumberSmsEnvironment\":null,\"mobileNumberSmsRequestedAt\":null,\"mobileNumberVerified\":null,\"nettvTCAgreed\":null,\"nettvTermsAgreedDate\":null,\"nickName\":null,\"olderThanAgeLimit\":true,\"optIn\":{\"status\":null,\"updated\":null},\"personalDataMarketingProfiling\":null,\"personalDataTransferAcceptance\":null,\"personalDataUsageAcceptance\":null,\"photos\":[],\"post_login_confirmation\":[],\"preferredLanguage\":\"en\",\"primaryAddress\":{\"company\":null,\"address2\":null,\"stateAbbreviation\":null,\"zipPlus4\":null,\"city\":null,\"state\":null,\"address1\":null,\"houseNumber\":null,\"phone\":null,\"dayTimePhoneNumber\":null,\"zip\":null,\"mobile\":null,\"country\":\"US\",\"address3\":null},\"profiles\":[],\"providerMergedLast\":null,\"receiveMarketingEmail\":true,\"requiresVerification\":null,\"retentionConsentGivenAt\":null,\"roles\":[{\"id\":1533260745,\"role\":\"consumer\",\"expiryDate\":null,\"role_assigned\":\"2019-03-12 11:07:05 +0000\",\"verifier\":null}],\"salutation\":null,\"secondaryPassword\":null,\"sitecatalystIDs\":[],\"ssn\":null,\"statuses\":[],\"streamiumServicesTCAgreed\":null,\"termsAndConditionsAcceptance\":null,\"uuid\":\"5f920990-0e8d-49a0-8888-1e8cdb722555\",\"uuidHash\":null,\"visitedMicroSites\":[{\"microSiteID\":\"77000\",\"id\":1533260746,\"timestamp\":\"2019-03-12 11:07:00 +0000\"},{\"microSiteID\":\"81783\",\"id\":1533261026,\"timestamp\":\"2019-03-12 11:07:05 +0000\"}],\"weddingDate\":null,\"wishList\":null}}";
    }
}