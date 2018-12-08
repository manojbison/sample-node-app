pipeline {
    agent any

    environment {
        HAB_NOCOLORING = true
        HAB_BLDR_URL = 'https://bldr.habitat.sh/'
        HAB_ORIGIN = 'bison'
    }

    stages {
        stage('scm') {
            steps {
                git url: 'https://github.com/manojbison/sample-node-app.git', branch: 'master'
            }
        }
        stage('build') {
            steps {
                habitat task: 'build', directory: '.', origin: "${env.HAB_ORIGIN}"
            }
        }
        stage('upload') {
            steps {
                withCredentials([string(credentialsId: 'depot-token', variable: 'HAB_AUTH_TOKEN')]) {
                    habitat task: 'upload', authToken: env.HAB_AUTH_TOKEN, lastBuildFile: "${workspace}/results/last_build.env", bldrUrl: "${env.HAB_BLDR_URL}"
                }
            }
        }
    }
}
