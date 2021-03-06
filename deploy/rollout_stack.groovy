pipeline {
    parameters {
        string(name: 'VERSION', description: 'Devops Project version to rollout. Use the explicit version tag of the docker image here')
        string(name: 'GIT_REF', defaultValue: 'main', description: 'Project repository GIT REF')
        choice(name: 'REGION', choices: ['us-east-1'], description: 'Target region.')
    }
    agent any
    options {
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        buildDiscarder(
            logRotator(
                daysToKeepStr: '30',
                numToKeepStr:'30',
                artifactDaysToKeepStr: '30',
                artifactNumToKeepStr: '30'
            )
        )
    }
    stages {
        stage('Init') {
            steps {
                script {
                    sh(script: '''
                      set +x
                      aws ecr get-login-password \
                          --region us-east-1 \
                      | docker login \
                          --username AWS \
                          --password-stdin 914194858346.dkr.ecr.us-east-1.amazonaws.com
                      set -x
                    ''')
                }
                git branch: params.GIT_REF, credentialsId: 'GitHubToken', url: 'https://github.com/l-linev/devops-project.git'
            }
        }
        stage('Deploy Devops Project') {
            steps {
                echo "Started Devops Project stack deploy for version: `${params.VERSION}`, region: `${params.REGION}`."
                sh '''
                    #!/bin/bash -xe
                    cd deploy
                    chmod 755 deploy.sh
                    ./deploy.sh "${VERSION}"
                '''
            }
        }
    }
    post {
        success {
            echo "Devops Project deploy finished for version: `${params.VERSION}`, region: `${params.REGION}`"
        }
        failure {
            echo  "Devops Project deploy failed for version: `${params.VERSION}`, region: `${params.REGION}`(<${env.BUILD_URL}|Open>)."
        }
    }
}
