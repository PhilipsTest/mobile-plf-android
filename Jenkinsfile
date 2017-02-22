node('Android') {
    stage('Checkout') {
        sh 'rm -rf *'
        checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'android-commlib-all'], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false], [$class: 'WipeWorkspace'], [$class: 'PruneStaleBranch'], [$class: 'LocalBranch']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/com/android-commlib-all.git']]])
    }

    Slack = load "android-commlib-all/Source/common/jenkins/Slack.groovy"
    Pipeline = load "android-commlib-all/Source/common/jenkins/Pipeline.groovy"

    Slack.notify('#conartists') {
        if (env.BRANCH_NAME == "develop" || env.BRANCH_NAME =~ "release" || env.BRANCH_NAME == "master") {
            stage('Build against binaries') {
                sh 'cd android-commlib-all/Source/commlib-all-parent && ./gradlew --refresh-dependencies -PenvCode=${JENKINS_ENV} assemble'
            }
        } else {
            stage('Checkout local BlueLib') {
                try {
                    checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'android-shinelib']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/ehshn/android-shinelib.git']]])
                } catch (Exception e) {
                    checkout([$class: 'GitSCM', branches: [[name: 'develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'android-shinelib']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/ehshn/android-shinelib.git']]])
                }
            }

            stage('Checkout local CommLib') {
                try {
                    checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'dicomm-android']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/com/dicomm-android.git']]])
                } catch (Exception e) {
                    checkout([$class: 'GitSCM', branches: [[name: 'develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'dicomm-android']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'd866c69b-16f0-4fce-823a-2a42bbf90a3d', url: 'ssh://git@bitbucket.atlas.philips.com:7999/com/dicomm-android.git']]])
                }
            }

            stage('Build against local libs') {
                sh 'cd android-commlib-all/Source/commlib-all-parent && ./gradlew -PenvCode=${JENKINS_ENV} assemble'
            }
        }

        stage('Tests') {
            sh 'rm -rf android-shinelib/Source/ShineLib/shinelib/build/test-results'
            sh 'rm -rf dicomm-android/Source/DICommClient/dicommClientLib/build/test-results'
            sh 'rm -rf android-commlib-all/Source/commlib-all-parent/commlib-all/build/test-results'
            sh 'cd android-commlib-all/Source/commlib-all-parent && ./gradlew -PenvCode=${JENKINS_ENV} commlib-all:testDebug || true'

            step([$class: 'JUnitResultArchiver', testResults: '**/build/test-results/*/*.xml'])

            if (fileExists('android-commlib-all/Source/commlib-all-parent/build/cucumber-reports/report.json')) {
                step([$class: 'CucumberReportPublisher', jsonReportDirectory: 'android-commlib-all/Source/commlib-all-parent/build/cucumber-reports', fileIncludePattern: '*.json'])
            } else {
                echo 'No Cucumber result found, nothing to publish'
            }
        }

        stage('Archive App') {
            step([$class: 'ArtifactArchiver', artifacts: 'android-commlib-all/Source/commlib-all-parent/commlib-all-example/build/outputs/apk/*.apk', excludes: null, fingerprint: true, onlyIfSuccessful: true])
        }

        if (env.BRANCH_NAME == "develop" || env.BRANCH_NAME =~ "release" || env.BRANCH_NAME == "master") {
            stage('Publish') {
                sh 'rm -rf ./android_shinelib ./dicomm_android'
                sh 'cd android-commlib-all/Source/commlib-all-parent && ./gradlew -PenvCode=${JENKINS_ENV} zipDoc artifactoryPublish'
            }
        }

        stage('Save Dependencies') {
            sh 'cd android-commlib-all/Source/commlib-all-parent && ./gradlew -PenvCode=${JENKINS_ENV} saveResDep'
            archiveArtifacts '**/dependencies.lock'
        }

        Pipeline.trigger(env.triggerBy, env.BRANCH_NAME, "CommLibBle", "cml")
    }
}
