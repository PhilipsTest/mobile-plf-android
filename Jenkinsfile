node('Android') {
    stage 'Checkout'
    checkout scm

    stage 'Build'
    sh 'cd ./Source/cloudcontroller-api && ./gradlew assembleDebug'

    stage 'Lint'
    sh 'cd ./Source/cloudcontroller-api && ./gradlew lintDebug || true'
    step([$class: 'LintPublisher', healthy: '0', unHealthy: '20', unstableTotalAll: '20'])

    if(env.BRANCH_NAME == "develop" || env.BRANCH_NAME == "master"){
        stage 'Publish'
        sh 'cd ./Source/cloudcontroller-api && ./gradlew zipDocuments artifactoryPublish'
    }
}
