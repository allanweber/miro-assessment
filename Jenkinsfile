String  committer, envType, version, image
String imageBaseName = 'allanweber/miro-widgets'
String prd = 'prd'
String master = 'master'
pipeline {
    agent any

    stages {
        stage ('Checking') {
            steps {
                echo 'Checking Branch Build: ' + env.BRANCH_NAME
                checkout scm
                script {
                    committer = sh(returnStdout: true, script: 'git show -s --pretty=%an').trim()
                }
                echo 'committer -> ' + committer

                script {
                    version = sh(returnStdout: true,
                    script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout')
                }
                script {
                    if (env.BRANCH_NAME == master) {
                        envType = prd
                        image = "${imageBaseName}:${version}"
                    }
                    else {
                        envType = 'dev'
                        image = "${imageBaseName}:${version}-${envType}-${env.BUILD_ID}"
                    }
                }
                echo "Building for ${envType} environment"
                echo 'project version: ' + version
                echo 'image name: ' + image
            }
        }
        stage('Test') {
            steps {
                sh 'mvn clean verify'
                step([$class: 'JUnitResultArchiver', testResults: 'target/surefire-reports/TEST-*.xml'])
            }
        }
        stage('Sonar') {
            when {
                not {
                    branch master
                }
            }
            steps {
                echo 'run sonarQube in future'
            }
        }
        stage('Build Image') {
            steps {
                script {
                    sh "docker build --build-arg ENV_ARG=${envType} -t ${image} ."
                }
            }
        }
        stage('Docker Login') {
            steps {
                script {
                    echo "${env.DOCKER_TOKEN} | docker login -u ${env.DOCKER_USER} --password-stdin"
                }
            }
        }
        // stage('Deploy Image') {
        //     steps {
        //         script {
        //             // sh "docker push ${image}"
        //             pushImage(image)
        //         }
        //     }
        // }
        stage('Deploy Parallel') {
            parallel deploy: {
                steps {
                    script {
                        pushImage(image)
                    }
                }
            } deployMaster: {
                when {
                    branch master
                }
                steps {
                    script {
                        String latestImage = "${imageBaseName}:latest"
                        sh "docker tag ${image} ${latestImage}"
                        pushImage(latestImage)
                        removeImage(latestImage)
                    }
                }
            }
        }
        // stage('Tag Latest') {
        //     when {
        //         branch master
        //     }
        //     steps {
        //         script {
        //             String latestImage = "${imageBaseName}:latest"
        //             sh "docker tag ${image} ${latestImage}"
        //             pushImage(latestImage)
        //             removeImage(latestImage)
        //         }
        //         // sh "docker tag ${image} ${imageBaseName}:latest"
        //         // sh "docker push ${imageBaseName}:latest"
        //         // sh "docker rmi ${imageBaseName}:latest -f"

        //     }
        // }
        stage('Remove Docker Image') {
            steps {
                // sh "docker rmi ${image}"
                removeImage(image)
            }
        }
    }
}

def pushImage(imageName) {
    sh "docker push ${imageName}"
}

def removeImage(imageName) {
    sh "docker rmi ${imageName}"
}
