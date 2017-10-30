
package com.philips.platform.catk.injection;

import android.content.Context;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.catk.model.CreateConsentModelRequest;
import com.philips.platform.catk.model.GetConsentsModelRequest;
import com.philips.platform.catk.network.NetworkController;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {CatkModule.class, AppInfraModule.class,UserModule.class})
@Singleton
public interface CatkComponent {
    Context context();

    LoggingInterface getLoggingInterface();

    RestInterface getRestInterface();

    void inject(NetworkController networkController);

    void inject(CreateConsentModelRequest createConsentModelRequest);

    void inject(GetConsentsModelRequest getConsentsModelRequest);
}
