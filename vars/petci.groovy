def call() {
    pipeline {    
        agent any 

        environment {
            DOCKER_HUB_REPO = "techiescamp/jenkins-java-app"
            IMAGE_TAG = "2.0.0"
        }

        stages {
            stage('Install Docker') {
                steps {
                    sh '''
                        # Update package list and install prerequisites
                        sudo apt-get update
                        sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release

                        # Add Docker's official GPG key
                        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
                        
                        # Set up the stable repository
                        echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
                        
                        # Install Docker Engine
                        sudo apt-get update
                        sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
                        
                        # Verify Docker installation
                        docker --version
                        
                        # Start Docker daemon in the background
                        sudo nohup dockerd > /var/log/dockerd.log 2>&1 &
                        
                        # Wait for Docker daemon to initialize
                        sleep 10
                        
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
                        docker build -t my-first-maven-app:latest .
                        docker tag my-first-maven-app:latest $DOCKER_HUB_REPO:$IMAGE_TAG
                        docker push $DOCKER_HUB_REPO:$IMAGE_TAG
                    '''
                }
            }
        }
    }
}
