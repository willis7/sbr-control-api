#!groovy
@Library('jenkins-pipeline-shared@develop') _

pipeline {
    agent any

    stages {
        stage('Build') {
            echo 'compiling project'
        }

        stage('Validate') {
            failFast true
            parallel {
                stage('Unit') {
                    echo 'running unit tests'
                }

                stage('Static') {
                    echo 'performing static code analysis'
                }
            }
        }

        stage('Integration') {
            echo 'running integration tests'
        }

        stage('Publish') {
            echo 'packaging and publishing release to artifactory'
        }

        stage('Deploy Dev') {
            when {
                branch 'master'
                environment name: 'DEPLOY_TO', value: 'development'
            }
            echo 'deploying release from artifactory into Development'
        }

        stage('SIT') {
            when {
                branch 'master'
            }
            echo 'performing a system integration test'
        }

        stage('Deploy UAT') {
            when {
                branch 'master'
                environment name: 'DEPLOY_TO', value: 'uat'
            }
            echo 'deploying release from artifactory into UAT'
        }

        stage('NFR') {
            when {
                branch 'master'
            }
            failFast true
            parallel {
                stage('Regression') {
                    echo 'running regression tests'
                }
                
                stage('Performance') {
                    echo 'running performance/load tests'
                }

                stage('Security') {
                    echo 'running security tests'
                }
            }
        }
        
        stage('Deploy Production') {
            when {
                branch 'master'
                environment name: 'DEPLOY_TO', value: 'production'
            }
            echo 'deploying release from artifactory into Production'
        }

        stage('Regression') {
            when {
                branch 'master'
            }
            echo 'performing a small regression test'
        }
    }
}