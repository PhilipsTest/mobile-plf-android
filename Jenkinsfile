#!/usr/bin/env groovy																											

BranchName = env.BRANCH_NAME
JENKINS_ENV = env.JENKINS_ENV

properties([
    [$class: 'ParametersDefinitionProperty', parameterDefinitions: [[$class: 'StringParameterDefinition', defaultValue: '', description: 'triggerBy', name : 'triggerBy']]],
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '10']]
])

def MailRecipient = 'DL_CDP2_Callisto@philips.com,DL_App_chassis@philips.com'
def errors = []

node ('android&&device') {
	timestamps {
		
        try {

            stage ('Checkout') {
               checkout([$class: 'GitSCM', branches: [[name: '*/'+BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'PruneStaleBranch'], [$class: 'LocalBranch']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://tfsemea1.ta.philips.com:22/tfs/TPC_Region24/CDP2/_git/ail-android-appinfra']]])
            }

			if (BranchName =~ /master|develop|release.*/) {
			     stage ('build') {
                    sh '''#!/bin/bash -l
                        chmod -R 775 .
                        cd ./Source/Library
                        ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} clean assembleDebug lint
                        ./gradlew -PenvCode=${JENKINS_ENV} assembleRelease cC zipDocuments artifactoryPublish
                    '''                    
			     }	
			}
            else
            {
                stage ('build') {
                    sh '''#!/bin/bash -l
                        chmod -R 775 .
                        cd ./Source/Library 
                        ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} clean assembleDebug lint  
                        ./gradlew -PenvCode=${JENKINS_ENV} assembleRelease cC
                    '''
                }
            }
			
            stage ('save dependencies list') {
                sh '''#!/bin/bash -l
                    cd ./Source/Library
                    ./gradlew -PenvCode=${JENKINS_ENV} saveResDep saveAllResolvedDependencies saveAllResolvedDependenciesGradleFormat
                '''
            }
                        
            if (env.triggerBy != "ppc" && (BranchName =~ /master|develop|release.*/)) {
                stage ('callIntegrationPipeline') {
                    if (BranchName =~ "/") {
                        BranchName = BranchName.replaceAll('/','%2F')
                        echo "BranchName changed to ${BranchName}"
                    }
                    build job: "Platform-Infrastructure/ppc/ppc_android/${BranchName}", parameters: [[$class: 'StringParameterValue', name: 'componentName', value: 'ail'],[$class: 'StringParameterValue', name: 'libraryName', value: '']], wait: false
                    // call dls pipeline as well (temporary)
                   // build job: "Platform-Infrastructure/ppc/ppc_android/${BranchName}_DLS", parameters: [[$class: 'StringParameterValue', name: 'componentName', value: 'ail'],[$class: 'StringParameterValue', name: 'libraryName', value: '']], wait: false

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
            stage ('reporting') {
                androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '', shouldDetectModules: true, unHealthy: '', unstableTotalHigh: '0'
                // junit allowEmptyResults: false, testResults: 'Source/Library/*/build/test-results/*/*.xml'
                junit allowEmptyResults: false, testResults: 'Source/Library/*/build/outputs/androidTest-results/*/*.xml'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/AppInfra/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'connected tests']) 
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/AppInfra/build/reports/coverage/debug', reportFiles: 'index.html', reportName: 'coverage tests']) 
                archiveArtifacts '**/*dependencies*.lock'
            }   
            stage('informing') {
            	step([$class: 'StashNotifier'])
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