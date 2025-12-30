def call(String configFile) {
    pipeline {
        agent any
        
        stages {
            stage('Read Config') {
                steps {
                    script {
                        echo "📋 Reading ${configFile}..."
                        def config = readJSON file: configFile
                        
                        echo "Found ${config.clients.size()} clients"
                        
                        // Create a job for each client
                        config.clients.each { client ->
                            createJobForClient(client)
                        }
                    }
                }
            }
        }
    }
}

def createJobForClient(client) {
    echo "🔨 Creating job for ${client.name}..."
    
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
    
    echo "✅ Created job: deploy-${client.name}"
}

