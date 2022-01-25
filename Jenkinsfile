import groovy.json.JsonSlurperClassic
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}
pipeline {
    agent any
    environment{
        NEXUS_USER = credentials('user-nexus')
        NEXUS_PASS = credentials('password-nexus')
    }
    stages {
        stage("Paso 1: Compilar"){
            steps {
                script {
                sh "echo 'Compile Code!'"
                // Run Maven on a Unix agent.
                sh "mvn clean compile -e"
                }
            }
        }
        stage("Paso 2: Testear"){
            steps {
                script {
                sh "echo 'Test Code!'"
                // Run Maven on a Unix agent.
                sh "mvn clean test -e"
                }
            }
        }
        stage("Paso 3: Build .Jar"){
            steps {
                script {
                sh "echo 'Build .Jar!'"
                // Run Maven on a Unix agent.
                sh "mvn clean package -e"
                }
            }
            post {
                //record the test results and archive the jar file.
                success {
                    archiveArtifacts artifacts:'build/*.jar'
                }
            }
        }
        stage("Paso 4: Análisis SonarQube"){
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "echo 'Calling sonar Service in another docker container!'"
                    // Run Maven on a Unix agent to execute Sonar.
                    sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=MYTREASURE'
                }
            }
        }
        stage('Paso 5: Subir a nexus') {
            steps {
                nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'devospusach', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: '/var/jenkins_home/workspace/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
            }
        }
        stage('Paso 6: Descargar desde nexus') {
            steps {
                sh 'curl -X GET -u ${NEXUS_USER}:${NEXUS_PASS} http://nexus:8081/repository/devospusach/devopsusach/devopsusach/0.0.1/devopsusach-0.0.1.jar -O '
            }
        }
        stage("Paso 7:Run: Levantar Springboot APP"){
            steps {
                sh 'nohup java -jar devopsusach-0.0.1.jar & >/dev/null'
            }
        }
        stage("Paso 8:Curl: Dormir(Esperar 20sg) "){
            steps {
               sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
            }
        }
        stage("Paso 9:Subir nueva Version"){
            steps {
                //archiveArtifacts artifacts:'build/*.jar'
                nexusPublisher nexusInstanceId: 'nexus',
                    nexusRepositoryId: 'devospusach',
                    packages: [
                        [$class: 'MavenPackage',
                            mavenAssetList: [
                                [classifier: '',
                                extension: '.jar',
                                filePath: 'devopsusach-0.0.1.jar']
                            ],
                    mavenCoordinate: [
                        artifactId: 'DevOpsUsach2020',
                        groupId: 'com.devopsusach2020',
                        packaging: 'jar',
                        version: '1.0.0']
                    ]
                ]
            }
    }
}
    post {
        always {
            sh "echo 'fase always executed post'"
        }
        success {
            sh "echo 'fase success'"
        }
        failure {
            sh "echo 'fase failure'"
        }
    }
}