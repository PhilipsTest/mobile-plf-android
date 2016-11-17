/* following 2 lines are mandatory for the platform CI pipeline integration */
properties([[$class: 'ParametersDefinitionProperty', parameterDefinitions: [[$class: 'StringParameterDefinition', defaultValue: '', description: 'triggerBy', name: 'triggerBy']]]])

node('Android') {
    stage 'Checkout'
    checkout scm

    stage 'Build'
    sh 'cd ./Source/ShineLib && ./gradlew assembleDebug'

    stage 'Unit test'
    sh 'rm -rf ./Source/ShineLib/shinelib/build/test-results/debug ./Source/ShineLib/pluginreferenceboard/build/test-results/debug'
    sh 'cd ./Source/ShineLib && ./gradlew test || true'
    step([$class: 'JUnitResultArchiver', testResults: 'Source/ShineLib/*/build/test-results/*/*.xml'])

    stage 'Lint'
    sh 'cd ./Source/ShineLib && ./gradlew lintDebug || true'
    step([$class: 'LintPublisher', healthy: '0', unHealthy: '20', unstableTotalAll: '20'])

    stage 'Archive Apps'
    step([$class: 'ArtifactArchiver', artifacts: 'Source/ShineLib/bluelibtestapp/build/outputs/apk/*.apk', excludes: null, fingerprint: true, onlyIfSuccessful: true])
    step([$class: 'ArtifactArchiver', artifacts: 'Source/ShineLib/bluelibexampleapp/build/outputs/apk/*.apk', excludes: null, fingerprint: true, onlyIfSuccessful: true])

    stage 'Reporting'
    step([$class: 'JacocoPublisher', execPattern: '**/*.exec', classPattern: '**/classes', sourcePattern: '**/src/main/java', exclusionPattern: '**/R.class,**/R$*.class,**/BuildConfig.class,**/Manifest*.*,**/*Activity*.*,**/*Fragment*.*'])

    if (env.BRANCH_NAME == "develop" || env.BRANCH_NAME == "master") {
        stage 'Publish'
        sh 'cd ./Source/ShineLib && ./gradlew zipDocuments artifactoryPublish'
    }
}

/* next if-then + stage is mandatory for the platform CI pipeline integration */
if (env.triggerBy != "ppc" && (env.BRANCH_NAME == "develop" || env.BRANCH_NAME == "master")) {
    stage('callIntegrationPipeline') {
        build job: "Platform-Infrastructure/ppc/ppc_android/${env.BRANCH_NAME}", parameters: [[$class: 'StringParameterValue', name: 'componentName', value: 'bll'], [$class: 'StringParameterValue', name: 'libraryName', value: 'BlueLib']]
    }
}
