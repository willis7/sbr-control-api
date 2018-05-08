#!groovy
// @Library('jenkins-pipeline-shared@develop') _

pipeline {
    agent any
    options { timeout(time: 5) }
    tools {
        sbt 'sbt_0.13.13'
    }
    stages {
        stage('Build') {
            steps {
                echo 'compiling project'
                checkout scm
                sh 'sbt compile'
            }
        }

        stage('Validate') {
            steps {
                parallel('Unit': {
                    echo 'running unit tests'
                },
                        'Static': {
                    echo 'performing static code analysis'
                }
                )
            }
        }

        stage('Integration') {
            steps {
                echo 'running integration tests'
            }
        }

        stage('Publish') {
            steps {
                echo 'packaging and publishing release to artifactory'
            }
        }

        stage('Deploy Dev') {
            when {
                branch 'master'
                environment name: 'DEPLOY_TO', value: 'development'
            }
            steps {
                echo "deploying release from artifactory into ${env.DEPLOY_TO}"
            }
        }

        stage('SIT') {
            when {
                branch 'master'
            }
            steps {
                echo 'performing a system integration test'
            }
        }

        stage('Deploy UAT') {
            when {
                branch 'master'
                environment name: 'DEPLOY_TO', value: 'uat'
            }
            steps {
                echo "deploying release from artifactory into ${env.DEPLOY_TO}"
            }
        }

        stage('Test UAT') {
            when {
                branch 'master'
            }
            steps {
                parallel('Regression': {
                    echo 'running regression tests'
                },
                        'Preformance': {
                    echo 'running performance tests'
                },
                        'Security': {
                    echo 'running security tests'
                }
                )
            }
        }
        
        stage('Deploy Production') {
            when {
                branch 'master'
                environment name: 'DEPLOY_TO', value: 'production'
            }
            steps {
                echo "deploying release from artifactory into ${env.DEPLOY_TO}"
            }
        }

        stage('Regression') {
            when {
                branch 'master'
            }
            steps {
                echo 'performing a small regression test'
            }
        }
    }
}