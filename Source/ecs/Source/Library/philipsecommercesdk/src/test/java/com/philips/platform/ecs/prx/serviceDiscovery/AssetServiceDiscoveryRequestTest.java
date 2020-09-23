package com.philips.platform.ecs.prx.serviceDiscovery;

import android.content.Context;

import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.request.ProductAssetRequest;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.StaticBlock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class AssetServiceDiscoveryRequestTest {

    private Context mContext;
    private AppInfra appInfra;
    private MockECSServices mockECSServices;
    private ECSServices ecsServices;

    @Mock
    RestInterface mockRestInterface;

    ProductAssetRequest assetServiceDiscoveryRequest;

    String CTN = "1234";

    PrxConstants.Sector sector = PrxConstants.Sector.B2C;

    PrxConstants.Catalog catalog = PrxConstants.Catalog.CONSUMER;


    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices(appInfra);
        ecsServices = new ECSServices(appInfra);
        StaticBlock.initialize();

        assetServiceDiscoveryRequest = new ProductAssetRequest(CTN, PrxConstants.Sector.B2C, PrxConstants.Catalog.CARE, null);
    }

    @Test
    public void shouldTestGetCTN() {
        assertEquals(CTN, assetServiceDiscoveryRequest.getCtn());
    }

    @Test
    public void shouldTestSector() {
        assertEquals(sector, assetServiceDiscoveryRequest.getSector());
    }

    @Test
    public void shouldTestCatalog() {
        assertEquals(catalog, assetServiceDiscoveryRequest.getCatalog());
    }

    @Test
    public void shouldTestGetRequestUrlFromAppInfra() {

    }
}