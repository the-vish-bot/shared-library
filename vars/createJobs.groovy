import groovy.json.JsonSlurper

@NonCPS
def parseClients(String jsonText) {
    def slurper = new JsonSlurper()
    def parsed = slurper.parseText(jsonText)
    // Convert to ArrayList of plain HashMaps to ensure serializability
    return parsed.collect { client ->
        [name: client.name.toString(), environment: client.environment.toString()]
    }
}

def call() {
    // Load and parse clients
    def clientsJson = libraryResource('clients.json')
    def clients = parseClients(clientsJson)
    
    echo "Creating jobs for ${clients.size()} clients..."
    
    // Create jobs for each client
    for (int i = 0; i < clients.size(); i++) {
        def client = clients[i]
        def clientName = client.name
        def clientEnv = client.environment
        
        echo "Creating job for ${clientName}..."
        
        jobDsl scriptText: """
            pipelineJob('deploy-${clientName}') {
                description('Deploy pipeline for ${clientName}')
                definition {
                    cps {
                        sandbox(true)
                        script('''
                            @Library('my-shared-library') _
                            deployApp(
                                clientName: '${clientName}',
                                environment: '${clientEnv}'
                            )
                        ''')
                    }
                }
            }
        """
    }
    
    echo "✅ Successfully created ${clients.size()} jobs"
}
