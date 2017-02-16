#!/usr/bin/env groovy																											

BranchName = env.BRANCH_NAME
JENKINS_ENV = env.JENKINS_ENV

properties([
    [$class: 'ParametersDefinitionProperty', parameterDefinitions: [[$class: 'StringParameterDefinition', defaultValue: '', description: 'triggerBy', name : 'triggerBy']]],
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '50']]
])


def MailRecipient = 'DL_CDP2_Callisto@philips.com,DL_App_chassis@philips.com'

node_ext = "androidppc"
if (env.triggerBy == "ppc") {
  node_ext = "build_p"
}


node ('android_pipeline &&' + node_ext) {
	timestamps {
		stage ('Checkout') {
             checkout([$class: 'GitSCM', branches: [[name: '*/'+BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'PruneStaleBranch'], [$class: 'LocalBranch']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '4edede71-63a0-455e-a9dd-d250f8955958', url: 'ssh://git@bitbucket.atlas.philips.com:7999/mail/app-infra_android.git']]])
			step([$class: 'StashNotifier'])
		}
		
        try {
			if (BranchName =~ /master|develop|release.*/) {
			     stage ('build') {
				    // sh 'chmod -R 775 . && cd ./Source/Library && ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} clean assembleDebug && ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} lint cC assembleRelease zipDocuments artifactoryPublish'
                    sh 'chmod -R 775 . && cd ./Source/Library && ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} assembleRelease zipDocuments artifactoryPublish'
			     }	
			}
            else
            {
                stage ('build') {
                    sh 'chmod -R 775 . && cd ./Source/Library && ./gradlew --refresh-dependencies clean assembleDebug assembleRelease'
                }
            }
			
            stage ('save dependencies list') {
                sh 'chmod -R 775 . && cd ./Source/Library && ./gradlew -PenvCode=${JENKINS_ENV} saveResDep'
            }
            
            archiveArtifacts '**/dependencies.lock'
            currentBuild.result = 'SUCCESS'
            
		} //end try
		
		catch(err) {
            currentBuild.result = 'FAILURE'
            error ("Someone just broke the build")
        }

        try {
            if (env.triggerBy != "ppc" && (BranchName =~ /master|develop|release.*/)) {
                stage ('callIntegrationPipeline') {
                    if (BranchName =~ "/") {
                        BranchName = BranchName.replaceAll('/','%2F')
                        echo "BranchName changed to ${BranchName}"
                    }
                    build job: "Platform-Infrastructure/ppc/ppc_android/${BranchName}", parameters: [[$class: 'StringParameterValue', name: 'componentName', value: 'ail'],[$class: 'StringParameterValue', name: 'libraryName', value: '']]
                    currentBuild.result = 'SUCCESS'
                }            
            }
        }

        catch(err) {
            currentBuild.result = 'UNSTABLE'
        }

        stage('informing') {
        	step([$class: 'StashNotifier'])
        	step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: MailRecipient, sendToIndividuals: true])
        }

	} // end timestamps
} // end node ('android')