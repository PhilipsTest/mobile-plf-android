package com.philips.pins.shinelib;

/**
 * Created by 310188215 on 02/03/15.
 */
public enum SHNResult {
    SHNOk,
    SHNUnknownLogSyncRecordType,
    SHNAborted,
    SHNUnsupportedOperation,
    SHNOperationFailed,
    SHNUserNotAuthorized,
    // error codes
    SHNErrorInvalidParameter,
    SHNErrorWhileParsing,
    SHNErrorTimeout,
    SHNErrorInvalidState,
    SHNErrorInvalidResponse,
    SHNErrorResponseIncomplete,
    SHNServiceUnavailableError,
    SHNLostConnectionError,
    SHNUnsupportedDataTypeError,
    SHNAssociationError,
    SHNLogSyncBufferFormatError,
    SHNUnknownDeviceTypeError,

    SHNErrorBluetoothDisabled,
    SHNErrorUserConfigurationIncomplete,
    SHNErrorUserConfigurationInvalid,
    SHNErrorProcedureAlreadyInProgress,
    SHNErrorReceptionInterrupted
}
