BlueLib(/ShineLib) for Android - Release Notes
=======================================

Version 2.3.2
-------------
### New features
* [COM-141] Exposed DiComm package for reuse
* Added new capability type: DI_COMM and corresponding capability definition: CapabilityDiComm
* [BL-147] Custom capabilities added. It is now possible to implement capabilities without modifying BlueLib.
* [CON-96] Discovery of DiComm devices

### Bugs fixed
* [DE15085] ProGuard configuration for release builds

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 2.0.3
-------------
### New features
* The dafault implementation of SHNDevice interface does not ignore connect calls: 
	- In state Connected onStateUpdated callback will be issued.
	- In state Connecting no callback.
	- In state Disconnecting onFailedToConnect callback will be issued. 
	- In state Disconnected onStateUpdated callback will be issued.

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 2.0.2
-------------
### New features
* Added data types: IR light, Lux value (For Moonlight/Circadian)

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 2.0.1
-------------
### New features
* [CON-31] Made tests compatible with the release build variant. Added new exception type: TimeoutException. TimeoutException is generated in case the injected persistent storage takes too long to perform its operations.

### Bugs fixed
* [BL-385] NPE when disconnecting BtGatt

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 2.0.0
-------------
### Bugs fixed
* [BL-241] Implemented retry mechanism for perhiral connection

### API changes
* Deprecated SHNDeviceWrapper connect call with parameters
* Added a connect call on SHNDevice that allows to provide a timeout for establishing BLE connection

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 1.0.0
-------------
### New features
* [COD-42] Capability for direct bluetooth characteristic manipulations
* [COD-73] Export RSSI in SHNDevice
* [COD-74] Callbacks for service and characteristic discovery

### API changes
* Added a readRSSI call on SHNDevice
* Added registerDiscoveryListener and unregisterDiscoveryListener on SHNDevice
* Added new capability type: CapabilityBluetoothDirect

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 0.7.3
-------------
### New features
* [BL-295] Add version number to API

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 0.7.2
-------------
### Bugs fixed
* [BG-262] Multiple scanStopped callbacks when scanning is restarted

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 0.7.1
-------------
### Bugs fixed
* [BG-260] Sync problems after re-association

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 0.7.0
-------------
### New features
* [BG-150] Added new SHNDataType: ExtendedWeight 

### Known issues
* [BL-234] Google backup might restore previously associated devices


Version 0.6.0
-------------
### New features
* [BG-168] Added javadoc for app developers

### Known issues
* [BL-234] Google backup might restore previously associated devices

