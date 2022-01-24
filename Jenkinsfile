@NonCPS
def cancelPreviousBuilds() {
    def jobName = env.JOB_NAME
    def buildNumber = env.BUILD_NUMBER.toInteger()
    /* Get job name */
    def currentJob = Jenkins.instance.getItemByFullName(jobName)

    /* Iterating over the builds for specific job */
    for (def build : currentJob.builds) {
        def exec = build.getExecutor()
        /* If there is a build that is currently running and it's not current build */
        if (build.isBuilding() && build.number.toInteger() != buildNumber && exec != null) {
            /* Then stop it */
            exec.interrupt(
                    Result.ABORTED,
                    new CauseOfInterruption.UserInterruption("Aborted by #${currentBuild.number}")
                )
            println("Aborted previously running build #${build.number}")
        }
    }
}
pipeline {
    agent any
    options {
        timeout(time: 30, unit: 'MINUTES')
        parallelsAlwaysFailFast()
        timestamps()
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
                    echo "GIT_COMMIT: ${env.GIT_COMMIT}"
                    env.GIT_COMMIT_NEW = sh returnStdout: true, script: "git log -n 1 --pretty=format:'%H' --invert-grep --author=Jenkins"
                    echo "GIT_COMMIT_NEW: ${env.GIT_COMMIT_NEW}"
                    echo "GIT_BRANCH: ${env.GIT_BRANCH}"
                    echo "CHANGE_BRANCH: ${env.CHANGE_BRANCH}"
                    echo "CHANGE_ID: ${env.CHANGE_ID}"
                    echo "CHANGE_TARGET: ${env.CHANGE_TARGET}"
                    if (env.CHANGE_BRANCH) {
                        //CI-CD is triggered by a push to a PR hence the pr_ prefix
                        cancelPreviousBuilds()
                        env.commit_to_test = env.CHANGE_BRANCH
                        env.VERSION = "pr-${env.CHANGE_ID}"
                        }
                    } else {
                        //CI-CD is triggered by a push to main branch. 
                        env.commit_to_test = env.GIT_COMMIT
                        env.VERSION = "br-${env.BUILD_ID}"
                    }
                    echo "New image version tag is ${env.VERSION}"
                    echo "Testing ${env.commit_to_test} merged with origin/main"
                }
            }
        }
        stage('Build, test and push') {
            steps {
                script {
                    def buildjobResult = build(job: 'devops_project/build_job', parameters: [
                        string(name: 'GIT_REF', value: env.commit_to_test),
                        string(name: 'VERSION', value: env.VERSION),
                        booleanParam(name: 'PUSH_DOCKER_IMAGES', value: true),
                        booleanParam(name: 'SKIP_TESTS', value: false),
                        booleanParam(name: 'TEST_MERGE_COMMIT', value: true)
                        ], wait: true, propagate: true)
                    echo "Devops Project app build, test and push docker image job id ${buildjobResult.number} finished with ${buildjobResult.result}."
                    env.buildId = buildjobResult.number
                }
            }
        }
        stage('Rollout Devops Project App') {
            steps {
                script {
                    def rolloutjobResult = build(job: 'devops_project/rollout_stack', parameters: [
                        string(name: 'GIT_REF', value: "${env.commit_to_test}"),
                        string(name: 'VERSION', value: env.VERSION)
                        ], wait: true, propagate: true)
                    echo "Devops Project App rollout_stack job id ${rolloutjobResult.number} deploying to ECS finished with ${rolloutjobResult.result}."
                }
            }
        }
    }
    post {
        cleanup {
            cleanWs()
        }
    }
}
