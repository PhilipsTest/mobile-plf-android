/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.cloud.communication;

import android.content.Context;
import android.os.Handler;

import com.philips.cdp.cloudcontroller.api.CloudController;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.dicommclient.request.RemoteRequest;
import com.philips.cdp.dicommclient.request.RequestQueue;
import com.philips.cdp.dicommclient.request.ResponseHandler;
import com.philips.cdp.dicommclient.request.StartDcsRequest;
import com.philips.cdp.dicommclient.subscription.RemoteSubscriptionHandler;
import com.philips.cdp.dicommclient.subscription.SubscriptionEventListener;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.core.util.Availability.AvailabilityListener;
import com.philips.cdp2.commlib.core.util.ConnectivityMonitor;
import com.philips.cdp2.commlib.core.util.HandlerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CloudCommunicationStrategyTest {

    private static final int SUBSCRIPTION_TTL = 0;
    private final Map<String, Object> dataMap = new HashMap<>();
    private final String PORT_NAME = "AirPort";
    private final int PRODUCT_ID = 0;

    @Mock
    private Context contextMock;

    @Mock
    private Handler handlerMock;

    @Mock
    private ConnectivityMonitor connectivityMonitorMock;

    @Mock
    private NetworkNode networkNodeMock;

    @Mock
    private CloudController cloudControllerMock;

    @Mock
    private ResponseHandler responseHandlerMock;

    @Mock
    private RequestQueue requestQueueMock;

    @Mock
    private ArgumentCaptor<StartDcsRequest> startRequestArgumentCaptor;

    @Mock
    private RemoteSubscriptionHandler remoteSubscriptionHandlerMock;

    @Mock
    private AvailabilityListener<CommunicationStrategy> availabilityListenerMock;

    @Captor
    private ArgumentCaptor<AvailabilityListener<ConnectivityMonitor>> availabilityListenerArgumentCaptor;

    private CloudCommunicationStrategy cloudCommunicationStrategy;
    private ResponseHandler capturedResponseHandler;

    @Before
    public void setUp() {
        initMocks(this);

        HandlerProvider.enableMockedHandler(handlerMock);

        when(contextMock.getApplicationContext()).thenReturn(contextMock);

        cloudCommunicationStrategy = new CloudCommunicationStrategyForTesting(networkNodeMock, cloudControllerMock, connectivityMonitorMock);
    }

    @Test
    public void whenPutPropsIsCalledThenDSCIsStarted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenDSCHasStartedSuccessfullyThenPutPropsRequestIsExecuted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue(startRequestArgumentCaptor.capture());
        capturedResponseHandler.onSuccess(null);

        verify(requestQueueMock).addRequest((RemoteRequest) any());
    }

    @Test
    public void whenPutPropsIsCalledWhileDSCIsStartedThenPutPropsRequestIsExecuted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STARTED);
        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock, never()).addRequestInFrontOfQueue(any(StartDcsRequest.class));
        verify(requestQueueMock).addRequest((RemoteRequest) any());
    }

    @Test
    public void whenPutPropsIsCalledTwiceThenAnotherDSCStartRequestIsNotIssued() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STARTING);
        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);
        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock, times(1)).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenDSCISFailedToStartThenItIsStartedAgainWithNextRequest() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue(startRequestArgumentCaptor.capture());
        capturedResponseHandler.onError(Error.REQUEST_FAILED, null);

        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock, times(2)).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenDSCISStoppedThenItIsRestartedAgainWithNextRequest() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue(startRequestArgumentCaptor.capture());
        capturedResponseHandler.onSuccess(null);

        cloudCommunicationStrategy.putProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock, times(2)).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenGetPropsIsCalledThenDSCIsStarted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.getProperties(PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenAddPropsIsCalledThenDSCIsStarted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.addProperties(dataMap, PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenDeletePropsIsCalledThenDSCIsStarted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.deleteProperties(PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenSubscribeIsCalledThenDSCIsStarted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.subscribe(PORT_NAME, PRODUCT_ID, SUBSCRIPTION_TTL, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenSubscribeIsCalled_ThenDSCIsStarted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        SubscriptionEventListener subscriptionEventListener = mock(SubscriptionEventListener.class);
        cloudCommunicationStrategy.addSubscriptionEventListener(subscriptionEventListener);
        cloudCommunicationStrategy.subscribe("somePort", 1, SUBSCRIPTION_TTL, null);

        verify(requestQueueMock).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenUnsubscribeIsCalledThenDSCIsStarted() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.unsubscribe(PORT_NAME, PRODUCT_ID, responseHandlerMock);

        verify(requestQueueMock).addRequestInFrontOfQueue((StartDcsRequest) any());
    }

    @Test
    public void whenEnableSubscriptionIsCalledThenEnableSubscriptionIsCalled() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        SubscriptionEventListener subscriptionEventListener = mock(SubscriptionEventListener.class);
        cloudCommunicationStrategy.addSubscriptionEventListener(subscriptionEventListener);
        cloudCommunicationStrategy.enableCommunication();

        verify(remoteSubscriptionHandlerMock, never()).enableSubscription(eq(networkNodeMock), eq(singleton(subscriptionEventListener)));
    }

    @Test
    public void whenUnsubscribeIsCalled_thenDSCIsStopped() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.unsubscribe("somePort", 1, null);

        verify(cloudControllerMock).stopDCSService();
    }

    @Test
    public void whenUnsubscribeIsCalled_thenDisableSubscriptionIsCalled() {
        when(cloudControllerMock.getState()).thenReturn(CloudController.ICPClientDCSState.STOPPED);
        cloudCommunicationStrategy.unsubscribe("somePort", 1, null);

        verify(remoteSubscriptionHandlerMock).disableSubscription();
    }

    @Test
    public void isAvailableWhenWifiConnectedAndApplianceIsPaired() {
        verify(connectivityMonitorMock).addAvailabilityListener(availabilityListenerArgumentCaptor.capture());
        when(networkNodeMock.getPairedState()).thenReturn(NetworkNode.PairingState.PAIRED);

        cloudCommunicationStrategy = new CloudCommunicationStrategyForTesting(networkNodeMock, cloudControllerMock, connectivityMonitorMock);
        when(connectivityMonitorMock.isAvailable()).thenReturn(true);
        cloudCommunicationStrategy.addAvailabilityListener(availabilityListenerMock);
        availabilityListenerArgumentCaptor.getValue().onAvailabilityChanged(connectivityMonitorMock);

        assertThat(cloudCommunicationStrategy.isAvailable()).isTrue();
        verify(availabilityListenerMock).onAvailabilityChanged(cloudCommunicationStrategy);
    }

    @Test
    public void isNotAvailableWhenWifiConnectedAndApplianceIsNotPaired() {
        verify(connectivityMonitorMock).addAvailabilityListener(availabilityListenerArgumentCaptor.capture());
        when(networkNodeMock.getPairedState()).thenReturn(NetworkNode.PairingState.NOT_PAIRED);

        cloudCommunicationStrategy = new CloudCommunicationStrategyForTesting(networkNodeMock, cloudControllerMock, connectivityMonitorMock);
        when(connectivityMonitorMock.isAvailable()).thenReturn(true);
        cloudCommunicationStrategy.addAvailabilityListener(availabilityListenerMock);
        availabilityListenerArgumentCaptor.getValue().onAvailabilityChanged(connectivityMonitorMock);

        assertThat(cloudCommunicationStrategy.isAvailable()).isFalse();
        verify(availabilityListenerMock).onAvailabilityChanged(cloudCommunicationStrategy);
    }

    @Test
    public void isNotAvailableWhenWifiNotConnected() {
        verify(connectivityMonitorMock).addAvailabilityListener(availabilityListenerArgumentCaptor.capture());

        cloudCommunicationStrategy = new CloudCommunicationStrategyForTesting(networkNodeMock, cloudControllerMock, connectivityMonitorMock);
        cloudCommunicationStrategy.addAvailabilityListener(availabilityListenerMock);
        availabilityListenerArgumentCaptor.getValue().onAvailabilityChanged(connectivityMonitorMock);

        assertThat(cloudCommunicationStrategy.isAvailable()).isFalse();
        verify(availabilityListenerMock).onAvailabilityChanged(cloudCommunicationStrategy);
    }

    class CloudCommunicationStrategyForTesting extends CloudCommunicationStrategy {

        CloudCommunicationStrategyForTesting(NetworkNode networkNode, CloudController cloudController, ConnectivityMonitor connectivityMonitor) {
            super(networkNode, cloudController, connectivityMonitor);
        }

        @Override
        protected RequestQueue createRequestQueue() {
            return requestQueueMock;
        }

        @Override
        protected StartDcsRequest createStartDcsRequest(ResponseHandler responseHandler) {
            capturedResponseHandler = responseHandler;
            return null;
        }

        @Override
        protected RemoteSubscriptionHandler createRemoteSubscriptionHandler(CloudController cloudController) {
            return remoteSubscriptionHandlerMock;
        }
    }
}
