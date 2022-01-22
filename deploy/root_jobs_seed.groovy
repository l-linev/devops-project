/*folder('devops_project') {
    displayName('Devops Project App')
    description('Devops Project App')
}*/

job('devops_project/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
    stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('l-linev/devops-project')
                credentials('GitHub')
            }
            branch('$GIT_REF')
        }
    }
    steps {
        dsl {
            external('deploy/jobs_seed.groovy')
            lookupStrategy('SEED_JOB')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('lib')
        }
    }
}

multibranchPipelineJob('devops_project/ci-cd') {
    displayName("CI-CD")
    branchSources {
        github {
            id('631742818912') // IMPORTANT: use a constant and unique identifier
            buildForkPRHead(false)
            buildForkPRMerge(true)
            buildOriginBranch(false)
            buildOriginBranchWithPR(false)
            buildOriginPRHead(false)
            buildOriginPRMerge(true)
            scanCredentialsId('GitHub')
            repoOwner('l-linev')
            repository('devops-project')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(20)
        }
    }
}
