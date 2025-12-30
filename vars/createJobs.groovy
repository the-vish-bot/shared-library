import groovy.json.JsonSlurper

def call() {
    pipeline {
        agent any

        stages {
            stage('Create Jobs') {
                steps {
                    script {
                        // Read clients.json dynamically
                        def jsonFile = "${WORKSPACE}/clients.json"
                        def clients = new JsonSlurper().parse(new File(jsonFile))

                        echo "Creating jobs for ${clients.size()} clients: ${clients.collect { it.name }}"

                        clients.each { client ->
                            createJobForClient(client)
                        }
                    }
                }
            }
        }
    }
}

def createJobForClient(client) {
    echo "Creating job for ${client.name}..."

    // Job DSL outside sandbox to avoid approval
    jobDsl scriptText: """
        pipelineJob('deploy-${client.name}') {
            description('Deploy pipeline for ${client.name}')
            definition {
                cps {
                    sandbox(false)
                    script(\"\"\"
                        @Library('my-shared-library') _
                        deployApp(
                            clientName: '${client.name}',
                            environment: '${client.environment}'
                        )
                    \"\"\")
                }
            }
        }
    """
}

