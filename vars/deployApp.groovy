pipeline {
    agent any
    
    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
        timeout(time: 1, unit: 'HOURS')
    }

    environment {
        CLIENT_NAME = "${env.CLIENT_NAME}"
        ENVIRONMENT = "${env.ENVIRONMENT}"
    }

    stages {
        stage('Validate') {
            steps {
                script {
                    if (params.VERSION.isEmpty()) {
                        error('VERSION parameter cannot be empty')
                    }
                    currentBuild.displayName = "#${BUILD_NUMBER}-${params.VERSION}"
                }
            }
        }

        stage('Build') {
            steps {
                echo "🔨 Building ${env.CLIENT_NAME}"
                sh "echo Building version ${params.VERSION}"
            }
        }

        stage('Test') {
            when {
                expression { params.RUN_TESTS }
            }
            steps {
                echo "🧪 Running tests for ${env.CLIENT_NAME}"
                sh "echo Tests passed"
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Deploying to ${env.ENVIRONMENT}"
                sh """
                    echo ==========================================
                    echo DEPLOYED ${env.CLIENT_NAME}
                    echo ENV: ${env.ENVIRONMENT}
                    echo VERSION: ${params.VERSION}
                    echo ==========================================
                """
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful for ${env.CLIENT_NAME}"
        }
        failure {
            echo "❌ Deployment failed for ${env.CLIENT_NAME}"
        }
    }
}
