package com.philips.cl.di.dicomm.port;

import com.philips.cl.di.dicomm.communication.Error;

public interface DIPropertyListener {

	public DIRegistration onPortUpdate(DICommPort<?> port);

    public DIRegistration onPortError(DICommPort<?> port, Error error, String errorData);

}
