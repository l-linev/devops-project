def defaultPipelineJob(jobName, displayJobName, jobDescription, jobScriptPath, gitRepo) {
    pipelineJob(jobName) {
        displayName(displayJobName)
        description(jobDescription)
        parameters {
            stringParam('GIT_REF', 'main', 'GIT reference, branch, tag or commit for this job script')
            booleanParam('ONLY_REFRESH_PARAMETERS', false, "Run job just to refresh parameters.")
        }
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            github(gitRepo)
                            credentials('GitHub')
                        }
                        branch('$GIT_REF')
                        extensions {
                            wipeOutWorkspace()
                        }
                    }
                }
                scriptPath(jobScriptPath)
            }
        }
    }
}

folder('devops_project') {
    displayName('Devops Project')
    views {
        listView('Devops Project') {
            description("""Use <b>Build job</b>.""")
            filterBuildQueue()
            filterExecutors()
            jobs {
                names('build_job')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
    }
}

defaultPipelineJob(
    "devops_project/build_job",
    "Build job",
    "Build new Devops Project docker image",
    "deploy/build_job.groovy",
    "l-linev/devops-project"
)

defaultPipelineJob(
    "devops_project/rollout_stack",
    "Rollout App with Cloudformation",
    "Rollout Devops Project to AWS",
    "deploy/rollout_stack.groovy",
    "l-linev/devops-project"
)
