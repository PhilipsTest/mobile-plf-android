#!/usr/bin/env groovy
// please look at: https://jenkins.io/doc/book/pipeline/syntax/
BranchName = env.BRANCH_NAME
String param_string_cron = BranchName == "develop" ? "H H(20-21) * * * %buildType=PSRA \nH H(21-22) * * * %buildType=TICS" : ""

def MailRecipient = 'DL_CDP2_Callisto@philips.com'
def nodes = '27.0.2 && device'
if (BranchName == "develop") {
    nodes = nodes + " && TICS"
}

pipeline {
    agent {
        node {
            label nodes
        }
    }
    parameters {
        choice(choices: 'Normal\nPSRA\nLeakCanary\nTICS\nHPFortify\nJAVADocs', description: 'What type of build to build?', name: 'buildType')
    }
    triggers {
        parameterizedCron(param_string_cron)
    }
    environment {
        EPOCH_TIME = sh(script: 'date +%s', returnStdout: true).trim()
    }
    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '24'))
    }
    stages {
        stage('Initialize') {
            steps {
                echo "Node labels: ${nodes}"
                sh 'printenv'
                InitialiseBuild()
            }
        }

        stage('Build+test') {
            when {
                allOf {
                    not { expression { return params.buildType == 'PSRA' }}
                    not { expression { return params.buildType == 'HPFortify' }}
                }
            }
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    BuildAndUnitTest()
                }
            }
        }

        stage('Publish tests') {
            when {
                allOf {
                    not { expression { return params.buildType == 'PSRA' }}
                    not { expression { return params.buildType == 'HPFortify' }}
                }
            }
            steps {
                PublishUnitTestsResults()
            }
        }

        stage('Lint+Jacoco') {
            when {
                expression { return params.buildType == 'TICS' }
            }
            steps {
                BuildLint()
                PublishLintJacocoResults()
            }
        }

        stage('PSRAbuild') {
            when {
                allOf {
                    expression { return params.buildType == 'PSRA' }
                }
            }
            steps {
                sh '''#!/bin/bash -l
                    chmod -R 775 .
                    ./gradlew referenceApp:assemblePsraRelease
                '''
            }
        }

        stage('LeakCanarybuild') {
            when {
                allOf {
                    expression { return params.buildType == 'LeakCanary' }
                    anyOf { branch 'master'; branch 'develop'; branch 'release/platform_*' }
                }
            }
            steps {
                sh '''#!/bin/bash -l
                    chmod -R 775 .
                    ./gradlew referenceApp:assembleLeakCanary
                '''
                DeployingLeakCanaryArtifacts()
            }
        }

        stage('Publish to artifactory') {
            when {
                allOf {
                    not { expression { return params.buildType == 'PSRA' }}
                    not { expression { return params.buildType == 'HPFortify' }}
                    anyOf { branch 'master'; branch 'develop*'; branch 'release/platform_*' }
                }
            }
            steps {
                sh '''#!/bin/bash -l
                    set -e
                    ./gradlew --full-stacktrace saveResDep saveAllResolvedDependenciesGradleFormat zipDocuments artifactoryPublish :referenceApp:printArtifactoryApkPath :AppInfra:zipcClogs :securedblibrary:zipcClogs :registrationApi:zipcClogs :jump:zipcClogs :hsdp:zipcClogs :productselection:zipcClogs :digitalCareUApp:zipcClogs :digitalCare:zipcClogs :mya:zipcClogs

                    apkname=`xargs < apkname.txt`
                    dependenciesName=${apkname/.apk/.gradledependencies.gz}
                    ./gradlew -Dorg.gradle.parallel=false reportAllProjectDependencies | gzip -9 > ./allProjects.gradledependencies.gz
                    curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT "${dependenciesName}" -T ./allProjects.gradledependencies.gz
                '''
                archiveArtifacts 'Source/rap/Source/AppFramework/appFramework/*dependencies*.lock'
                DeployingConnectedTestsLogs()
            }
        }
        stage('java docs') {
            when {
                anyOf {
                    expression { return params.buildType == 'JAVADocs' }
                    expression { return params.buildType == 'TICS' }
                }
            }
            steps {
                GenerateJavaDocs()
                PublishJavaDocs()
                DeployingJavaDocs()
            }
        }

        stage('Publish PSRA apk') {
            when {
                allOf {
                    expression { return params.buildType == 'PSRA' }
                }
            }
            steps {
                sh '''#!/bin/bash -le
                    ./gradlew :referenceApp:printArtifactoryApkPath
                    apkname=`xargs < apkname.txt`
                    PSRA_APK_NAME=${apkname/.apk/_PSRA.apk}
                    curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT ${PSRA_APK_NAME} -T Source/rap/Source/AppFramework/appFramework/build/outputs/apk/psraRelease/referenceApp-psraRelease.apk
                '''
            }
        }

        stage('HPFortify') {
            when {
                allOf {
                    expression { return params.buildType == 'HPFortify' }
                }
            }
            steps {
                BuildHPFortify()
            }
        }

        stage('TICS') {
           when {
               expression { return params.buildType == 'TICS' }
          }
            steps {
                script {
                    echo "Running TICS..."
                    sh """#!/bin/bash -le
                        /mnt/tics/Wrapper/TICSMaintenance -project OPA-Android -branchname develop -branchdir .
                        /mnt/tics/Wrapper/TICSQServer -project OPA-Android -nosanity
                    """
                }
            }
        }

        stage('Upload Cucumber results to TFS') {
            when {
                allOf {
                    not { expression { return params.buildType == 'PSRA' }}
                    not { expression { return params.buildType == 'HPFortify' }}
                    anyOf { branch 'develop'; }
                }
            }
            steps {
                script {
                    build(job: 'Platform-Infrastructure/CucumberToTfs/master', 
                        parameters: [
                            string(name: 'JenkinsProjectName', value: env.JOB_NAME),
                            string(name: 'JenkinsProjectBuild', value: env.BUILD_ID),
                            string(name: 'TestPlan', value: 'In sprint_cml_bll_ews'),
                            string(name: 'TestSuitePath', value: 'Android/Automated Tests')
                        ], wait: false)
                }
            }
        }

        stage('Trigger E2E Test') {
            when {
                allOf {
                    not { expression { return params.buildType == 'LeakCanary' } }
                    anyOf { branch 'master'; branch 'develop'; branch 'release/platform_*' }
                }
            }
            steps {
                script {
                    APK_NAME = readFile("apkname.txt").trim()
                    if (params.buildType == 'PSRA') {
                        APK_NAME=sh(returnStdout: true, script: ${apkname/.apk/_PSRA.apk})
                    }
                    echo "APK_NAME = ${APK_NAME}"

                    def jobBranchName = "release_platform_1802.0.0"
                    if (BranchName =~ /develop.*/) {
                       jobBranchName = "develop"
                    }
                    echo "BranchName changed to ${jobBranchName}"

                    sh """#!/bin/bash -le
                        curl -X POST http://platform-ubuntu-ehv-002.ddns.htc.nl.philips.com:8080/job/Platform-Infrastructure/job/E2E_Tests/job/E2E_Android_${jobBranchName}/buildWithParameters?APKPATH=$APK_NAME
                    """
                }
            }
        }

        stage('LeakCanary E2E Test') {
            when {
                allOf {
                    expression { return params.buildType == 'LeakCanary' }
                    anyOf { branch 'master'; branch 'develop'; branch 'release/platform_*' }
                }
            }
            steps {
                script {
                    APK_NAME = readFile("apkname.txt").trim()
                    echo "APK_NAME = ${APK_NAME}"

                    def jobBranchName = BranchName.replace('/', '_')
                    echo "jobBranchName = ${jobBranchName}"
                    sh """#!/bin/bash -le
                        curl -X POST http://310256016:61a84d6f3e9343128dff5736ef68259e@cdp2-jenkins.htce.nl.philips.com:8080/job/Platform-Infrastructure/job/E2E_Tests/job/Reliability/job/LeakCanary_Android_develop/buildWithParameters?APKPATH=$APK_NAME
                    """
                }
            }
        }
    }
    post {
        always{
            deleteDir()
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: MailRecipient, sendToIndividuals: true])
        }
    }
}

def InitialiseBuild() {
    committerName = sh (script: "git show -s --format='%an' HEAD", returnStdout: true).trim()
    currentBuild.description = "Submitter: " + committerName + ";Node: ${env.NODE_NAME}"
    echo currentBuild.description

    if (params.buildType == 'TICS') {
        currentBuild.displayName = "${env.BUILD_NUMBER}-TICS"
    }
    if (params.buildType == 'PSRA') {
        currentBuild.displayName = "${env.BUILD_NUMBER}-PSRA"
    }
    if (params.buildType == 'HPFortify') {
        currentBuild.displayName = "${env.BUILD_NUMBER}-HPFortify"
    }
    echo currentBuild.displayName
}

def BuildAndUnitTest() {
    sh '''#!/bin/bash -l
        set -e
        chmod -R 755 .
        ./gradlew --refresh-dependencies --full-stacktrace assembleRelease \
            :AppInfra:cC \
            :AppInfra:testReleaseUnitTest \
            :uAppFwLib:testReleaseUnitTest \
            :securedblibrary:cC \
            :registrationApi:cC \
            :registrationApi:testReleaseUnitTest \
            :jump:cC \
            :jump:testReleaseUnitTest \
            :hsdp:cC \
            :hsdp:testReleaseUnitTest \
            :productselection:cC \
            :telehealth:testReleaseUnitTest \
            :bluelib:generateJavadoc \
            :bluelib:testReleaseUnitTest \
            :product-registration-lib:testReleaseUnitTest \
            :iap:testReleaseUnitTest \
            :digitalCareUApp:cC \
            :digitalCareUApp:testRelease \
            :digitalCare:cC \
            :digitalCare:testRelease \
            :commlib:testReleaseUnitTest \
            :commlib-testutils:testReleaseUnitTest \
            :commlib-integration-tests:testReleaseUnitTest \
            :commlib-ble:testReleaseUnitTest \
            :commlib-lan:testReleaseUnitTest \
            :commlib-cloud:testReleaseUnitTest \
            :commlib-api:testReleaseUnitTest \
            :mya:cC \
            :mya:testReleaseUnitTest \
            :catk:testReleaseUnitTest \
            :csw:testReleaseUnitTest \
            :pif:testReleaseUnitTest \
            :dataServices:testReleaseUnitTest \
            :dataServicesUApp:testReleaseUnitTest \
            :devicepairingUApp:testReleaseUnitTest \
            :ews-android:testReleaseUnitTest \
            :referenceApp:testReleaseUnitTest 
            
    '''

    archiveArtifacts 'Source/rap/Source/AppFramework/appFramework/build/outputs/apk/release/*.apk'
}

def GenerateJavaDocs(){
    sh '''#!/bin/bash -l
        set -e
        chmod -R 755 .
        ./gradlew :AppInfra:generateJavadocPublicApi \
        :securedblibrary:generateJavadocPublicApi \
        :registrationApi:generateJavadocPublicApi \
        :jump:generateJavadocPublicApi \
        :productselection:generateJavadocPublicApi \
        :dataServices:generateJavadocPublicApi \
        :pif:generateJavadocPublicApi \
        :csw:generateJavadocPublicApi \
        :catk:generateJavadocPublicApi \
        :mya:generateJavadocPublicApi \
        :digitalCare:generateJavadocPublicApi \
        :iap:generateJavadocPublicApi \
        :ews-android:generateJavadocPublicApi \
        :commlib-api:generateJavadocPublicApi \
        :commlib-ble:generateJavadocPublicApi \
        :commlib-lan:generateJavadocPublicApi \
        :commlib-cloud:generateJavadocPublicApi \
        :product-registration-lib:generateJavadocPublicApi \
        :telehealth:generateJavadocPublicApi \
        :referenceApp:generateJavadocPublicApi \
        :hsdp:generateJavadocPublicApi \
        :uid:generateJavadocPublicApi
'''
}


def BuildLint() {
    sh '''#!/bin/bash -l
        set -e
        #do not use -PenvCode=${JENKINS_ENV} since the option 'opa' is hardcoded in the archive
        ./gradlew :IconFont:lint \
         :AppInfra:lint \
         :uikitLib:lint \
         :securedblibrary:lint \
         :registrationApi:lint \
         :productselection:lint \
         :telehealth:lintRelease \
         :bluelib:lintDebug \
         :product-registration-lib:lint \
         :iap:lint \
         :digitalCare:lint \
         :cloudcontroller-api:lintDebug \
         :commlib:lintDebug \
         :mya:lint \
         :catk:lint \
         :csw:lint \
         :pif:lint \
         :dataServices:lintRelease \
         :devicepairingUApp:lint \
         :ews-android:lint \
         :ewsUApp:lint \
         :pushnotification:lintRelease \
         :themesettings:lintRelease
        #prx:lint and rap:lintRelease are not working and we are keeping it as known issues
    '''
}

def BuildHPFortify() {
    sh '''#!/bin/bash -l
        set -e
        chmod -R 755 .
        ./gradlew --refresh-dependencies
        echo "*** sourceanalyzer -b 001 -source 1.8 ./gradlew --full-stacktrace assembleRelease ***"
        sourceanalyzer -debug -verbose -b 001 -source 1.8 ./gradlew --full-stacktrace assembleRelease
        echo "*** sourceanalyzer -b 001 -scan -f results.fpr ***"
        sourceanalyzer -b 001 -scan -f results.fpr
        echo "*** fortifyclient -url https://fortify.philips.com/ssc ***"
        fortifyclient -url https://fortify.philips.com/ssc -authtoken 59f58b28-62a3-4770-87dd-e0cddb3c7bba uploadFPR -file results.fpr -project CDPP_CoCo -version plf_android
    '''
}

def DeployingLeakCanaryArtifacts() {
    boolean MasterBranch = (BranchName ==~ /master.*/)
    boolean ReleaseBranch = (BranchName ==~ /release\/platform_.*/)
    boolean DevelopBranch = (BranchName ==~ /develop.*/)

    def shellcommand = '''#!/bin/bash -l
        export BASE_PATH=`pwd`
        echo $BASE_PATH
        TIMESTAMP=`date -u +%Y%m%d%H%M%S`
        TIMESTAMPEXTENSION=".$TIMESTAMP"

        cd $BASE_PATH/Source/rap/Source/AppFramework/appFramework/build/outputs/apk
        PUBLISH_APK=false
        APK_NAME="RefApp_LeakCanary_"${TIMESTAMP}".apk"
        ARTIFACTORY_URL="http://artifactory-ehv.ta.philips.com:8082/artifactory"
        ARTIFACTORY_REPO="unknown"

        if [ '''+MasterBranch+''' = true ]
        then
            PUBLISH_APK=true
            ARTIFACTORY_REPO="platform-pkgs-opa-android-release"
        elif [ '''+ReleaseBranch+''' = true ]
        then
            PUBLISH_APK=true
            ARTIFACTORY_REPO="platform-pkgs-opa-android-stage"
        elif [ '''+DevelopBranch+''' = true ]
        then
            PUBLISH_APK=true
            ARTIFACTORY_REPO="platform-pkgs-opa-android-snapshot"
        else
            echo "Not published as build is not on a master, develop or release branch" . $BranchName
        fi

        if [ $PUBLISH_APK = true ]
        then
            mv referenceApp-leakCanary.apk $APK_NAME
            curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/referenceApp/LeakCanary/ -T $APK_NAME
            echo "$ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/referenceApp/LeakCanary/$APK_NAME" > $BASE_PATH/Source/rap/Source/AppFramework/apkname.txt
        fi

        if [ $? != 0 ]
        then
            exit 1
        else
            cd $BASE_PATH
        fi
    '''
    sh shellcommand
}

def DeployingConnectedTestsLogs() {
    boolean MasterBranch = (BranchName ==~ /master.*/)
    boolean ReleaseBranch = (BranchName ==~ /release\/platform_.*/)
    boolean DevelopBranch = (BranchName ==~ /develop.*/)

    def shellcommand = '''#!/bin/bash -l
        export BASE_PATH=`pwd`
        echo $BASE_PATH

        cd $BASE_PATH

        ARTIFACTORY_URL="http://artifactory-ehv.ta.philips.com:8082/artifactory"
        ARTIFACTORY_REPO="unknown"

        if [ '''+MasterBranch+''' = true ]
        then
            ARTIFACTORY_REPO="platform-logs-release-local"
        elif [ '''+ReleaseBranch+''' = true ]
        then
            ARTIFACTORY_REPO="platform-logs-release-local"
        elif [ '''+DevelopBranch+''' = true ]
        then
            ARTIFACTORY_REPO="platform-logs-snapshot-local"
        else
            echo "Not published as build is not on a master, develop or release branch" . $BranchName
        fi

        find . -name *logs.zip | while read LOGS;
        do
            curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/logs/ -T $LOGS
        done

        if [ $? != 0 ]
        then
            exit 1
        else
            cd $BASE_PATH
        fi
    '''
    sh shellcommand
}

def DeployingJavaDocs() {
    boolean MasterBranch = (BranchName ==~ /master.*/)
    boolean ReleaseBranch = (BranchName ==~ /release\/platform_.*/)
    boolean DevelopBranch = (BranchName ==~ /develop.*/)

    def shellcommand = '''#!/bin/bash -l
        export BASE_PATH=`pwd`
        echo $BASE_PATH

        cd $BASE_PATH

        ARTIFACTORY_URL="http://artifactory-ehv.ta.philips.com:8082/artifactory"
        ARTIFACTORY_REPO="unknown"

        if [ '''+MasterBranch+''' = true ]
        then
            ARTIFACTORY_REPO="platform-pkgs-android-stage-local"
        elif [ '''+ReleaseBranch+''' = true ]
        then
            ARTIFACTORY_REPO="platform-pkgs-android-release-local"
        elif [ '''+DevelopBranch+''' = true ]
        then
            ARTIFACTORY_REPO="platform-pkgs-android-snapshot-local"
        else
            echo "Not published JavaDoc as build is not on a master, develop or release branch" . $BranchName
        fi

        ./gradlew :bluelib:zipJavadoc :commlib-api:zipJavadoc :commlib-ble:zipJavadoc :commlib-cloud:zipJavadoc :commlib-lan:zipJavadoc  :AppInfra:zipJavadoc :uid:zipJavadoc :dataServices:zipJavadoc :digitalCare:zipJavadoc :ews-android:zipJavadoc :hsdp:zipJavadoc :iap:zipJavadoc :jump:zipJavadoc :mya:zipJavadoc :pif:zipJavadoc :product-registration-lib:zipJavadoc :productselection:zipJavadoc :prx:zipJavadoc :pushnotification:zipJavadoc :referenceApp:zipJavadoc :registrationApi:zipJavadoc :telehealth:zipJavadoc :catk:zipJavadoc :csw:zipJavadoc :referenceApp:printPlatformVersion
        platformVersion=`xargs < platformversion.txt`

        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/bluelib/$platformVersion/ -T ./Source/bll/Documents/External/bluelib-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/commlib-api/$platformVersion/ -T ./Source/cml/Documents/External/commlib-api-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/commlib-ble/$platformVersion/ -T ./Source/cml/Documents/External/commlib-ble-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/commlib-cloud/$platformVersion/ -T ./Source/cml/Documents/External/commlib-cloud-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/commlib-lan/$platformVersion/ -T ./Source/cml/Documents/External/commlib-lan-api.zip
        
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/AppInfra/$platformVersion/ -T ./Source/ail/Documents/External/AppInfra-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/catk/$platformVersion/ -T ./Source/csw/Documents/External/catk-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/csw/$platformVersion/ -T ./Source/csw/Documents/External/commlib-ble-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/dataServices/$platformVersion/ -T ./Source/dsc/Documents/External/dataServices-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/digitalCare/$platformVersion/ -T ./Source/dcc/Documents/External/digitalCare-api.zip
        
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/ews-android/$platformVersion/ -T ./Source/ews/Documents/External/ews-android-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/hsdp/$platformVersion/ -T ./Source/usr/Documents/External/hsdp-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/iap/$platformVersion/ -T ./Source/iap/Documents/External/iap-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/jump/$platformVersion/ -T ./Source/usr/Documents/External/jump-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/mya/$platformVersion/ -T ./Source/mya/Documents/External/mya-api.zip
        
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/pif/$platformVersion/ -T ./Source/ews/Documents/External/pif-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/product-registration-lib/$platformVersion/ -T ./Source/prg/Documents/External/product-registration-lib-api.zip
        curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/productselection/$platformVersion/ -T ./Source/pse/Documents/External/productselection-api.zip
        
        

        if [ $? != 0 ]
        then
            exit 1
        else
            cd $BASE_PATH
        fi
    '''
    sh shellcommand
}

def PublishUnitTestsResults() {
    junit allowEmptyResults: false, testResults: 'Source/ail/Source/Library/AppInfra/build/test-results/testReleaseUnitTest/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/ail/Source/Library/*/build/outputs/androidTest-results/*/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/ufw/Source/Library/*/build/test-results/*/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/sdb/Source/Library/**/build/outputs/androidTest-results/*/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/usr/Source/Library/**/build/test-results/**/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/usr/Source/Library/**/build/outputs/androidTest-results/*/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/pse/Source/Library/**/build/outputs/androidTest-results/*/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/ths/Source/Library/*/build/test-results/**/*.xml'
    junit allowEmptyResults: true,  testResults: 'Source/bll/**/testReleaseUnitTest/*.xml'
    junit allowEmptyResults: true,  testResults: 'Source/prg/Source/Library/*/build/test-results/**/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/iap/Source/Library/*/build/test-results/**/*.xml'
    junit allowEmptyResults: true,  testResults: 'Source/dcc/Source/DemoUApp/DemoUApp/build/reports/lint-results.xml'
    junit allowEmptyResults: true,  testResults: 'Source/dcc/Source/Library/digitalCare/build/test-results/**/*.xml'
    junit allowEmptyResults: true,  testResults: 'Source/cml/**/testReleaseUnitTest/*.xml'
    junit allowEmptyResults: true,  testResults: 'Source/mya/Source/DemoUApp/DemoUApp/build/test-results/**/*.xml'
    junit allowEmptyResults: true,  testResults: 'Source/mya/Source/Library/*/build/test-results/**/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/dsc/Source/Library/*/build/test-results/**/*.xml'
    junit allowEmptyResults: true,  testResults: 'Source/dpr/Source/DemoUApp/*/build/test-results/*/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/ews/Source/Library/ews-android/build/test-results/*/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/rap/Source/AppFramework/*/build/test-results/*/*.xml'
    junit allowEmptyResults: false, testResults: 'Source/cml/Source/Library/commlib-integration-tests/build/test-results/*/*.xml'

    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/ail/Source/Library/AppInfra/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'ail connected tests'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/ufw/Source/Library/uAppFwLib/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'ufw unit test release'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/sdb/Source/Library/securedblibrary/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'sdb connected tests'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/usr/Source/Library/RegistrationApi/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'usr unit test release'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/usr/Source/Library/RegistrationApi/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'usr connected tests RegistrationApi'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/usr/Source/Library/jump/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'usr connected tests Jump'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/usr/Source/Library/hsdp/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'usr connected tests hsdp'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/pse/Source/Library/productselection/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'pse connected tests'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/ths/Source/Library/thsuapp/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'ths unit test release'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true,  keepAll: true, reportDir: 'Source/bll/Documents/External/bluelib-api', reportFiles: 'index.html', reportName: 'bll Bluelib Public API'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true,  keepAll: true, reportDir: 'Source/bll/Documents/External/bluelib-plugin-api', reportFiles: 'index.html', reportName: 'bll Bluelib Plugin API'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true,  keepAll: true, reportDir: 'Source/bll/Source/ShineLib/shinelib/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'bll unit test release'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/prg/Source/Library/product-registration-lib/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'prg unit test release'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/iap/Source/Library/iap/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'iap unit test release'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/dcc/Source/Library/digitalCare/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'dcc unit test release'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/cml/Source/Library/commlib-integration-tests/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'cml integration tests'])
    publishHTML([allowMissing: true,  alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/mya/Source/DemoUApp/DemoUApp/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'mya DemoUApp - release test'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/csw/Source/Library/catk/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'catk'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/csw/Source/Library/csw/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'csw'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/mya/Source/Library/mya/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'mya-mya'])
    publishHTML([allowMissing: true,  alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/pif/Source/Library/chi/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'pif'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/dsc/Source/Library/dataServices/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'dsc unit test release'])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/rap/Source/AppFramework/appFramework/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'rap Release UnitTest'])

    for (lib in ["commlib-api", "commlib-ble", "commlib-lan", "commlib-cloud"]) {
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/cml/Source/Library/${lib}/build/reports/tests/testReleaseUnitTest", reportFiles: 'index.html', reportName: "cml $lib unit test release"])
    }

    def cucumber_path = 'Source/cml/Source/Library/commlib-integration-tests/build/cucumber-reports'
    if (fileExists("$cucumber_path/report.json")) {
        step([$class: 'CucumberReportPublisher', jsonReportDirectory: cucumber_path, fileIncludePattern: '*.json'])
        archiveArtifacts artifacts: cucumber_path+'/*.json', fingerprint: true, onlyIfSuccessful: true
    } else {
        echo 'No Cucumber result found, nothing to publish.'
    }
}

def PublishJavaDocs(){
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/ail/Documents/External/AppInfra-api", reportFiles: 'index.html', reportName: "AppInfra Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/csw/Documents/External/catk-api", reportFiles: 'index.html', reportName: "catk Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/csw/Documents/External/csw-api", reportFiles: 'index.html', reportName: "csw Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/dsc/Documents/External/dataServices-api", reportFiles: 'index.html', reportName: "dsc Data services Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/dcc/Documents/External/digitalCare-api", reportFiles: 'index.html', reportName: "dcc Digital careLibrary API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/ews/Documents/External/ews-android-api", reportFiles: 'index.html', reportName: "ews-android Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/usr/Documents/External/hsdp-api", reportFiles: 'index.html', reportName: "hsdp Hsdp Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/iap/Documents/External/iap-api", reportFiles: 'index.html', reportName: "iapp Inapp purchase Library API documentation"])

    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/mya/Documents/External/mya-api", reportFiles: 'index.html', reportName: "mya My account Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/pif/Documents/External/pif-api", reportFiles: 'index.html', reportName: "pif Platform Infrastructure Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/prg/Documents/External/product-registration-lib-api", reportFiles: 'index.html', reportName: "Product registration library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/pse/Documents/External/productselection-api", reportFiles: 'index.html', reportName: "Product selection Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/rap/Documents/External/referenceApp-api", reportFiles: 'index.html', reportName: "Reference app API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/usr/Documents/External/registrationApi-api", reportFiles: 'index.html', reportName: "User registration Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/sdb/Documents/External/securedblibrary-api", reportFiles: 'index.html', reportName: "Secure db Library API documentation"])
    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/ths/Documents/External/telehealth-api", reportFiles: 'index.html', reportName: "Telehealth Library API documentation"])

    publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/uid/Documents/External/uid-api", reportFiles: 'index.html', reportName: "UID Library API documentation"])

    for (lib in ["commlib-api", "commlib-ble", "commlib-lan", "commlib-cloud"]) {
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/cml/Documents/External/$lib-api", reportFiles: 'index.html', reportName: "cml $lib API documentation"])
    }
}

def PublishLintJacocoResults() {
    step([$class: 'JacocoPublisher', execPattern: 'Source/bll/**/*.exec', classPattern: 'Source/bll/**/classes', sourcePattern: 'Source/bll/**/src/main/java', exclusionPattern: 'Source/bll/**/R.class,Source/bll/**/R$*.class,Source/bll/**/BuildConfig.class,Source/bll/**/Manifest*.*,Source/bll/**/*Activity*.*,Source/bll/**/*Fragment*.*'])
    step([$class: 'JacocoPublisher', execPattern: 'Source/ews/**/*.exec', sourcePattern: 'Source/ews/**/src/main/java', exclusionPattern: 'Source/ews/**/R.class, Source/ews/**/R$*.class, Source/ews/*/BuildConfig.class, Source/ews/**/Manifest*.*, Source/ews/**/*_Factory.class, Source/ews/**/*_*Factory.class , Source/ews/**/Dagger*.class, Source/ews/**/databinding/**/*.class, Source/ews/**/*Activity*.*, Source/ews/**/*Fragment*.*, Source/ews/**/*Service*.*, Source/ews/**/*ContentProvider*.*'])
    step([$class: 'JacocoPublisher', execPattern: 'Source/cml/**/*.exec', classPattern: 'Source/cml/**/classes', sourcePattern: 'Source/cml/**/src/main/java', exclusionPattern: 'Source/cml/**/R.class,Source/cml/**/R$*.class,Source/cml/**/BuildConfig.class,Source/cml/**/Manifest*.*,Source/cml/**/*Activity*.*,Source/cml/**/*Fragment*.*,Source/cml/**/*Test*.*'])
    androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '', shouldDetectModules: true, unHealthy: '', unstableTotalHigh: ''
}
