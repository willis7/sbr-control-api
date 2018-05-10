#!groovy
// @Library('jenkins-pipeline-shared@develop') _

pipeline {
    agent any
    options { timeout(time: 15) }
    environment {
        SBT_TOOL = "${tool name: 'sbt-0.13.13', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'}/bin"
        PATH = "${env.SBT_TOOL}:${env.PATH}"
    }
    stages {
        stage('Build') {
            steps {
                echo 'compiling project'
                sh 'sbt compile'
            }
        }

        stage('Validate') {
            steps {
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
            post {
                success {
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'target/mutation-analysis-report',
                        reportFiles: 'overview.html',
                        reportTitles: "ScalaMu Report",
                        reportName: "ScalaMu Report"
                    ])
                    // TODO: add health thresholds
                    checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: ['target/scalastyle-result.xml', 'target/scala-2.11/scapegoat-report/scapegoat-scalastyle.xml'], unHealthy: ''
                    // checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'target/scala-2.11/scapegoat-report/scapegoat-scalastyle.xml', unHealthy: ''
                }
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
                // create jar
                // push to artifactory snapshot
            }
        }

        stage('Deploy Dev') {
            when {
                branch 'master'
            }
            steps {
                echo "deploying release from artifactory into ${env.DEPLOY_TO}"
            }
        }

        stage('Test Dev') {
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
                // push to artifactory release
            }
        }
   
        stage('Deploy Production') {
            when {
                branch 'master'
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