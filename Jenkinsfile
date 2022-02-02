pipeline {
    agent any
    environment {
        NEXUS_USER         = credentials('user-nexus')
        NEXUS_PASSWORD     = credentials('password-nexus')
    }
    parameters {
        choice(
            name: 'compileTool',
            choices: ['Maven', 'Gradle'],
            description: 'Maven o Gradle'
        )
    }
    stages {
        stage("Pipeline"){
            steps {
                script{
                    switch(params.compileTool)
                    {
                        case 'Maven':
                            def ejecucion = load 'maven.groovy'
                            ejecucion.call()
                        break;
                        case 'Gradle':
                            def ejecucion = load 'gradle.groovy'
                            ejecucion.call()
                        break;
                    }
                    stage("Paso 0: Download Code and checkout"){
                        checkout(
                            [$class: 'GitSCM',
                            //Acá reemplazar por el nonbre de branch
                            branches: [[name: "feature/maven-gradle " ]],
                            //Acá reemplazar por su propio repositorio
                            userRemoteConfigs: [[url: 'https://github.com/anguitait/ejemplo-gradle.git']]])
                    }
                    stage("Paso 1: Build && Test"){
                        sh "echo 'Build && Test!'"
                        sh "gradle build -x test"
                        // code
                    }
                    stage("Paso 2: Sonar - Análisis Estático"){
                        sh "echo 'Análisis Estático!'"
                        withSonarQubeEnv('SonarQube') {
                            sh "echo 'Calling sonar by ID!'"
                            // Run Maven on a Unix agent to execute Sonar.
                            sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=ejemplo-gradle'
                        }
                    }
                    stage("Paso 3: Curl Springboot Gradle sleep 20"){
                        sh "gradle bootRun&"
                        sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
                    }
                    stage("Paso 4: Subir Nexus"){
                        nexusPublisher nexusInstanceId: 'nexus',
                        nexusRepositoryId: 'devopsusach',
                        packages: [
                            [$class: 'MavenPackage',
                                mavenAssetList: [
                                    [classifier: '',
                                    extension: '.jar',
                                    filePath: 'build/DevOpsUsach2020-0.0.1.jar'
                                ]
                            ],
                                mavenCoordinate: [
                                    artifactId: 'DevOpsUsach2020',
                                    groupId: 'com.devopsusach2020',
                                    packaging: 'jar',
                                    version: '0.0.1'
                                ]
                            ]
                        ]
                    }
                    stage("Paso 5: Descargar Nexus"){
                        sh ' curl -X GET -u $NEXUS_USER:$NEXUS_PASSWORD "http://nexus:8081/repository/devops-usach-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar" -O'
                    }
                    stage("Paso 6: Levantar Artefacto Jar"){
                        sh 'nohup bash java -jar DevOpsUsach2020-0.0.1.jar & >/dev/null'
                    }
                    stage("Paso 7: Testear Artefacto - Dormir(Esperar 20sg) "){
                       sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
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
    }
}