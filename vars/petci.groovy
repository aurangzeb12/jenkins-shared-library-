def call() {
    pipeline {    
        agent { 
            label 'docker' // Ensure the agent has Docker or use a Docker-in-Docker setup
        } 

        environment {
            DOCKER_HUB_REPO = "techiescamp/jenkins-java-app"
            IMAGE_TAG = "2.0.0"
        }

        stages {
            stage('Install Docker') {
                steps {
                    sh '''
                        # Install Docker
                        curl -fsSL https://get.docker.com -o get-docker.sh
                        sh get-docker.sh
                        
                        # Move Docker binary to a standard location
                        mv /usr/bin/docker /usr/local/bin/docker
                        
                        # Check Docker version to verify installation
                        docker --version
                        
                        # Start Docker daemon
                        dockerd > /var/log/dockerd.log 2>&1 &
                        
                        # Verify Docker is running
                        docker info
                    '''
                }
            }
            
            stage('Checkout') {
                steps {
                    checkout scmGit(
                        branches: [[name: 'main']],
                        extensions: [],
                        userRemoteConfigs: [[url: 'https://github.com/aurangzeb12/kube-petclinc-app.git']]
                    )
                }
            }

            stage('Build with Maven') {
                steps {
                    sh 'mvn -B -Dmaven.repo.local=/root/.m2/repository clean install -DskipTests'
                }
            }

            stage('Build and Push Docker Image') {
                steps {
                    sh '''
                        DOCKER_BUILDKIT=0 docker build -t my-first-maven-app:latest .
                    '''
                }
            }
        }
    }
}
