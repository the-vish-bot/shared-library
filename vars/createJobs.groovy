import groovy.json.JsonSlurper

def call() {
    pipeline {
        agent any

        stages {
            stage('Create Jobs') {
                steps {
                    script {
                        // Load clients.json from library resources
                        def clientsJson = libraryResource('clients.json')
                        def rawClients = new JsonSlurper().parseText(clientsJson)

                        // Convert each client to a plain HashMap
                        def clients = rawClients.collect { client ->
                            return [name: client.name, environment: client.environment]
                        }

                        echo "Creating jobs for ${clients.size()} clients..."

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

    jobDsl scriptText: """
        pipelineJob('deploy-${client.name}') {
            description('Deploy pipeline for ${client.name}')

            definition {
                cps {
                    sandbox(true)
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
