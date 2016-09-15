#!/usr/bin/env groovy

if (!env.CHANGE_ID) {
    /* Only keep the 5 most recent builds. */
    properties([[$class: 'BuildDiscarderProperty',
                    strategy: [$class: 'LogRotator', numToKeepStr: '5']],
                    pipelineTriggers([cron('H/30 * * * *')]),
                    ])
}

node('Android && 23.0.3') {
    timestamps{
        stage 'Checkout'
        checkout scm

        step([$class: 'StashNotifier'])
        try {
            //Build stuff starts
            stage 'Build'
            sh 'cd ./Source/AppFramework && ./gradlew assembleDebug'

            stage 'Release'
            sh 'cd ./Source/AppFramework && ./gradlew zipDoc appFramework:aP'

            stage 'Notify Bitbucket'
            sh 'echo \"Check the build status in bitbucket!\"'
            //Build stuff ends

            currentBuild.result = 'SUCCESS'
        } catch(err) {
            currentBuild.result = 'FAILED'
        }
        step([$class: 'StashNotifier'])
        step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'benit.dhotekar@philips.com', sendToIndividuals: true])
   }
}