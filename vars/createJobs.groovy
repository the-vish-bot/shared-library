def call() {
    pipeline {
        agent any
        
        stages {
            stage('Create Jobs') {
                steps {
                    script {
                        // Add your clients here
                        def clients = [
                            [name: 'acme', environment: 'production'],
                            [name: 'globex', environment: 'production']
                        ]
                        
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
            description('Deploy to ${client.name}')
            
            definition {
                cps {
                    script('''
                        @Library('my-shared-library') _
                        deployApp(
                            clientName: '${client.name}',
                            environment: '${client.environment}'
                        )
                    ''')
                }
            }
        }
    """
}
