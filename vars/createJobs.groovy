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
                        
                        // Convert to serializable plain Maps and Lists
                        def clients = rawClients.collect { client ->
                            [
                                name: client.name.toString(),
                                environment: client.environment.toString()
                            ]
                        }
                        
                        echo "Creating jobs for ${clients.size()} clients..."
                        
                        // Process each client
                        clients.each { client ->
                            createJobForClient(client.name, client.environment)
                        }
                    }
                }
            }
        }
    }
}

def createJobForClient(String clientName, String environment) {
    echo "Creating job for ${clientName}..."
    jobDsl scriptText: """
        pipelineJob('deploy-${clientName}') {
            description('Deploy pipeline for ${clientName}')
            definition {
                cps {
                    sandbox(true)
                    script(\"\"\"
                        @Library('my-shared-library') _
                        deployApp(
                            clientName: '${clientName}',
                            environment: '${environment}'
                        )
                    \"\"\")
                }
            }
        }
    """
}
