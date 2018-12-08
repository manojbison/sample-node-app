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
                    habitat task: 'upload', authToken: env.HAB_AUTH_TOKEN, lastBuildFile: "C:\hab\studios\jenkins--workspace--testud\src\habitat\results\last_build.ps1", bldrUrl: "${env.HAB_BLDR_URL}"
                }
            }
        }
    }
}
