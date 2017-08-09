#!/usr/bin/env groovy																											

BranchName = env.BRANCH_NAME
JENKINS_ENV = env.JENKINS_ENV

properties([
    [$class: 'ParametersDefinitionProperty', parameterDefinitions: [[$class: 'StringParameterDefinition', defaultValue: '', description: 'triggerBy', name : 'triggerBy']]],
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '10']]
])

def MailRecipient = 'DL_CDP2_Callisto@philips.com'
def errors = []

node ('android&&docker') {
	timestamps {
    	try {
            stage ('Checkout') {
                checkout([$class: 'GitSCM', branches: [[name: '*/'+BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'PruneStaleBranch'], [$class: 'LocalBranch']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://tfsemea1.ta.philips.com:22/tfs/TPC_Region24/CDP2/_git/dpr-android-devicepairing']]])
                step([$class: 'StashNotifier'])
            }

    		if (BranchName =~ /master|develop|release.*/) {
    			stage ('build') {
                    sh '''#!/bin/bash -l
                        chmod -R 775 .
                        cd ./Source/DemoApp
                        ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} clean assembleDebug lint 
                        ./gradlew -PenvCode=${JENKINS_ENV} assembleRelease test artifactoryPublish
                    '''
			    }
			} else {
                stage ('build') {
                	sh '''#!/bin/bash -l
    				    chmod -R 775 .
    				    cd ./Source/DemoApp
                        ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} clean assembleDebug lint 
                        ./gradlew -PenvCode=${JENKINS_ENV} assembleRelease test
    				'''
    			}
			}

			stage ('save dependencies list') {
            	sh '''#!/bin/bash -l
            	    cd ./Source/DemoApp
            	    ./gradlew -PenvCode=${JENKINS_ENV} saveResDep saveAllResolvedDependencies saveAllResolvedDependenciesGradleFormat                   
            	'''
            }

            if (env.triggerBy != "ppc" && (BranchName =~ /master|develop|release.*/)) {
                stage ('callIntegrationPipeline') {
                    if (BranchName =~ "/") {
                        BranchName = BranchName.replaceAll('/','%2F')
                        echo "BranchName changed to ${BranchName}"
                    }
                    build job: "Platform-Infrastructure/ppc/ppc_android/${BranchName}", parameters: [[$class: 'StringParameterValue', name: 'componentName', value: 'dpr'],[$class: 'StringParameterValue', name: 'libraryName', value: '']], wait: false
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
                androidLint canComputeNew: true, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '', shouldDetectModules: true, unHealthy: '', unstableTotalHigh: '0'
                junit allowEmptyResults: true, testResults: 'Source/DemoApp/*/build/test-results/*/*.xml'
                junit allowEmptyResults: true, testResults: 'Source/DemoUApp/*/build/test-results/*/*.xml'
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/DemoApp/app/build/reports/tests/testDebugUnitTest', reportFiles: 'index.html', reportName: 'unit test debug'])
                publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'Source/DemoApp/app/build/reports/tests/testReleaseUnitTest', reportFiles: 'index.html', reportName: 'unit test release'])
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
