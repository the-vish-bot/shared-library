import groovy.json.JsonSlurper

pipeline {
    agent any

    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
        timeout(time: 2, unit: 'HOURS')
    }

    stages {
        stage('Create Jobs') {
            steps {
                script {
                    // Load JSON from shared library resource
                    def rawClients = libraryResource('clients.json')
                    def parsedClients = new JsonSlurper().parseText(rawClients)

                    // Convert LazyMap → HashMap to make it serializable
                    def clients = parsedClients.collect { client ->
                        client instanceof Map ? new HashMap(client) : client
                    }

                    echo "Creating jobs for ${clients.size()} clients..."

                    // Iterate over clients and create jobs using Job DSL
                    clients.each { client ->
                        def clientName = client instanceof Map ? client.name : client
                        echo "Creating job for ${clientName}..."

                        jobDsl(scriptText: """
                            job('${clientName}-job') {
                                description('Job created for ${clientName}')
                                scm {
                                    git('https://github.com/the-vish-bot/some-repo.git', 'main')
                                }
                                triggers {
                                    scm('H/5 * * * *')
                                }
                                steps {
                                    shell('echo Running job for ${clientName}')
                                }
                            }
                        """.stripIndent())
                    }
                }
            }
        }
    }
}
