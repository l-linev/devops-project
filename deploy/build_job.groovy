@Library('utils') _

def do_checkout = {
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: params.GIT_REF]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout'], [$class: 'CleanCheckout'], [$class: 'PreBuildMerge', options: [mergeRemote: 'origin', mergeTarget: 'main']]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'GitHub', url: env.GIT_URL]]]
}

pipeline {
    agent { label 'master' }
    parameters {
        booleanParam(name: 'PUSH_DOCKER_IMAGES', defaultValue: false, description: 'Push docker images')
        booleanParam(name: 'TEST_MERGE_COMMIT', defaultValue: false, description: 'Tries merging with origin/master and runs test for that commit.')
        string(name: 'GIT_REF', defaultValue: 'main', description: 'Project repository GIT REF')
    }
    options {
        timeout(time: 30, unit: 'MINUTES')
        parallelsAlwaysFailFast()
        timestamps()
        ansiColor('xterm')
        buildDiscarder(
            logRotator(
                daysToKeepStr: '15',
                numToKeepStr:'50',
                artifactDaysToKeepStr: '15',
                artifactNumToKeepStr: '50'
            )
        )
    }
    stages {
        stage('Init') {
            steps {
                script {
                    utils.abort_on_refresh_parameters()
                    utils.exit_on_null_parameters()
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
            }
        }
        stage('Merge master') {
            when {
                expression { params.TEST_MERGE_COMMIT == true }
            }
            steps {
                script {
                    do_checkout()
                }
            }
        }
        stage('Build Devops Project image') {
            steps {
                script {
                    docker.withRegistry('https://914194858346.dkr.ecr.us-east-1.amazonaws.com') {
                        env.devops_project_docker_tag = "dev-${env.BUILD_ID}"
                        devops_project_image = docker.build("914194858346.dkr.ecr.us-east-1.amazonaws.com/devops_project:${env.devops_project_docker_tag}", ".")
                    }
                }
            }
        }
    }
    post {
        success {
            script {
                if (params.PUSH_DOCKER_IMAGES == true) {
                    docker.withRegistry('https://914194858346.dkr.ecr.us-east-1.amazonaws.com') {
                        devops_project_image.push()
                        echo "Pushed image with new docker tag '${env.devops_project_docker_tag}' for Devops Project image"
                    }
                }
            }
        }
        cleanup {
            cleanWs()
        }
    }
}