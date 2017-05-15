#!/usr/bin/env groovy																											

BranchName = env.BRANCH_NAME
JENKINS_ENV = env.JENKINS_ENV

properties([
    [$class: 'ParametersDefinitionProperty', parameterDefinitions: [[$class: 'StringParameterDefinition', defaultValue: '', description: 'triggerBy', name : 'triggerBy']]],
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '50']]
])

def MailRecipient = 'DL_CDP2_Callisto@philips.com, DL_App_chassis@philips.com'

node ('android&&device') {
	timestamps {
		stage ('Checkout') {
			checkout([$class: 'GitSCM', branches: [[name: '*/'+BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CloneOption', noTags: false, reference: '', shallow: true, timeout: 30],[$class: 'WipeWorkspace'], [$class: 'PruneStaleBranch'], [$class: 'LocalBranch']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '4edede71-63a0-455e-a9dd-d250f8955958', url: 'ssh://tfsemea1.ta.philips.com:22/tfs/TPC_Region24/CDP2/_git/usr-android-user-registration']]])
			step([$class: 'StashNotifier'])
		}

		try {
    		if (BranchName =~ /master|develop|release.*/) {
    			stage ('build') {
                    sh '''#!/bin/bash -l
                        chmod -R 775 . 
                        cd ./Source/Library 
                        ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} clean assembleDebug lint
                        ./gradlew -PenvCode=${JENKINS_ENV} assembleRelease cC test zipDocuments artifactoryPublish
                    '''
                }
    		}	
    		else {
    			stage ('build') {
                    sh '''#!/bin/bash -l
                        chmod -R 775 . 
                        cd ./Source/Library 
                        ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} clean assembleDebug lint
                        ./gradlew -PenvCode=${JENKINS_ENV} assembleRelease cC test
                    '''
    			}
    		
            }

    		stage ('save dependencies list') {
                sh '''#!/bin/bash -l
            	   chmod -R 775 . 
                   cd ./Source/Library 
                   ./gradlew -PenvCode=${JENKINS_ENV} saveResDep
                '''
            }

           stage ('reporting') {
                androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '', shouldDetectModules: true, unHealthy: '', unstableTotalHigh: '0'
                junit allowEmptyResults: true, testResults: 'Source/Library/*/build/test-results/*/*.xml'
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/RegistrationApi/build/reports/tests/debug', reportFiles: 'index.html', reportName: 'unit test debug']) 
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/RegistrationApi/build/reports/tests/release', reportFiles: 'index.html', reportName: 'unit test release'])
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/RegistrationApi/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'connected tests RegistrationApi'])
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/coppa/build/reports/androidTests/connected', reportFiles: 'index.html', reportName: 'connected tests coppa'])
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/coppa/build/reports/Jump/connected', reportFiles: 'index.html', reportName: 'connected tests Jump'])
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/Library/coppa/build/reports/hsdp/connected', reportFiles: 'index.html', reportName: 'connected tests hsdp'])
                archiveArtifacts '**/dependencies.lock'
            }

        }

        catch(err) {
            currentBuild.result = 'FAILURE'
            error ("Someone just broke the build", err.toString())
        }

        if (env.triggerBy != "ppc" && (BranchName =~ /master|develop|release.*/)) {
        	stage ('callIntegrationPipeline') {
                if (BranchName =~ "/") {
                    BranchName = BranchName.replaceAll('/','%2F')
                    echo "BranchName changed to ${BranchName}"
                }
        		build job: "Platform-Infrastructure/ppc/ppc_android/${BranchName}", parameters: [[$class: 'StringParameterValue', name: 'componentName', value: 'usr'],[$class: 'StringParameterValue', name: 'libraryName', value: '']], wait: false 
        	}            
        }

    	step([$class: 'StashNotifier'])
    	step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: MailRecipient, sendToIndividuals: true])

	}
}
