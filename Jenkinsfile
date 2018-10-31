pipeline {
    agent {
        label env.NODELABEL_NAME
    }

    stages {

        stage('Test') {
            when {
                anyOf {
                    branch 'master'
                    changeRequest()
                }
            }
            steps {
                sh './gradlew --no-daemon clean test'
            }
        }

        stage('Upload to Bintray') {
            when {
                branch 'master'
            }
            steps {
                withCredentials(
                        [
                                string(credentialsId: 'BINTRAY_API_KEY', variable: 'BINTRAY_API_KEY'),
                                string(credentialsId: 'BINTRAY_USERNAME', variable: 'BINTRAY_USERNAME')
                        ]
                ) {
                    sh './gradlew --no-daemon bintrayUpload -PbintrayUser=$BINTRAY_USERNAME -PbintrayApiKey=$BINTRAY_API_KEY'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }

}