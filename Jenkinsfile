pipeline {
    agent {
        docker {
            image 'maven:3.5.2-jdk-9-slim'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -B clean compile'
            }
            post {
                always {
                    checkstyle pattern: '**/checkstyle-result.xml'
                    pmd canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '**/target/pmd.xml', unHealthy: ''
                    warnings canComputeNew: false, canResolveRelativePaths: false, categoriesPattern: '', consoleParsers: [[parserName: 'Java Compiler (javac)']], defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', messagesPattern: '', unHealthy: ''

                    // cpd
                }
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                 always {
                      stash name: "testresults", includes: "**/*-reports/**/*.xml,**/target/**/classes/**/*.class,**/target/**.exec"
                 }
            }

        }
        stage('Integration-Test') {
            steps {
                sh 'mvn -B verify'
            }
            post {
                 always {
                      stash name: "testresults", includes: "**/*-reports/**/*.xml,**/target/**/classes/**/*.class,**/target/**.exec"
                 }
            }
        }
        stage('Deploy') {
            steps {
                //timeout(time: 5, unit: 'DAYS') {
                //    input message: 'Approve deployment?'
                //}
                echo 'Deploy'
            }
        }
    }
    post {
        always {
            unstash 'testresults'
            junit '**/*-reports/**/*.xml'
            jacoco classPattern: '**/target/**/classes', execPattern: '**/target/jacoco-aggregate.exec'
        }
        success {
            archive "target/*-exec.jar"
        }
    }
}
