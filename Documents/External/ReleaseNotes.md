CommLib for Android - Release Notes
=======================================

Version NEXT
-------------

### API Changes
* NetworkNode.getModelType() and NetworkNode.setModelType(String modelType) are renamed to NetworkNode.getModelId() and NetworkNode.setModelId(String modelId)
* FirmwarePort: a timeout value was added as argument to pushLocalFirmware(byte[], long), pullRemoteFirmware(String, long), cancel(long) and deployFirmware(long)
* FirmwarePort: canUpgrade() method was added. The user can use this to determine if the device is able or willing to perform a firmware upgrade.

### New features
* DiscoveryStrategy interface is extended with start(Set<String> deviceTypes, Set<String> modelIds) method to allow filtering on model ids.
 
### Bugs fixed

### Known issues

Version 3.1.2
-------------

### API Changes
* Added type parameter to DICommPortListener to prevent unneeded inline casting.
* Added PortProperties marker interface to type parameter of DICommPort in order to enforce ProGuard rules (prevent field name mangling for JSON deserialization).

### New features
* COM-24 Updated to work with new CloudController API
* COM-24 Improved user pairing
* COM-141 Expanded Errors Enum to cover all errors in DiComm spec
* COM-207 New API for discovery (using CommCentral)
* COM-209 BLE discovery
* COM-249 Https support
* CON-119 Firmware update
 
### Bugs fixed
* [DE15085] ProGuard configuration for release builds

### Known issues
* The new API for discovery is not working yet for lan communication. The old DiscoveryManager should be used.

Version 2.0.0
-------------

### New features
* COM-119 DiComm client lib can now be initialized with external CloudController.
* COM-24 Added startUserPairing call to PairingHandler that allows to create user relationship. 

### Bugs fixed

### Known issues

Version 1.2.2
-------------

This release is for testing the Jenkins 2 build pipeline.

### New features

### Bugs fixed

### Known issues

Version v1.2.1
--------------

### New features
* Instead of supplying NetworkNode to every method, NetworkNode parameter is added to construstors (for example see CommunicationMarshal or DISecurity).

### Bugs fixed

### Known issues

Version v1.2.0
--------------

### New features
* DICommAppliance class signature change: 
	disableSubscription() renamed to disableCommunication()
* enableSubscription() renamed to enableCommunication()

### Bugs fixed
* DE12197 Fixed timing issues with SignOn ongoing. 

### Known issues

Version v1.1.3
--------------

### New features
* BG-294 Added impementation for getModelNumber in NetworkNode so it is possible to distinguish between appliance models in ApplianceFactory

### Bugs fixed
* DE12042 Rebooting appliance no longer removes pairing relationships 
* DE11897 When requesting removal of backend relations then the reletions are actually removed  

### Known issues

Version v1.1.2
--------------

### New features
* Upgraded ICPClient to v7.1.0_A.1

### Bugs fixed
* DE11257 Remote subscriptions' reponse messages are now parsed properly. 
* DE11299 DevicePort now supports subscription 

### Known issues

Version v1.1.0
--------------

### New features
* The following fields on the Firmware port are now non optional: name, version and progress.
* When an appliance is disconnected it is not subscribed anymore. As soon as it become connected, the subscription will still occur.
* The interface of PairingListener has changed, now it know the type of appliance, and will return the appliance instead of just its network node on success or failure.

### Bugs fixed

### Known issues

Version v1.0.7
--------------

### New features
* Remote control in sample app

### Bugs fixed
* DE10160: DIComm request error in Android
* DE9705: In Pairing relations, default AllowDelegation must be set to FALSE

### Known issues

Version v1.0.6
--------------

### New features
* Full 64-bit SSDP support
* New ICP client with 64-bit ARM support (v6.1.2_A.2)

### Bugs fixed
* DE9405: Fixed
* DE9406: Fixed
* DE9407: Not a bug. Use getSchedules to obtain the numbers.
* DE9719: Fixed

### Known issues

Version v1.0.5
--------------

### New features

### Bugs fixed
* Removed incorrect loging in SchedulelLstPort

### Known issues

Version v1.0.4
--------------

### New features
* Enabled remote control, by removing Airpurifier specific CPP code
* Improved data handling recieved from remote network location

### Bugs fixed
* Reworked ScheduleListPort for remote communication

### Known issues

Version 1.0.3
-------------

### New features
* Added command property to ScheduleListPort
* Enabled name functionality on schedules
* Improved data paring of device port
* Updated dependenies

### Bugs fixed
* DE8889: Not a bug
* DE8863: Fixed

### Known issues
