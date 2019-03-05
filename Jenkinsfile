#!/usr/bin/env groovy
// please look at: https://jenkins.io/doc/book/pipeline/syntax/
BranchName = env.BRANCH_NAME

/**Cron string to define interval time at which pipeline should be retriggered.
 * Applicable for develop branch and build type is PSRA at 8:00 pm to 9:00 pm
 * Applicable for develop branch and build type is Java API doc at 9:00 pm to 10:00 pm
 */
String param_string_cron = BranchName == "develop" ? "H H(20-21) * * * %buildType=PSRA \nH H(21-22) * * * %GenerateAPIDocs=true" : ""

//label for pipeline
def nodes = 'test'

if (BranchName == "develop") {
    nodes = nodes + " && TICS"
}

/**
 * Top-level block (pipeline) which enclosed all valid declarative pipelines.
 */
pipeline {

    /**
     * agent section defined at top level specifies that the entire pipeline will execute in jenkins environment.
     */
    agent {
        node {
            label nodes //execute the pipeline, on a agent available in jenkins environment with specified label.
        }
    }

    /**
     * The parameters directives provide the list of parameters which a user should provide when triggering the pipeline.
     * The values for this user specified parameters are made available to pipeline steps via build params in jenkins.
     */
    parameters {
        //specify values for buildType (Normal/PSRA/LeakCanary/HPFortify/Javadocs).
        choice(choices: 'Normal\nPSRA\nLeakCanary\nHPFortify\nJAVADocs', description: 'What type of build to build?', name: 'buildType')
    }

    /**
     * Cron is a linux utility tool to schedule job at certain interval time.
     * ParametrizedCron will trigger pipeline with parameters sent in cron string.
     */
    triggers {
        parameterizedCron(param_string_cron)
    }

    /**
     *The environment directives specifies a key-value pair (EPOCH_TIME) which will defined as environment variables
     * for all the steps.
     */
    environment {
        EPOCH_TIME = sh(script: 'date +%s', returnStdout: true).trim()
    }

    /**
     *The options directives allows configuring Pipeline-specific options from within pipeline itself.
     */
    options {
        timestamps() //Prepend all console output generated by the pipeline run with the time at which the line was emitted.
        buildDiscarder(logRotator(numToKeepStr: '24')) //Persists artifacts and console output for the specific number of recent pipeline runs.
        skipDefaultCheckout(true)  //skip checking out code from source control by default in the agent directives.
    }

    /**
     * stages contains a sequence of one or more stage directives. The bulk of the work described by a pipeline will be located
     * under stages section.Please refer jenkins to view all stages.
     */
    stages {

        //Perform initialization
        stage('Initialize') {
            steps {
                echo "Node labels: ${nodes}"    //print node labels to console output
                sh 'printenv'
                deleteDir()     //Recursively delete current directory from workspace.
                sh """
                    if [ -d ~/workspace/master ]; then
                        git clone ~/workspace/master ${WORKSPACE}
                    fi
                """

                //checkout current branch where git repo URL is specified
                // TODO: Please check what is credentials id
                checkout([$class: 'GitSCM', branches: [[name: '*/'+env.BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CloneOption', depth: 0, honorRefspec: true, noTags: false, reference: '', shallow: false, timeout: 20]], userRemoteConfigs: [[credentialsId: 'd51576c2-35b7-4136-a1fa-5a638fa03b01', url: 'git@ssh.dev.azure.com:v3/PhilipsAgile/8.0%20DC%20Innovations%20%28IET%29/mobile-plf-android', refspec: '+refs/heads/'+env.BRANCH_NAME+':refs/remotes/origin/'+env.BRANCH_NAME]]])
                sh 'printenv'
                InitialiseBuild()
            }
        }

        //stage to refresh dependencies,clean,build components and UnitTest
        stage('Commit') {
            //Not necessary to rebuild with unit testing for below build types
            when {
                allOf {
                    not { expression { return params.buildType == 'LeakCanary' }}
                    not { expression { return params.buildType == 'HPFortify' }}
                    not { expression { return params.buildType == 'JAVADocs' }}
                }
            }
            steps {
                //Sets a timeout of 1 hour for Build and unit test.
                timeout(time: 1, unit: 'HOURS') {
                    BuildAndUnitTest()
                }
            }

            // post section defines to run additional step PublishUnitTestsResults() on completion of current stage 'Commit'.
            post {
                always{
                    PublishUnitTestsResults()
                }
            }
        }

        //stage to publish aar to artifactory
        stage('Publish to artifactory') {
            //Not required to publish to artifactory for below build types
            when {
                allOf {
                    not { expression { return params.buildType == 'PSRA' }}
                    not { expression { return params.buildType == 'HPFortify' }}
                    not { expression { return params.buildType == 'JAVADocs' }}

                    //publish to artifactory only for master,develop and release/platform_*
                    anyOf { branch 'master'; branch 'develop*'; branch 'release/platform_*' }
                }
            }
            //execute gradle task to save resource dependencies,to save all resolved dependencies in gradle format
            // to zip documents, publish artifactory for specified component and to zip connected test logs in this order.
            //fetch apk name from apkname.txt file which is generated in reference app's build.gradle
            //flushing all gradle dependencies to allprojects.gradledependencies.gz
            steps {
                sh '''#!/bin/bash -l
                    set -e
                    ./gradlew --full-stacktrace saveResDep saveAllResolvedDependenciesGradleFormat zipDocuments artifactoryPublish :referenceApp:printArtifactoryApkPath :AppInfra:zipcClogs :securedblibrary:zipcClogs :registrationApi:zipcClogs :productselection:zipcClogs :digitalCareUApp:zipcClogs :digitalCare:zipcClogs 

                    apkname=`xargs < apkname.txt`
                    dependenciesName=${apkname/.apk/.gradledependencies.gz}
                    ./gradlew -Dorg.gradle.parallel=false reportAllProjectDependencies | gzip -9 > ./allProjects.gradledependencies.gz
                    curl -L -u readerwriter:APBcfHoo7JSz282DWUzMVJfUsah -X PUT "${dependenciesName}" -T ./allProjects.gradledependencies.gz
                '''

                //archive all .lock files from below directory
                archiveArtifacts 'Source/rap/Source/AppFramework/appFramework/*dependencies*.lock'
                DeployingConnectedTestsLogs()       //deploy connected test logs
            }
        }

//        stage('Trigger Incontext Test') {
//            when {
//                allOf {
//                    not { expression { return params.buildType == 'LeakCanary' } }
//                    not { expression { return params.buildType == 'PSRA' } }
//                    not { expression { return params.buildType == 'JAVADocs' } }
//                    anyOf { branch 'develop'; branch 'release/platform_*' }
//                }
//            }
//            steps {
//                script {
//                    build job: 'Platform-Infrastructure/IncontextTest/master', parameters: [string(name: 'branchname', value:BranchName), string(name: 'triggered_from', value:'Platform'), string(name: 'os_platform', value:'Android')], wait: false
//                }
//            }
//        }

//        stage('Acceptance') {
//            when {
//                allOf {
//                    not { expression { return params.buildType == 'PSRA' }}
//                    not { expression { return params.buildType == 'HPFortify' }}
//                }
//            }
//            steps {
//                timeout(time: 1, unit: 'HOURS') {
//                    AcceptanceTest()
//                }
//            }
//            post{
//                always{
//                    PublishAcceptanceTestsResults()
//                }
//            }
//        }

//        stage('Capacity') {
//            when {
//                allOf {
//                    not { expression { return params.buildType == 'PSRA' }}
//                    not { expression { return params.buildType == 'HPFortify' }}
//                }
//            }
//            steps {
//                CapacityTest()
//            }
//        }

        //stage to run lint and jacoco
        stage('Lint+Jacoco') {
            steps {
                BuildLint()
            }
        }

        //stage to run PSRA build
        stage('PSRAbuild') {
            //steps will run only for buildType PSRA
            when {
                allOf {
                    expression { return params.buildType == 'PSRA' }
                }
            }
            //execute command to build assemblePsraRelease for reference app
            steps {
                sh '''#!/bin/bash -l
                    chmod -R 775 .
                    ./gradlew referenceApp:assemblePsraRelease
                '''
            }
        }

        //stage to run leak canary build
        stage('LeakCanarybuild') {
            //steps will run only for LeakCanary build
            //steps will run for master,develop or release/platform_* branch
            when {
                allOf {
                    expression { return params.buildType == 'LeakCanary' }
                    anyOf { branch 'master'; branch 'develop'; branch 'release/platform_*' }
                }
            }
            //execute command to build assembleLeakCanary for reference app
            steps {
                sh '''#!/bin/bash -l
                    chmod -R 775 .
                    ./gradlew referenceApp:assembleLeakCanary
                '''
                DeployingLeakCanaryArtifacts()      //deploy leakcanary build
            }
        }

        //stage to run JAVADocs build
        stage('java docs') {
            //steps will execute only for JAVADocs build
            when {
                anyOf {
                    expression { return params.buildType == 'JAVADocs' }
                }
            }
            steps {
                GenerateJavaDocs()  //Generate java docs
                PublishJavaDocs()   //Publish java docs
                DeployingJavaDocs() //Deploy java docs
            }
        }

        //stage to publish PSRA apk
        stage('Publish PSRA apk') {
            //steps will executed only for PSRA build
            when {
                allOf {
                    expression { return params.buildType == 'PSRA' }
                }
            }

            //print referenceApp artifactory path
            //fetch apk name from apkname.txt file which is generated in reference app's build.gradle
            //flushing psra apk to specified path
            steps {
                sh '''#!/bin/bash -le
                    ./gradlew :referenceApp:printArtifactoryApkPath
                    apkname=`xargs < apkname.txt`
                    PSRA_APK_NAME=${apkname/.apk/_PSRA.apk}
                    curl -L -u 320049003:#W3llc0m3 -X PUT ${PSRA_APK_NAME} -T Source/rap/Source/AppFramework/appFramework/build/outputs/apk/psraRelease/referenceApp-psraRelease.apk
                '''

                //archive referenceApp-psraRelease apk from below path
                archiveArtifacts 'Source/rap/Source/AppFramework/appFramework/build/outputs/apk/psraRelease/referenceApp-psraRelease.apk'
            }
        }

        //stage to run HPFortify build
        stage('HPFortify') {
            when {
                allOf {
                    expression { return params.buildType == 'HPFortify' }
                }
            }
            steps {
                BuildHPFortify()   //build HPFortify
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
                          APK_NAME=APK_NAME.replace('.apk', '_PSRA.apk')
                      }
                      echo "APK_NAME = ${APK_NAME}"

                      def jobBranchName = "release_platform_1805"
                      if (BranchName =~ /develop.*/) {
                          jobBranchName = "develop"
                      }
                      echo "BranchName changed to ${jobBranchName}"

                      sh """#!/bin/bash -le
                          curl -X POST curl -X POST http://pil.ietjenkins.pic.philips.com:8080/job/E2E_Tests/job/E2E_Android_${jobBranchName}/buildWithParameters?APKPATH=$APK_NAME"""
                      
                  }
              }
          }

//        stage('LeakCanary E2E Test') {
//            when {
//                allOf {
//                    expression { return params.buildType == 'LeakCanary' }
//                    anyOf { branch 'master'; branch 'develop'; branch 'release/platform_*' }
//                }
//            }
//            steps {
//                script {
//                    APK_NAME = readFile("apkname.txt").trim()
//                    echo "APK_NAME = ${APK_NAME}"
//
//                    def jobBranchName = BranchName.replace('/', '_')
//                    echo "jobBranchName = ${jobBranchName}"
//                    sh """#!/bin/bash -le
//                        curl -X POST http://310256016:61a84d6f3e9343128dff5736ef68259e@cdp2-jenkins.htce.nl.philips.com:8080/job/Platform-Infrastructure/job/E2E_Tests/job/Reliability/job/LeakCanary_Android_develop/buildWithParameters?APKPATH=$APK_NAME
//                    """
//                }
//            }
//        }
    }

    /**
     * Delete dir and send notification on completion of all above stages
     */
    post {
        always{
            deleteDir()
            notifyBuild(currentBuild.result) //send notification

        }
    }
}

/**
 * Notify build status
 * @param buildStatus
 */
def notifyBuild(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus =  buildStatus ?: 'aborted' || 'failure' || 'fixed' || 'unstable'
   // Default values
   def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
   def details = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]': Check console output at ${env.BUILD_URL}"

    //send notification email
    emailext (
        subject: subject,
        body: details,
        to: "dl_iet_amaron@philips.com, dl_iet_exide@philips.com, rallapalli.prasad@philips.com"
    )
}

/**
 *Initialize build
 */
def InitialiseBuild() {
    committerName = sh (script: "git show -s --format='%an' HEAD", returnStdout: true).trim()   //fetch committer name
    currentBuild.description = "Submitter: " + committerName + ";Node: ${env.NODE_NAME}"        //append committer name and node to build description
    echo currentBuild.description  //print description to console output

    //append buildType with BUILD_NUMBER for PSRA and HPFortify
    if (params.buildType == 'PSRA') {
        currentBuild.displayName = "${env.BUILD_NUMBER}-PSRA"
    }
    if (params.buildType == 'HPFortify') {
        currentBuild.displayName = "${env.BUILD_NUMBER}-HPFortify"
    }
    echo currentBuild.displayName   //print current build name to console output
}

/**
 * Refresh dependencies, clean and build modules and UnitTest.
  */
def BuildAndUnitTest() {
    sh '''#!/bin/bash -l
        set -e
        chmod -R 755 .
        ./gradlew --refresh-dependencies --full-stacktrace clean assembleRelease \
            :AppInfra:cC \
            :AppInfra:testReleaseUnitTest \
            :uAppFwLib:testReleaseUnitTest \
            :registrationApi:cC \
            :registrationApi:testReleaseUnitTest \
            :productselection:cC \
            :product-registration-lib:testReleaseUnitTest \
            :iap:testReleaseUnitTest \
            :digitalCareUApp:cC \
            :digitalCareUApp:testRelease \
            :digitalCare:cC \
            :digitalCare:testRelease \
            :mya:cC \
            :mya:testReleaseUnitTest \
            :pif:testReleaseUnitTest \
            :referenceApp:testReleaseUnitTest 
            
    '''

    //archive the apk type files from below source
    archiveArtifacts 'Source/rap/Source/AppFramework/appFramework/build/outputs/apk/release/*.apk'
}

/**
 * Perform acceptance test.
 */
def AcceptanceTest() {
    sh '''#!/bin/bash -l
        set -e
        chmod -R 755 .
        ./gradlew --refresh-dependencies --full-stacktrace assembleRelease \
            :AppInfra:cC \
            :securedblibrary:cC \
            :registrationApi:cC \
            :productselection:cC \
            :digitalCareUApp:cC \
            :digitalCare:cC \
            :mya:cC
    '''
}

//def CapacityTest() {
//    sh '''#!/bin/bash -l
//        set -e
//        chmod -R 755 .
//        echo "Nothing here yet..."
//    '''
//}

/**
 * Generate java docs for specific components
  */
def GenerateJavaDocs(){
    sh '''#!/bin/bash -l
        set -e
        chmod -R 755 .
        ./gradlew :AppInfra:generateJavadocPublicApi \
        :securedblibrary:generateJavadocPublicApi \
        :registrationApi:generateJavadocPublicApi \
        :productselection:generateJavadocPublicApi \
        :pif:generateJavadocPublicApi \
        :digitalCare:generateJavadocPublicApi \
        :iap:generateJavadocPublicApi \
        :product-registration-lib:generateJavadocPublicApi \
        :referenceApp:generateJavadocPublicApi \
'''
}

/**
 * Generate lint issues for specific components.
 */
def BuildLint() {
    sh '''#!/bin/bash -l
        set -e
        #do not use -PenvCode=${JENKINS_ENV} since the option 'opa' is hardcoded in the archive
        ./gradlew  \
         :AppInfra:lint \
         :securedblibrary:lint \
         :registrationApi:lint \
         :productselection:lint \
         :product-registration-lib:lint \
         :iap:lint \
         :digitalCare:lint \
         :mya:lint \
         :pif:lint \
         :themesettings:lintRelease
        #prx:lint and rap:lintRelease are not working and we are keeping it as known issues
    '''
}

/**
 *
 * Build HPFOrtify buildType
 */
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
        fortifyclient -url https://fortify.philips.com/ssc -authtoken b7f82273-bec3-4cbf-a2df-539274e37cca uploadFPR -file results.fpr -project CDPP_CoCo -version plf_android
    '''
}

/**
 * Deploy leakcanary build to artifactory
 */
def DeployingLeakCanaryArtifacts() {
    boolean MasterBranch = (BranchName ==~ /master.*/)
    boolean ReleaseBranch = (BranchName ==~ /release\/platform_.*/)
    boolean DevelopBranch = (BranchName ==~ /develop.*/)

    //Construct command to deploy build
    def shellcommand = '''#!/bin/bash -l
        export BASE_PATH=`pwd`
        echo $BASE_PATH
        TIMESTAMP=`date -u +%Y%m%d%H%M%S`
        TIMESTAMPEXTENSION=".$TIMESTAMP"

        cd $BASE_PATH/Source/rap/Source/AppFramework/appFramework/build/outputs/apk
        PUBLISH_APK=false
        APK_NAME="RefApp_LeakCanary_"${TIMESTAMP}".apk"
        ARTIFACTORY_URL="https://artifactory-ehv.ta.philips.com/artifactory"
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
            curl -L -u 320049003:#W3llc0m3 -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/referenceApp/LeakCanary/ -T $APK_NAME
            echo "$ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/referenceApp/LeakCanary/$APK_NAME" > $BASE_PATH/Source/rap/Source/AppFramework/apkname.txt
        fi

        if [ $? != 0 ]
        then
            exit 1
        else
            cd $BASE_PATH
        fi
    '''
    sh shellcommand //execute shell command to deploy leakcanary build
}


/**
 * Deploying connected test logs while publish to artifactory
 */
def DeployingConnectedTestsLogs() {
    boolean MasterBranch = (BranchName ==~ /master.*/)
    boolean ReleaseBranch = (BranchName ==~ /release\/platform_.*/)
    boolean DevelopBranch = (BranchName ==~ /develop.*/)

    //construct shell command to publish logs to artifactory
    //If branch is develop,master or release then only publish log
    def shellcommand = '''#!/bin/bash -l
        export BASE_PATH=`pwd`
        echo $BASE_PATH

        cd $BASE_PATH

        ARTIFACTORY_URL="https://artifactory-ehv.ta.philips.com/artifactory"
        ARTIFACTORY_REPO="unknown"

        if [ '''+MasterBranch+''' = true ]
        then
            ARTIFACTORY_REPO="iet-mobile-android-release-local"
        elif [ '''+ReleaseBranch+''' = true ]
        then
            ARTIFACTORY_REPO="iet-mobile-android-release-local"
        elif [ '''+DevelopBranch+''' = true ]
        then
            ARTIFACTORY_REPO="iet-mobile-android-snapshot-local"
        else
            echo "Not published as build is not on a master, develop or release branch" . $BranchName
        fi

        find . -name *logs.zip | while read LOGS;
        do
            curl -L -u 320049003:#W3llc0m3 -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/logs/ -T $LOGS
        done

        if [ $? != 0 ]
        then
            exit 1
        else
            cd $BASE_PATH
        fi
    '''
    sh shellcommand     //execute command to publish connected test log
}

/**
 *Deploying java docs for specified components
 */
def DeployingJavaDocs() {
    boolean MasterBranch = (BranchName ==~ /master.*/)
    boolean ReleaseBranch = (BranchName ==~ /release\/platform_.*/)
    boolean DevelopBranch = (BranchName ==~ /develop.*/)

    //construct shell command to zip java doc and deploy
    def shellcommand = '''#!/bin/bash -l
        export BASE_PATH=`pwd`
        echo $BASE_PATH

        cd $BASE_PATH

        ARTIFACTORY_URL="https://artifactory-ehv.ta.philips.com/artifactory"
        ARTIFACTORY_REPO="unknown"

        if [ '''+MasterBranch+''' = true ]
        then
            ARTIFACTORY_REPO="iet-mobile-android-release-local"
        elif [ '''+ReleaseBranch+''' = true ]
        then
            ARTIFACTORY_REPO="iet-mobile-android-release-local"
        elif [ '''+DevelopBranch+''' = true ]
        then
            ARTIFACTORY_REPO="iet-mobile-android-snapshot-local"
        else
            echo "Not published JavaDoc as build is not on a master, develop or release branch" . $BranchName
        fi

        ./gradlew  :AppInfra:zipJavadoc :digitalCare:zipJavadoc :iap:zipJavadoc :pif:zipJavadoc :product-registration-lib:zipJavadoc :productselection:zipJavadoc :prx:zipJavadoc  :referenceApp:zipJavadoc :registrationApi:zipJavadoc :referenceApp:printPlatformVersion
        platformVersion=`xargs < platformversion.txt`
 
        curl -L -u 320049003:#W3llc0m3 -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/AppInfra/$platformVersion/ -T ./Source/ail/Documents/External/AppInfra-api.zip
        curl -L -u 320049003:#W3llc0m3 -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/digitalCare/$platformVersion/ -T ./Source/dcc/Documents/External/digitalCare-api.zip
        
        curl -L -u 320049003:#W3llc0m3 -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/iap/$platformVersion/ -T ./Source/iap/Documents/External/iap-api.zip
        
        curl -L -u 320049003:#W3llc0m3 -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/product-registration-lib/$platformVersion/ -T ./Source/prg/Documents/External/product-registration-lib-api.zip
        curl -L -u 320049003:#W3llc0m3 -X PUT $ARTIFACTORY_URL/$ARTIFACTORY_REPO/com/philips/cdp/productselection/$platformVersion/ -T ./Source/pse/Documents/External/productselection-api.zip
        
        

        if [ $? != 0 ]
        then
            exit 1
        else
            cd $BASE_PATH
        fi
    '''
    sh shellcommand //execute shell command to deploy java docs
}

/**
 * publishing junit test case report
 */
def PublishUnitTestsResults() {
    junit allowEmptyResults: true, testResults: 'Source/ail/Source/Library/AppInfra/build/test-results/testReleaseUnitTest/*.xml'
    junit allowEmptyResults: true, testResults: 'Source/ufw/Source/Library/*/build/test-results/*/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/ufw/Source/Library/uAppFwLib/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'ufw unit test release'])

    junit allowEmptyResults: true, testResults: 'Source/usr/Source/Library/**/build/test-results/**/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/usr/Source/Library/RegistrationApi/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'usr unit test release'])


    junit allowEmptyResults: true,  testResults: 'Source/prg/Source/Library/*/build/test-results/**/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/prg/Source/Library/product-registration-lib/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'prg unit test release'])

    junit allowEmptyResults: true, testResults: 'Source/iap/Source/Library/*/build/test-results/**/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/iap/Source/Library/iap/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'iap unit test release'])

    junit allowEmptyResults: true,  testResults: 'Source/dcc/Source/DemoUApp/DemoUApp/build/reports/lint-results.xml'
    junit allowEmptyResults: true,  testResults: 'Source/dcc/Source/Library/digitalCare/build/test-results/**/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/dcc/Source/Library/digitalCare/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'dcc unit test release'])


    junit allowEmptyResults: true,  testResults: 'Source/dpr/Source/DemoUApp/*/build/test-results/*/*.xml'
    junit allowEmptyResults: true, testResults: 'Source/rap/Source/AppFramework/*/build/test-results/*/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/rap/Source/AppFramework/appFramework/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'rap Release UnitTest'])
    publishHTML([allowMissing: true,  alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/pif/Source/Library/chi/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'pif'])
}

/**
 *publishing acceptance junit test case report
 */
def PublishAcceptanceTestsResults() {
    junit allowEmptyResults: true, testResults: 'Source/ail/Source/Library/*/build/outputs/androidTest-results/*/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/ail/Source/Library/AppInfra/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'ail connected tests'])

    junit allowEmptyResults: true, testResults: 'Source/sdb/Source/Library/**/build/outputs/androidTest-results/*/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/sdb/Source/Library/securedblibrary/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'sdb connected tests'])

    junit allowEmptyResults: true, testResults: 'Source/usr/Source/Library/**/build/outputs/androidTest-results/*/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/usr/Source/Library/RegistrationApi/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'usr connected tests RegistrationApi'])

    junit allowEmptyResults: true, testResults: 'Source/pse/Source/Library/**/build/outputs/androidTest-results/*/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/pse/Source/Library/productselection/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'pse connected tests'])

    junit allowEmptyResults: true, testResults: 'Source/dcc/Source/Library/**/build/outputs/androidTest-results/*/*.xml'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/dcc/Source/Library/digitalCare/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'dcc connected tests'])

}

/**
 * Publish Java Documents
 *
 * allowMissing: If true, build will not fail on missing report.
 * alwaysLinkToLastBuild: If this control and "Keep past HTML reports" are checked, publish the link on project level even if build failed.
 * reportDir: The path to the html report directory relative to the work space.
 * reportFiles: File to provide link inside the report directory.
 * reportName: The name of the report to display.
 */
def PublishJavaDocs(){
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/ail/Documents/External/AppInfra-api", reportFiles: 'index.html', reportName: "AppInfra Library API documentation"])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/dcc/Documents/External/digitalCare-api", reportFiles: 'index.html', reportName: "dcc Digital careLibrary API documentation"])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/iap/Documents/External/iap-api", reportFiles: 'index.html', reportName: "iapp Inapp purchase Library API documentation"])

    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/pif/Documents/External/pif-api", reportFiles: 'index.html', reportName: "pif Platform Infrastructure Library API documentation"])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/prg/Documents/External/product-registration-lib-api", reportFiles: 'index.html', reportName: "Product registration library API documentation"])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/pse/Documents/External/productselection-api", reportFiles: 'index.html', reportName: "Product selection Library API documentation"])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/rap/Documents/External/referenceApp-api", reportFiles: 'index.html', reportName: "Reference app API documentation"])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/usr/Documents/External/registrationApi-api", reportFiles: 'index.html', reportName: "User registration Library API documentation"])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "Source/sdb/Documents/External/securedblibrary-api", reportFiles: 'index.html', reportName: "Secure db Library API documentation"])
}

