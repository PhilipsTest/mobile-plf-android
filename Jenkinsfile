/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

/* following line is mandatory for the platform CI pipeline integration */
properties([[$class: 'ParametersDefinitionProperty', parameterDefinitions: [[$class: 'StringParameterDefinition', defaultValue: '', description: 'triggerBy', name: 'triggerBy']]]])

node('Android') {
    stage('Checkout') {
        sh 'rm -rf *'
        checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'android-commlib-all'], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false], [$class: 'WipeWorkspace'], [$class: 'PruneStaleBranch'], [$class: 'LocalBranch']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/com/android-commlib-all.git']]])
    }

    Slack = load "android-commlib-all/Source/common/jenkins/Slack.groovy"
    Pipeline = load "android-commlib-all/Source/common/jenkins/Pipeline.groovy"
    def gradle = 'cd android-commlib-all/Source/commlib-all-parent && ./gradlew -PenvCode=${JENKINS_ENV}'

    stage('Build javadoc') {
        sh "$gradle --refresh-dependencies generateJavadocPublicApi"
    }

    Slack.notify('#conartists') {
        if (env.BRANCH_NAME == "develop" || env.BRANCH_NAME =~ "release" || env.BRANCH_NAME == "master") {
            stage('Build against binaries') {
                sh "$gradle assemble"
            }
        } else {
            stage('Checkout local BlueLib') {
                try {
                    checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'android-shinelib'], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/ehshn/android-shinelib.git']]])
                } catch (Exception e) {
                    checkout([$class: 'GitSCM', branches: [[name: 'develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'android-shinelib'], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/ehshn/android-shinelib.git']]])
                }
            }

            stage('Checkout local CommLib') {
                try {
                    checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'dicomm-android'], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/com/dicomm-android.git']]])
                } catch (Exception e) {
                    checkout([$class: 'GitSCM', branches: [[name: 'develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'dicomm-android'], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/com/dicomm-android.git']]])
                }
            }

            stage('Build against local libs') {
                sh "$gradle assemble"
            }
        }

        stage('Tests') {
            sh 'find . -path "**build/test-results" -exec rm -r "{}" \\;'
            sh "$gradle commlib-all:testDebug commlib-all:lintDebug || true"
            sh "$gradle commlib-all:pitestDebug"

            step([$class: 'JUnitResultArchiver', testResults: '**/testDebugUnitTest/*.xml'])
            step([$class: 'LintPublisher', healthy: '0', unHealthy: '20', unstableTotalAll: '20'])
            step([$class: 'JacocoPublisher', execPattern: '**/*.exec', classPattern: '**/classes', sourcePattern: '**/src/main/java', exclusionPattern: '**/R.class,**/R$*.class,**/BuildConfig.class,**/Manifest*.*,**/*Activity*.*,**/*Fragment*.*'])
            publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'android-commlib-all/Source/commlib-all-parent/build/report/commlib-all/pitest/debug/', reportFiles: 'index.html', reportName: 'Pitest'])
            publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'android-commlib-all/Documents/External/commlib-all-api', reportFiles: 'index.html', reportName: 'Commlib-ble API'])

            if (fileExists('android-commlib-all/Source/commlib-all-parent/build/cucumber-reports/report.json')) {
                step([$class: 'CucumberReportPublisher', jsonReportDirectory: 'android-commlib-all/Source/commlib-all-parent/build/cucumber-reports', fileIncludePattern: '*.json'])
            } else {
                echo 'No Cucumber result found, nothing to publish'
            }
        }

        stage('Archive artifacts') {
            archiveArtifacts artifacts: '**/build/outputs/apk/*.apk', fingerprint: true, onlyIfSuccessful: true
        }

        if (env.BRANCH_NAME == "develop" || env.BRANCH_NAME =~ "release" || env.BRANCH_NAME == "master") {
            stage('Publish') {
                sh 'rm -rf ./android_shinelib ./dicomm_android'
                sh "$gradle zipDoc artifactoryPublish"
            }
        }

        stage('Save Dependencies') {
            sh "$gradle saveResDep"
            archiveArtifacts '**/dependencies.lock'
        }
    }
    Pipeline.trigger(env.triggerBy, env.BRANCH_NAME, "CommLibBle", "cba")
    Pipeline.trigger(env.triggerBy, env.BRANCH_NAME + "_DLS", "CommLibBle", "cba")
}
