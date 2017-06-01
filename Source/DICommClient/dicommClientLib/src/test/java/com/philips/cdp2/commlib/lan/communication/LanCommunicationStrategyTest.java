/*
 * (C) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

/*
 * (C) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

/*
 * (C) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

/*
 * (C) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.lan.communication;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.request.ExchangeKeyRequest;
import com.philips.cdp.dicommclient.request.GetKeyRequest;
import com.philips.cdp.dicommclient.request.RequestQueue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LanCommunicationStrategyTest {

    private static final String PORT_NAME = "test";
    private static final int PRODUCT_ID = 1;
    private static final int SUBSCRIPTION_TTL = 10;

    @Mock
    private NetworkNode networkNodeMock;

    @Mock
    private RequestQueue requestQueueMock;

    private LanCommunicationStrategy lanCommunicationStrategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        lanCommunicationStrategy = new LanCommunicationStrategy(networkNodeMock) {
            @Override
            RequestQueue createRequestQueue() {
                return requestQueueMock;
            }
        };
    }

    @Test
    public void whenSubscribingViaHttpAndNoKeyPresent_ThenKeyIsExchangeIsStarted() throws Exception {
        setupForHttpWithoutKeyPresent();

        lanCommunicationStrategy.subscribe(PORT_NAME, PRODUCT_ID, SUBSCRIPTION_TTL, null);

        verify(requestQueueMock).addRequestInFrontOfQueue(isA(ExchangeKeyRequest.class));
    }

    @Test
    public void whenSubscribingViaHttpAndKeyPresent_ThenNoKeyIsExchangeIsStarted() throws Exception {
        setupForHttpWithKeyPresent();

        lanCommunicationStrategy.subscribe(PORT_NAME, PRODUCT_ID, SUBSCRIPTION_TTL, null);

        verify(requestQueueMock, never()).addRequestInFrontOfQueue(any(LanRequest.class));
    }

    @Test
    public void whenSubscribingViaHttpsAndNoKeyPresent_ThenKeyRetrievalIsStarted() throws Exception {
        setupForHttpsWithoutKeyPresent();

        lanCommunicationStrategy.subscribe(PORT_NAME, PRODUCT_ID, SUBSCRIPTION_TTL, null);

        verify(requestQueueMock).addRequestInFrontOfQueue(isA(GetKeyRequest.class));
    }

    @Test
    public void whenSubscribingViaHttpsAndKeyPresent_ThenNoKeyRetrievalIsStarted() throws Exception {
        setupForHttpsWithKeyPresent();

        lanCommunicationStrategy.subscribe(PORT_NAME, PRODUCT_ID, SUBSCRIPTION_TTL, null);

        verify(requestQueueMock, never()).addRequestInFrontOfQueue(any(LanRequest.class));
    }

    @Test
    public void whenPuttingViaHttpAndNoKeyPresent_ThenKeyIsExchangeIsStarted() throws Exception {
        setupForHttpWithoutKeyPresent();

        lanCommunicationStrategy.putProperties(null, PORT_NAME, PRODUCT_ID, null);

        verify(requestQueueMock).addRequestInFrontOfQueue(isA(ExchangeKeyRequest.class));
    }

    @Test
    public void whenPuttingViaHttpAndKeyPresent_ThenNoKeyIsExchangeIsStarted() throws Exception {
        setupForHttpWithKeyPresent();

        lanCommunicationStrategy.putProperties(null, PORT_NAME, PRODUCT_ID, null);

        verify(requestQueueMock, never()).addRequestInFrontOfQueue(any(LanRequest.class));
    }

    @Test
    public void whenPuttingViaHttpsAndNoKeyPresent_ThenKeyRetrievalIsStarted() throws Exception {
        setupForHttpsWithoutKeyPresent();

        lanCommunicationStrategy.putProperties(null, PORT_NAME, PRODUCT_ID, null);

        verify(requestQueueMock).addRequestInFrontOfQueue(isA(GetKeyRequest.class));
    }

    @Test
    public void whenPuttingViaHttpsAndKeyPresent_ThenNoKeyRetrievalIsStarted() throws Exception {
        setupForHttpsWithKeyPresent();

        lanCommunicationStrategy.putProperties(null, PORT_NAME, PRODUCT_ID, null);

        verify(requestQueueMock, never()).addRequestInFrontOfQueue(any(LanRequest.class));
    }

    @Test
    public void whenGettingViaHttpAndNoKeyPresent_ThenKeyIsExchangeIsStarted() throws Exception {
        setupForHttpWithoutKeyPresent();

        lanCommunicationStrategy.getProperties(PORT_NAME, PRODUCT_ID, null);

        verify(requestQueueMock).addRequestInFrontOfQueue(isA(ExchangeKeyRequest.class));
    }

    @Test
    public void whenGettingViaHttpAndKeyPresent_ThenNoKeyIsExchangeIsStarted() throws Exception {
        setupForHttpWithKeyPresent();

        lanCommunicationStrategy.getProperties(PORT_NAME, PRODUCT_ID, null);

        verify(requestQueueMock, never()).addRequestInFrontOfQueue(any(LanRequest.class));
    }

    @Test
    public void whenGettingViaHttpsAndNoKeyPresent_ThenKeyRetrievalIsStarted() throws Exception {
        setupForHttpsWithoutKeyPresent();

        lanCommunicationStrategy.getProperties(PORT_NAME, PRODUCT_ID, null);

        verify(requestQueueMock).addRequestInFrontOfQueue(isA(GetKeyRequest.class));
    }

    @Test
    public void whenGettingViaHttpsAndKeyPresent_ThenNoKeyRetrievalIsStarted() throws Exception {
        setupForHttpsWithKeyPresent();

        lanCommunicationStrategy.getProperties(PORT_NAME, PRODUCT_ID, null);

        verify(requestQueueMock, never()).addRequestInFrontOfQueue(any(LanRequest.class));
    }

    private void setupForHttpsWithKeyPresent() {
        when(networkNodeMock.getHttps()).thenReturn(true);
        when(networkNodeMock.getEncryptionKey()).thenReturn("tha_key");
    }

    private void setupForHttpsWithoutKeyPresent() {
        when(networkNodeMock.getHttps()).thenReturn(true);
        when(networkNodeMock.getEncryptionKey()).thenReturn(null);
    }

    private void setupForHttpWithKeyPresent() {
        when(networkNodeMock.getHttps()).thenReturn(false);
        when(networkNodeMock.getEncryptionKey()).thenReturn("tha_key");
    }

    private void setupForHttpWithoutKeyPresent() {
        when(networkNodeMock.getHttps()).thenReturn(false);
        when(networkNodeMock.getEncryptionKey()).thenReturn(null);
    }
}