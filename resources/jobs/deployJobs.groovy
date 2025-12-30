def createDeployJob(String jobName, String clientName, String environment) {
    pipelineJob(jobName) {
        description("Deploy pipeline for ${clientName}")
        
        parameters {
            stringParam('VERSION', 'latest', 'Version to deploy')
            booleanParam('RUN_TESTS', true, 'Run tests?')
        }
        
        environmentVariables {
            env('CLIENT_NAME', clientName)
            env('ENVIRONMENT', environment)
        }
        
        definition {
            cps {
                script(readFileFromWorkspace('vars/deployApp.groovy'))
                sandbox()
            }
        }
    }
}

// Create jobs for each client
clients.each { client ->
    String jobName = "deploy-${client.name}"
    createDeployJob(jobName, client.name, client.environment)
}
