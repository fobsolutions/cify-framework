node {

    def changeRequest = env.CHANGE_ID
    def branchName = env.BRANCH_NAME
    def tagName = env.TAG_NAME

    // Clone repository
    stage('Clone repository') {
        checkout scm
    }

    stage('Read configuration') {

        if (branchName) {
            println("JENKINSFILE: Found branch with name: " + branchName)
        }

        if (changeRequest) {
            println("JENKINSFILE: Found pull request with data: " + changeRequest)
        }

        if (tagName) {
            println("JENKINSFILE: Found tag with name: " + tagName)
        }
    }

    if (changeRequest || branchName == "master" || tagName) {
        stage('Test') {
            sh './gradlew --no-daemon clean test'
        }
    }

    if (branchName == "master") {
        stage('Upload to Bintray') {
            withCredentials(
                    [
                            string(credentialsId: 'BINTRAY_API_KEY', variable: 'BINTRAY_API_KEY'),
                            string(credentialsId: 'BINTRAY_USERNAME', variable: 'BINTRAY_USERNAME')
                    ]
            )
                    {
                        sh './gradlew --no-daemon bintrayUpload -PbintrayUser=$BINTRAY_USERNAME -PbintrayApiKey=$BINTRAY_API_KEY'
                    }
        }
    }

    stage("Clean up") {
        println('Cleaning up after execution...')
        cleanWs()
    }
}