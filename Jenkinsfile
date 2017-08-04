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
		try {
            stage ('Checkout') {
                checkout([$class: 'GitSCM', branches: [[name: '*/'+BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'PruneStaleBranch'], [$class: 'LocalBranch']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://tfsemea1.ta.philips.com:22/tfs/TPC_Region24/CDP2/_git/dsc-android-dataservices']]])
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
                    ./gradlew -PenvCode=${JENKINS_ENV} lintRelease testRelease 
                '''
            }

            if (BranchName =~ /master|develop|release.*/) {
                stage ('publish') {
                    sh '''#!/bin/bash -l
                        chmod -R 755 . 
                        cd ./Source/DemoApp 
                        ./gradlew -PenvCode=${JENKINS_ENV} zipDocuments artifactoryPublish
                    '''
                }
            } 

			stage ('save dependencies list') {
                sh '''#!/bin/bash -l
                	chmod -R 775 . 
                    cd ./Source/DemoApp
                    ./gradlew -PenvCode=${JENKINS_ENV} saveResDep
                	cd ../Library 
                    ./gradlew -PenvCode=${JENKINS_ENV} saveResDep
                '''
            }

           stage ('reporting') {
                androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '', shouldDetectModules: true, unHealthy: '', unstableTotalHigh: '0'
                junit allowEmptyResults: true, testResults: "Source/Library/*/build/test-results/*/*.xml"
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/dataServices/build/reports/tests/release', reportFiles: 'index.html', reportName: 'unit test release']) 
                archiveArtifacts '**/dependencies.lock'
            }

            if (env.triggerBy != "ppc" && (BranchName =~ /master|develop|release.*/)) {
                stage ('callIntegrationPipeline') {
                    if (BranchName =~ "/") {
                        BranchName = BranchName.replaceAll('/','%2F')
                        echo "BranchName changed to ${BranchName}"
                    }
                    build job: "Platform-Infrastructure/ppc/ppc_android/${BranchName}", parameters: [[$class: 'StringParameterValue', name: 'componentName', value: 'dsc'],[$class: 'StringParameterValue', name: 'libraryName', value: '']], wait: false
                }            
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
            stage('informing') {
            	step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: MailRecipient, sendToIndividuals: true])
            }
            stage('Cleaning workspace') {
                step([$class: 'WsCleanup', deleteDirs: true, notFailBuild: true])
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
