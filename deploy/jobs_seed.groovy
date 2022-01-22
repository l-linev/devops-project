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

defaultPipelineJob("reload_jobs", "Reload all jobs", "Reload parameters for all jobs in this folder", "deploy/reload_jobs.groovy", "l-linev/devops-project")

/*folder('devops_project') {
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
                userName()
                buildParameters("VERSION")
                buildButton()
            }
        }
    }
}*/


defaultPipelineJob(
    "devops_project/build_job",
    "Build job",
    "Build new Devops Project docker image",
    "deploy/build_job.groovy",
    "l-linev/devops-project"
)
