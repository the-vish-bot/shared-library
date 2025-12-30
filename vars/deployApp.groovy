def call(Map config) {
    pipeline {
        agent any
        
        stages {
            stage('Hello') {
                steps {
                    echo "👋 Hello from ${config.clientName}!"
                }
            }
            
            stage('Build') {
                steps {
                    echo "🔨 Building for ${config.clientName}..."
                    sh 'echo "Building application..."'
                }
            }
            
            stage('Deploy') {
                steps {
                    echo "🚀 Deploying to ${config.environment}..."
                    sh """
                        echo "====================================="
                        echo "   Deployed to ${config.clientName}!"
                        echo "   Environment: ${config.environment}"
                        echo "====================================="
                    """
                }
            }
        }
        
        post {
            success {
                echo "✅ Success!"
            }
            failure {
                echo "❌ Failed!"
            }
        }
    }
}
