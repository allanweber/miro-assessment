String committer, envType, version, image
String prd = 'prd'
pipeline {
    agent any

    stages {
        stage ('Evaluating Environment') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master') envType = prd
                    else  envType = 'dev'
                }
                echo "Building for ${envType} environment"
            }
        }
        stage('Checking') {
            steps {
                echo 'Checking Branch Build: ' + env.BRANCH_NAME
                checkout scm
                script {
                    committer = sh(returnStdout: true, script: 'git show -s --pretty=%an').trim()
                }
                echo 'committer -> ' + committer
            }
        }
        stage('Test') {
            steps {
                sh 'mvn clean verify'
            }
        }
        stage('Test Results') {
            steps {
                step([$class: 'JUnitResultArchiver', testResults: 'target/surefire-reports/TEST-*.xml'])
            }
        }
        stage('Sonar') {
            when {
                not {
                    branch 'master'
                }
            }
            steps {
                echo 'run sonarQube in future'
            }
                }
        stage('Fomat Image Name') {
            steps {
                script {
                    version = sh(returnStdout: true,
                    script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout')
                }
                echo 'project version: ' + version
                if (envType == prd) {
                    image = allanweber/miro-widgets:${version}
                } else {
                    image = allanweber/miro-widgets-${envType}:${version}
                }
                echo 'image name: ' + image
            }
        }
        stage('Build Image') {
            sh "docker build -t ${image} ."
        }
        }
    }
}
