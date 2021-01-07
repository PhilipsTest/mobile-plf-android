Introduction:

Mobile Platform is basically a classification of common components that helps the proposition app
rapid development. The components are generic and not specific to a business.
It can be used to create, derive, and deploy mobile apps for any Philips proposition.
The components are depicted in the following figure in a simple architectural block diagram.

Proposition apps consume these common components to suffice there requirement.
And deploy mobile apps for any Philips proposition. This also helps in developing and maintaining features (common components) generic to the proposition. The components are depicted in the following figure in a simple architectural block diagram.


Architecture : https://confluence.atlas.philips.com/display/MOB/Architecture

Architectural Advantages:
    * Developer friendly
    * Improved maintainability, extensibility, reusability, reliability, consistency, and diversified management of apps
    * Seamless integration with other Philips platforms
    * Integrated test framework for all the common component in a single app called ReferenceApp


Business Advantages:

    * Cost reduction through platform approach
    * Faster time to market
    * Single user experience for consumer
    * Easy Eco System management
    * Leverage knowledge and reuse across Philips propositions
    * Creation of faster and richer cross-BG solutions.


Terms used in EMS-Platform:
----------------------------------------------------------------------------------------------------------------|
|Common Component| Description                                                                                  |
|---------------------------------------------------------------------------------------------------------------|
|Proposition    | The Apps that consume/use ReferenceApp to extend/achieve the common functionalities.          |
|---------------------------------------------------------------------------------------------------------------|
|uApps          | uApps (also named as micro Apps) are individual Apps created for each functionality           |
|               | and are attached to the ReferenceApp. µApp is a software architectural module that            |
|               | can be a Coco with a User Interface.                                                          |
|---------------------------------------------------------------------------------------------------------------|
|UR             | The User Registration component provides a standard user experience for the user              |
|               | registration flow including social login. The App framework integrates this component         |
|               | that abstracts the interaction towards the HSDP(HealthSuite Digital Platform -                |
|               | HealthSuite digital platform offers both a native cloud-based infrastructure and the          |
|               | core services needed to develop and run a new generation of connected healthcare              |
|               | applications) identity management service and various SDKs for social login.                  |
|---------------------------------------------------------------------------------------------------------------|
|PRG            | The Product Registration component provides a standard user experience to register product.   |
|---------------------------------------------------------------------------------------------------------------|
|IAP            | InAppPurchase is the component that provides interface to purchase products, services and     |
|               | accessories via mobile app. It provides the User Interface, connection to payment gateway     |
|               | and store.                                                                                    |
|---------------------------------------------------------------------------------------------------------------|
|CC             | The consumer care component is off the shelf component providing consumer care functionality  |
|---------------------------------------------------------------------------------------------------------------|
|ECS            | E-Commerces SDK                                                                               |
|---------------------------------------------------------------------------------------------------------------|
|AppInfra       | App infra provides functionality that is common for most propositions and provides a base     |
|               | layer for optimal app development. App Infra provides logging, tagging, secure storage,       |
|               | service discovery, and many other functions.                                                  |
----------------------------------------------------------------------------------------------------------------|

Pull requests:
PRs are essential part of the workflow. Master, development and release branches are protected and you can not push directly to them. You need to create a PR, someone has to approve it and the build has to pass on the CircleCI (static checkers, lint and tests)

For PR creators:
When creating a PR make sure you write a good description. It doesn't have to be long, but it has to give context to the reviewer. Commit messages will not be considered as a description of what has been done in the PR. For every PR you open you should add tag for version of app for which that PR is. Also if that PR contains bugfix you should add bug tag or if it contains new feature then you should add enhancement tag.

For PR reviewers:
When reviewing a PR, alongside the code inspection, checkout the branch on your machine and test it on your mobile phone. That way, some obvious issues (paddings, margins, user specific bugs etc.) are caught before they reach our testers.

#Getting Started

ReferenceApp as a Platform:
ReferenceApp is a base platform used by all propositions to develop and maintain features generic to the propositions. Common components, such as App infra, PhilipsUIKit, uAppFramework, and so on, allow final Apps to be created from these frameworks/libraries.

What ReferenceApp is?
ReferenceApp is a standard UI and a ReferenceApp for home screen, startup screens, general flows, and so on, that any proposition can pick and use commercially. The UI delivered through ReferenceApp is designed as a reference UI. Every proposition should build their own specific UX and UI. We expect that the look and feel of start-up screens, home screen, and so on a defined by the designers in each business. For example, Lumify cannot have the same UI as uGrow, PowerSLeep or Aurora (Note – Lumify, uGrow, PowerSleep and Aurora are Proposition Apps). But they all should be able to use ReferenceApp and the same uApps. The PhilipsUIKit is available in ReferenceApp and it is expected that the unique UIs for each app will leverage the toolkits to ensure consistency, such as how buttons look; what menus should look like; basic flows, and so on.

ReferenceApp does not demonstrate all integration points of a uApp since there could be many variation points based on proposition requirements.

Summarising ReferenceApp below:

Integration of all CoCos from Mobile, Connectivity, Data, and Backend chapters.
ReferenceApp consists of Cocos that has its own User Interface, for example User Registration and Consumer Care have their own UI flows.
ReferenceApp provides a standard way to connect uApps and acts as an interface between uApps with the help of Flow Manager.
One single app that consists all the CoCos (common components) integrated together and fully tested. No more separate CoCos that you need to integrate and test yourself.
Provides a reference UI so that customers can play with the app and easily see how all the uApps are integrated together and experience them.
NOTE: Please note that Data Services and Connectivity screens present in ReferenceApp are for testing purpose only and they do not conform to any UI standards. Also note that ‘About’ screen is also for representation only. It is the Proposition who has to decide on the design and contents of the above screens. Propositions can Adapt/modify the ‘About’ screen by tweaking it.

Find the video of ReferenceApp and its functionality:
https://www.bluetube.philips.com/media/ReferenceAppDemo/1_7nasx7nf

#Getting Started

1.	Installation process
    Android IDE setup : Download android studio from the below link
    https://developer.android.com/studio/index.html?gclid=CjwKEAjw7J3KBRCxv93Q3KSukXQSJADzFzVSFfSWnsfB0Cgp63BJfNuN7H_ivPCXlosE9QNdXrnY8BoClyPw_wcB.

    We currently use the below version: Android studio 3.5

    Code Download:
    The TFS url for repo is https://tfsemea1.ta.philips.com/tfs/TPC_Region02/Innersource/_git/mobile-plf-android #Android Download the latest release source code and import the project in android studio.

    The code can be downloaded in Philips network only or on VPN

    Proxy Settings :
    If required configure the network  proxies in gradle.properties like below .
    systemProp.https.proxyPort=10015
    systemProp.https.proxyHost=165.225.104.34

    The exact proxies may vary depending on yor location

2.	Software dependencies
    Android SDK and Build Tool Version: Currently we use the below versions of android SDK and build tools.
    compileSdkVersion 28
    buildToolsVersion '28.0.2'
    minsdk 23

    Setting up gradle file:
    This is to find the server which hosts the common compoents

                   buildscript {
                    repositories {
                      maven {

                        url 'http://maartens-mini.ddns.htc.nl.philips.com:8081/artifactory/jcenter'                                               }
                                     }
             dependencies {
                             classpath 'com.android.tools.build:gradle:2.2.0'
                          classpath 'com.google.gms:google-services:3.0.0'
                                      }
                        }



                        Adding maven dependencies to get latest versions of common components

                        allprojects {
                                     ext.androidAssertJVer = '1.1.1'

                        repositories {
              maven { url 'http://maartens-mini.ddns.htc.nl.philips.com:8081/artifactory/jcenter' }
              maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
              maven { url "https://mvnrepository.com/artifact/org.robolectric/shadows-core" }
              maven { url 'http://maartens-mini.ddns.htc.nl.philips.com:8081/artifactory/libs-snapshot-local-android'}
        maven {
            url "http://maartens-mini.ddns.htc.nl.philips.com:8081/artifactory/plugins-release-local"
            credentials {
                username = "readonly"
                password = "readonly"
            }
        }
    }

    tasks.withType(Test) {

        maxHeapSize = '4g'
        scanForTestClasses = false
        include "**/*Test.class"
        test { // set JVM arguments for the test JVM(s)
             jvmArgs '-noverify'
        }
    }
}

3.	Latest releases
4.	API references

#Build and Test
make sure that build is green in Jenkins and run the automation testing. Build should PASS and all Regression test suite must pass.

#Contribute
Quality management
Every sprint, one person in a team is a quality manager. Responsibilities of a quality manager are:

Review PR's
1. Cleanup stale branches (end of sprint)
2. Make sure master -> dev -> release are synced (end of sprint)
3. Make sure changelog is created at the end of the sprint. Good practice is to keep an eye for all PR's merged into dev daily. Here we mark changelog for easier tracking.
4. Update Readme if necessary. (Dillinger.io is a nice online markdown document editor)
5. Present demo of all the features finished in the sprint.
6. Link the Work Items in the PRs
