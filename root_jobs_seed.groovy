folder('devops') {
    displayName('DevOps')
    description('DevOps jobs')
}

job('devops/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
        stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/constructor_devops')
                credentials('constructorbot_github_token')
            }
            branch('$GIT_REF')
        }
    }
    steps {
        dsl {
            external('jenkins/jobs/jobs_seed.groovy')
            lookupStrategy('SEED_JOB')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('lib')
        }
    }
}

folder('autocomplete') {
    displayName('Autocomplete')
    description('Autocomplete related jobs')
}

job('autocomplete/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
        stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/autocomplete')
                credentials('constructorbot_github_token')
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

folder('pypi_repositories') {
    displayName('PyPi Repositories')
    description('PyPi Repositories')
}

folder('pypi_repositories/cnstrc_tokenizer') {
    displayName('cnstrc_tokenizer')
    description('cnstrc_tokenizer')
}

multibranchPipelineJob('pypi_repositories/cnstrc_tokenizer/ci-cd') {
    displayName("CI-CD")
    branchSources {
        github {
            id('47101153232') // IMPORTANT: use a constant and unique identifier
            buildForkPRHead(false)
            buildForkPRMerge(true)
            buildOriginBranch(false)
            buildOriginBranchWithPR(false)
            buildOriginPRHead(false)
            buildOriginPRMerge(true)
            scanCredentialsId('constructorbot_github_token')
            repoOwner('Constructor-io')
            repository('cnstrc_tokenizer')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(20)
        }
    }
}

folder('customer-docs') {
    displayName('Customer Docs')
    description('Customer Docs')
}

job('customer-docs/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
        stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/customer-docs-v2')
                credentials('constructorbot_github_token')
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

folder('dice') {
    displayName('Dice App')
    description('Dice App related jobs')
}

job('dice/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
    stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/dice-experiment-analysis-tool')
                credentials('constructorbot_github_token')
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

folder('cubejs') {
    displayName('Cubejs App')
    description('Cubejs App related jobs')
}

job('cubejs/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
    stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/analytics-service')
                credentials('constructorbot_github_token')
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

folder('clickhouse') {
    displayName('Clickhouse')
    description('Clickhouse jobs')
}

job('clickhouse/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
    stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/constructor_devops')
                credentials('constructorbot_github_token')
            }
            branch('$GIT_REF')
        }
    }
    steps {
        dsl {
            external('jenkins/jobs/clickhouse/jobs_seed.groovy')
            lookupStrategy('SEED_JOB')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('lib')
        }
    }
}

folder('data_pipeline_v2') {
    displayName('Data Pipeline v2')
    description('Data Pipeline related jobs')
}

job('data_pipeline_v2/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
    stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/data_pipeline')
                credentials('constructorbot_github_token')
            }
            branch('$GIT_REF')
        }
    }
    steps {
        dsl {
            external('jenkins_jobs/jobs_seed.groovy')
            lookupStrategy('SEED_JOB')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('lib')
        }
    }
}

folder('eng_org') {
    displayName('Engineering Organization')
    description('Engineering Organization WebApp')
}

job('eng_org/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
    stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/eng-org')
                credentials('constructorbot_github_token')
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

folder('quizzes') {
    displayName('Quizzes')
    description('Quiz Service related jobs')
}

job('quizzes/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
    stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/quiz-service')
                credentials('constructorbot_github_token')
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

folder('website_constructor') {
    displayName('Website Constructror.io')
    description('New Website Constructror.io jobs')
}

job('website_constructor/generate_jobs') {
    displayName("Generate Jobs")
    parameters {
    stringParam('GIT_REF', 'master', 'GIT reference, branch, tag or commit')
    }
    scm {
        git {
            remote {
                github('Constructor-io/website')
                credentials('constructorbot_github_token')
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
