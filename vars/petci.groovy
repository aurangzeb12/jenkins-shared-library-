def call() {
    pipeline {    
        agent { 
            docker { image 'maven:3.8.4-openjdk-17' }
        } 

        environment {
            DOCKER_HUB_REPO = "techiescamp/jenkins-java-app"
            IMAGE_TAG = "2.0.0"
        }

        stages {
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
        
            stage('Install Docker and Build Image') {
                steps {
                    sh '''
                        # Download and extract Docker binary
                        curl -fsSLO https://download.docker.com/linux/static/stable/x86_64/docker-27.3.1.tgz
                        tar -xvf docker-27.3.1.tgz
                        mv docker/docker /usr/local/bin/
                        
                        # Verify Docker installation
                        docker --version
                        docker info
                        
                        # Build Docker image
                        DOCKER_BUILDKIT=0 docker build -t my-first-maven-app:latest .
                    '''
                }
            }
        }
    }
}
