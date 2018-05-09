#!groovy
// @Library('jenkins-pipeline-shared@develop') _

pipeline {
    agent any
    options { 
        timeout(time: 15)
        skipDefaultCheckout()
    }
    environment {
        SBT_TOOL = "${tool name: 'sbt-0.13.13', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'}/bin"
        PATH = "${env.SBT_TOOL}:${env.PATH}"
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
                // parallel('Unit': {
                //     echo 'running unit tests'
                //     sh 'sbt test'
                //     // sh '''
                //     //     sbt test
                //     //     sbt coverage
                //     //     sbt coverageReport
                //     // '''
                // },
                parallel('Static': {
                    echo 'performing static code analysis'
                    sh '''
                       sbt scalastyleGenerateConfig
                       sbt scalastyle
                       sbt scapegoat
                    '''
                },
                        'Mutation': {
                    echo 'performing mutation testing'
                    sh 'sbt mutationTest'
                }
                )
            }
        }
        post {
            always {
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'target/mutation-analysis-report',
                    reportFiles: 'overview.html',
                    reportTitles: "ScalaMu Report",
                    reportName: "ScalaMu Report"
                ])
                junit 'target/scala-2.11/scapegoat-report/scapegoat-scalastyle.xml'
            }
        }

        stage('Integration') {
            steps {
                echo 'running integration tests'
                // sh ''
            }
        }

        stage('Publish') {
            when {
                branch 'master'
            }
            steps {
                echo 'packaging and publishing release candidate to artifactory'
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
          
        stage('Promote') {
            steps {
                echo 'promoting candidate to release'
                // tag
                // push tag
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