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
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], 
                doGenerateSubmoduleConfigurations: false, 
                extensions: [], submoduleCfg: [], 
                userRemoteConfigs: [[credentialsId: 'efa9aff4-6688-440b-a0c8-e71bdf994dc0', 
                url: 'https://github.com/manojbison/sample-node-app.git']]])

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
                    habitat task: 'upload', authToken: env.HAB_AUTH_TOKEN, lastBuildFile: "${workspace}/habitat/results/last_build.ps1", bldrUrl: "${env.HAB_BLDR_URL}"
                }
            }
        }
stage('promote') {
            steps {
              
              script {
                env.HAB_PKG = sh ( 
                     script: "ls -t ${$workspace}/habitat/results | grep hart | head -n 1",
                     returnStdout: true
                     ).trim()
              }
             
              withCredentials([string(credentialsId: 'depot-token', variable: 'HAB_AUTH_TOKEN')]) {
                  habitat task: 'promote', channel: 'stable', authToken: "${env.HAB_AUTH_TOKEN}", artifact: "${env.HAB_PKG}", bldrUrl: "${env.HAB_BLDR_URL}"
              }
            }
        }
    }
}
