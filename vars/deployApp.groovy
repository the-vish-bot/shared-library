def call(Map config) {
    pipeline {
        agent any

        parameters {
            string(name: 'VERSION', defaultValue: 'latest', description: 'Version to deploy')
            booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Run tests?')
        }

        stages {
            stage('Hello') {
                steps {
                    echo "👋 Hello from ${config.clientName}"
                    echo "Environment: ${config.environment}"
                    echo "Version: ${params.VERSION}"
                }
            }

            stage('Build') {
                steps {
                    echo "🔨 Building for ${config.clientName}"
                    sh "echo Building version ${params.VERSION}"
                }
            }

            stage('Test') {
                when {
                    expression { params.RUN_TESTS }
                }
                steps {
                    echo "🧪 Running tests"
                    sh "echo Tests passed"
                }
            }

            stage('Deploy') {
                steps {
                    echo "🚀 Deploying to ${config.environment}"
                    sh """
                        echo ==========================================
                        echo DEPLOYED ${config.clientName}
                        echo ENV: ${config.environment}
                        echo VERSION: ${params.VERSION}
                        echo ==========================================
                    """
                }
            }
        }

        post {
            success {
                echo "✅ Deployment successful"
            }
            failure {
                echo "❌ Deployment failed"
            }
        }
    }
}
