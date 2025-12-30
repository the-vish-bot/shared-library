def call() {
    pipeline {
        agent any

        stages {
            stage('Create Jobs') {
                steps {
                    script {
                        def clients = [
                            [name: 'acme',   environment: 'production'],
                            [name: 'globex', environment: 'production'],
                            [name: 'timex', environment: 'production'],
                            [name: 'flex', environment: 'production'],
                            [name: 'rolex', environment: 'production']
                            
                            
                            
                        ]

                        echo "Creating jobs for ${clients.size()} clients..."

                        clients.each { client ->
                            this.createJobForClient(client)
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
