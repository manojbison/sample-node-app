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

    }
}
