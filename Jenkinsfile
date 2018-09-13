pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh './gradlew build'
      }
    }
    stage('Test') {
      steps {
        sh './gradlew clean test'
      }
    }
  }
  environment {
    BINTRAY_USER = 'markokonsafob'
    BINTRAY_API_KEY = 'a99750233b1dfe5e7444716626948411da7868cb'
  }
}