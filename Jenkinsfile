pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                 credentialsId: '466dcc78-0b3c-4b51-9c0b-28a9d809353c', 
                    url: 'https://github.com/SalahJamal1/Restaurant-MicroService'
            }
        }

        stage('Auth Service Build') {
            steps {
                dir('auth') {
                    sh "mvn clean package -DskipTests=true"
                }
            }
        }

        stage('Menu Service Build') {
            steps {
                dir('menu') {
                    sh "mvn clean package -DskipTests=true"
                }
            }
        }

        stage('Order Service Build') {
            steps {
                dir('order') {
                    sh "mvn clean package -DskipTests=true"
                }
            }
        }

        stage('Payment Service Build') {
            steps {
                dir('payment') {
                    sh "mvn clean package -DskipTests=true"
                }
            }
        }

        stage('OWASP Dependency Check') {
                steps { 
                    withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {
                        dependencyCheck additionalArguments: '--nvdApiKey ' + NVD_API_KEY + ' --scan ./ --format XML --format HTML',
                            odcInstallation: 'DP' 
                        
                    } 
                    
                }
            
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh '''
                        $SCANNER_HOME/bin/sonar-scanner \
                        -Dsonar.projectKey=restaurant-microservice \
                        -Dsonar.projectName=restaurant-microservice \
                        -Dsonar.java.binaries=auth/target/classes,menu/target/classes,order/target/classes,payment/target/classes 
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                            withCredentials([
                                usernamePassword(credentialsId: '8a0ea94d-bd48-4c46-a69f-6fc0a11e67d1', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')
                            ])
                            {
                            sh '''
                                docker login -u $DOCKER_USER -p $DOCKER_PASS
                                docker build --no-cache -t salah529/restaurant-microservice-auth:latest ./auth
                                docker build --no-cache -t salah529/restaurant-microservice-menu:latest ./menu
                                docker build --no-cache -t salah529/restaurant-microservice-order:latest ./order
                                docker build --no-cache -t salah529/restaurant-microservice-payment:latest ./payment
                                docker push salah529/restaurant-microservice-auth:latest
                                docker push salah529/restaurant-microservice-menu:latest
                                docker push salah529/restaurant-microservice-order:latest
                                docker push salah529/restaurant-microservice-payment:latest
                            '''
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                dir('k8s'){
                    script {
                        sh '''
                        kubectl apply -f .
                        '''
                     }
                    
                }
                
            }
        }
    }
}
