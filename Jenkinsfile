#!/usr/bin/env groovy                                                                                                           

BranchName = env.BRANCH_NAME
JENKINS_ENV = env.JENKINS_ENV

properties([
    [$class: 'ParametersDefinitionProperty', parameterDefinitions: [[$class: 'StringParameterDefinition', defaultValue: '', description: 'triggerBy', name : 'triggerBy']]],
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '10']]
])

def MailRecipient = 'DL_CDP2_Callisto@philips.com, DL_ph_cdp2_iap@philips.com'
def errors = []

node ('android&&docker') {
    timestamps {
        if (BranchName =~ /master|develop|release\/platform_.*/) {
            stage('Trigger OPA Build'){
                build job: "Platform-Infrastructure/opa-android/${BranchName}", wait: false
            }
        }
        else
        {
            try {
                stage ('Checkout') {
                    def jobBaseName = "${env.JOB_BASE_NAME}".replace('%2F', '/')
                    if (env.BRANCH_NAME != jobBaseName)
                    { 
                       echo "ERROR: Branches DON'T MATCH"
                       echo "Branchname  = " + env.BRANCH_NAME
                       echo "jobBaseName = " + jobBaseName
                       exit 1
                    }

                    checkout([$class: 'GitSCM', branches: [[name: '*/'+BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'PruneStaleBranch'], [$class: 'LocalBranch', localBranch: "**"]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://tfsemea1.ta.philips.com:22/tfs/TPC_Region24/CDP2/_git/dsc-android-dataservices']]])
                    step([$class: 'StashNotifier'])
                }

                stage ('build') {
                    sh '''#!/bin/bash -l
                        chmod -R 755 .
                        cd ./Source/DemoApp
                        ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} clean assembleRelease
                    '''
                }

                stage ('test') {
                    sh '''#!/bin/bash -l
                        chmod -R 755 .
                        cd ./Source/DemoApp
                        ./gradlew -PenvCode=${JENKINS_ENV} lintRelease testReleaseUnitTest
                    '''
                }

                stage ('save dependencies list') {
                    sh '''#!/bin/bash -l
                        chmod -R 775 . 
                        cd ./Source/DemoApp
                        ./gradlew -PenvCode=${JENKINS_ENV} saveResDep saveAllResolvedDependencies saveAllResolvedDependenciesGradleFormat
                        cd ../Library 
                        ./gradlew -PenvCode=${JENKINS_ENV} saveResDep saveAllResolvedDependencies saveAllResolvedDependenciesGradleFormat
                    '''
                }
            } catch(err) {
                errors << "errors found: ${err}"
            } finally {
                if (errors.size() > 0) {
                    stage ('error reporting') {
                        currentBuild.result = 'FAILURE'
                        for (int i = 0; i < errors.size(); i++) {
                            echo errors[i]; 
                        }
                    }
                }

                stage ('reporting') {
                    androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '', shouldDetectModules: true, unHealthy: '', unstableTotalHigh: ''
                    junit allowEmptyResults: false, testResults: 'Source/Library/*/build/test-results/**/*.xml'
                    publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/dataServices/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'unit test release'])
                    archiveArtifacts '**/*dependencies*.lock'
                }

                stage('informing') {
                    step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: MailRecipient, sendToIndividuals: true])
                }

                stage('Cleaning workspace') {
                    step([$class: 'WsCleanup', deleteDirs: true, notFailBuild: true])
                }
            }
        }
    } // end timestamps
} // end node ('android')

node('master') {
    stage('Cleaning workspace') {
        def wrk = pwd() + "@script/"
        dir("${wrk}") {
            deleteDir()
        }
    }
}
