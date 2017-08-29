
package com.philips.platform.datasync;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.core.BaseAppDataCreator;
import com.philips.platform.core.Eventing;
import com.philips.platform.core.injection.AppComponent;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.datasync.consent.ConsentsClient;
import com.philips.platform.datasync.insights.InsightClient;
import com.philips.platform.datasync.userprofile.UserRegistrationInterface;
import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UCoreAdapterTest {

    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final Class<ConsentsClient> CLIENT_CLASS = ConsentsClient.class;
    public static final Class<InsightClient> INSIGHT_CLASS = InsightClient.class;
    @Mock
    private OkHttpClient okHttpClientMock;

    @Mock
    private OkClientFactory okClientFactoryMock;

    @Mock
    private OkClient okClientMock;

    @Mock
    private RestAdapter.Builder restAdapterBuilderMock;

    @Mock
    private RestAdapter restAdapterMock;

    @Mock
    private ConsentsClient clientMock;
    @Mock
    private RequestInterceptor.RequestFacade requestFacadeMock;

    @Captor
    private ArgumentCaptor<RequestInterceptor> requestInterceptorCaptor;

    @Mock
    private GsonConverter gsonConverterMock;

    @Mock
    UserRegistrationInterface userRegistrationImplMock;

    @Mock
    BaseAppDataCreator baseAppDataCreatorMock;

    Eventing mEventing;

//    @Mock
//    private IcpClientFacade icpClientFacadeMock;

    @Mock
    private Context contextMock;

    private UCoreAdapter uCoreAdapter;
    private String TEST_BASE_URL = "https://platforminfra-ds-platforminfrastaging.cloud.pcftest.com";

    private String TEST_INSIGHTS_URL = "https://platforminfra-cs-platforminfrastaging.cloud.pcftest.com";

    @Mock
    private PackageManager packageManagerMock;

    @Mock
    AppComponent appComponantMock;

    @Mock
    ServiceDiscoveryInterface serviceDiscoveryInterfaceMock;

    @Mock
    ApplicationInfo applicationInfoMock;

    @Mock
    private PackageInfo packageInfoMock;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        DataServicesManager.getInstance().setAppComponant(appComponantMock);
        DataServicesManager.getInstance().setServiceDiscoveryInterface(serviceDiscoveryInterfaceMock);
        DataServicesManager.getInstance().mDataServicesBaseUrl = TEST_BASE_URL;
        DataServicesManager.getInstance().mDataServicesCoachingServiceUrl = TEST_INSIGHTS_URL;

        uCoreAdapter = new UCoreAdapter(okClientFactoryMock, restAdapterBuilderMock, contextMock);
        uCoreAdapter.okClientFactory = okClientFactoryMock;
        uCoreAdapter.okHttpClient = okHttpClientMock;

        when(okClientFactoryMock.create(okHttpClientMock)).thenReturn(okClientMock);
        when(restAdapterBuilderMock.setEndpoint(anyString())).thenReturn(restAdapterBuilderMock);
        when(restAdapterBuilderMock.setRequestInterceptor(any(RequestInterceptor.class))).thenReturn(restAdapterBuilderMock);
        when(restAdapterBuilderMock.setClient(okClientMock)).thenReturn(restAdapterBuilderMock);
        when(restAdapterBuilderMock.setConverter(gsonConverterMock)).thenReturn(restAdapterBuilderMock);
        when(restAdapterBuilderMock.build()).thenReturn(restAdapterMock);

        when(contextMock.getPackageManager()).thenReturn(packageManagerMock);
        String test_package_name = "TEST_PACKAGE_NAME";
        when(contextMock.getPackageName()).thenReturn(test_package_name);
        when(packageManagerMock.getPackageInfo(test_package_name, 0)).thenReturn(packageInfoMock);
        packageInfoMock.versionName = "0.24.0-SNAPSHOT";
        when(restAdapterMock.create(CLIENT_CLASS)).thenReturn(clientMock);
    }

    public void ShouldCreateClient_WhenGetClientIsCalled() throws Exception {
        //mEventing = new EventingImpl(new EventBus(), new Handler());
        ConsentsClient client = uCoreAdapter.getAppFrameworkClient(CLIENT_CLASS, ACCESS_TOKEN, gsonConverterMock);
        verify(uCoreAdapter.getAppFrameworkClient(CLIENT_CLASS, ACCESS_TOKEN, gsonConverterMock));
        // assertThat(client).isSameAs(clientMock);
    }

    @Test
    public void ShouldSetCorrectBaseUrl_WhenGetUGrowClientIsCalled() throws Exception {
        uCoreAdapter.getAppFrameworkClient(CLIENT_CLASS, ACCESS_TOKEN, gsonConverterMock);

        verify(restAdapterBuilderMock).setEndpoint(TEST_BASE_URL);
    }

    @Test
    public void ShouldSetCorrectBaseUrl_WhenGetInsightsClientIsCalled() throws Exception {
        uCoreAdapter.getAppFrameworkClient(INSIGHT_CLASS, ACCESS_TOKEN, gsonConverterMock);
        verify(restAdapterBuilderMock).setEndpoint(TEST_INSIGHTS_URL);
    }

    @Test
    public void ShouldSetCorrectBaseUrl_WhenGetInsightsClientIsCalled_and_URL_is_Null() throws Exception {
        DataServicesManager.getInstance().mDataServicesCoachingServiceUrl = null;
        uCoreAdapter.getAppFrameworkClient(INSIGHT_CLASS, ACCESS_TOKEN, gsonConverterMock);
       /* verify(restAdapterBuilderMock).setEndpoint(TEST_INSIGHTS_URL);
        verify(restAdapterMock).setLogLevel(UCoreAdapter.LOG_LEVEL);*/
       verifyNoMoreInteractions(restAdapterBuilderMock);
    }

    @Test
    public void ShouldSetHeaders_WhenRequestIsIntercepted() throws Exception {
        when(okClientFactoryMock.create(okHttpClientMock)).thenReturn(okClientMock);
        uCoreAdapter.getAppFrameworkClient(CLIENT_CLASS, ACCESS_TOKEN, gsonConverterMock);

        interceptRequest();

        verify(requestFacadeMock).addHeader("Content-Type", "application/json");
        verify(requestFacadeMock).addHeader("Authorization", "bearer " + ACCESS_TOKEN);
    }

    protected void interceptRequest() {
        verify(restAdapterBuilderMock).setRequestInterceptor(requestInterceptorCaptor.capture());
        RequestInterceptor interceptor = requestInterceptorCaptor.getValue();

        interceptor.intercept(requestFacadeMock);
    }

    public void ShouldAddVersionAPIHeader_WhenRequestIsIntercepted() throws Exception {
        uCoreAdapter.getAppFrameworkClient(CLIENT_CLASS, ACCESS_TOKEN, gsonConverterMock);
        interceptRequest();

        verify(requestFacadeMock).addHeader(UCoreAdapter.API_VERSION_CUSTOM_HEADER, String.valueOf(UCoreAdapter.API_VERSION));
        verify(requestFacadeMock).addHeader(eq(UCoreAdapter.APP_AGENT_HEADER), anyString());
    }

    @Test
    public void ShouldGetClient_WhenRequestIsIntercepted() throws Exception {
        uCoreAdapter.getClient(CLIENT_CLASS, TEST_BASE_URL, ACCESS_TOKEN, gsonConverterMock);
        interceptRequest();

        verify(restAdapterBuilderMock).setEndpoint(TEST_BASE_URL);

        verify(requestFacadeMock).addHeader(UCoreAdapter.API_VERSION_CUSTOM_HEADER, String.valueOf(UCoreAdapter.API_VERSION));
        verify(requestFacadeMock).addHeader(eq(UCoreAdapter.APP_AGENT_HEADER), anyString());
    }

    @Test
    public void ShouldGetAppAgentHeader_WhenRequestIsIntercepted() throws Exception {
        String agentHeader = uCoreAdapter.getAppAgentHeader();
        assertThat(agentHeader).isNotEmpty();
    }

    @Test
    public void ShouldGetAppAgentHeader_When_Fails_with_Exception() throws Exception {
        String test_package_name = "TEST_PACKAGE_NAME";
        PackageManager.NameNotFoundException exception = new PackageManager.NameNotFoundException();
        doThrow(exception).when(packageManagerMock).getPackageInfo(test_package_name, 0);
        String agentHeader = uCoreAdapter.getAppAgentHeader();
        assertThat(agentHeader).isNotEmpty();
    }

    @Test
    public void ShouldGetBuildTime_WhenRequestIsIntercepted() throws Exception {
        when(contextMock.getPackageName()).thenReturn("com.philips.platform");
        when(contextMock.getPackageManager().getApplicationInfo(contextMock.getPackageName(), 0)).thenReturn(applicationInfoMock);
        String agentHeader = uCoreAdapter.getBuildTime();
        assertThat(agentHeader).isNotEmpty();
    }
}
