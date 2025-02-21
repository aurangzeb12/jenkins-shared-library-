
def call() {
    pipeline {
    agent  { label "app-1"}

    stages {
        stage ('Hello') {
            steps {
                echo 'Hello World 22'
            }
        }

        stage('New Stage') {
            steps {
                echo "Hello Date"
                sh "date"
                sh "hostname"
            }
        }

        stage ('Shared Library work') {
            steps {
                echo 'This is coming Shared Library'
            }
        }
    }
}
}