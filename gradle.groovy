/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/
def call(){
    stage("Paso 1: Build && Test"){
        env.CURRENT_STAGE = STAGE_NAME
        sh "gradle clean build -x test"
    }
    stage("Paso 2: Sonar - Análisis Estático"){
        env.CURRENT_STAGE = STAGE_NAME
        sh "echo 'Análisis Estático!'"
        withSonarQubeEnv('SonarQube') {
            sh './gradlew sonarqube -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build'
        }
    }
    stage("Paso 3: Curl Springboot Gradle sleep 20"){
        env.CURRENT_STAGE = STAGE_NAME
        sh "gradle bootRun&"
        sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
    stage("Paso 4: Subir Nexus"){
        env.CURRENT_STAGE = STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus',
        nexusRepositoryId: 'devopsusach',
        packages: [
            [$class: 'MavenPackage',
                mavenAssetList: [
                    [classifier: '',
                    extension: '.jar',
                    filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar'
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
        env.CURRENT_STAGE = STAGE_NAME
        sh ' curl -X GET -u $user-nexus:$password-nexus "http://nexus:8081/repository/devopsusach/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar" -O'
    }
    stage("Paso 6: Levantar Artefacto Jar"){
        env.CURRENT_STAGE = STAGE_NAME
        sh 'nohup bash java -jar DevOpsUsach2020-0.0.1.jar & >/dev/null'
    }
    stage("Paso 7: Testear Artefacto - Dormir(Esperar 20sg) "){
        env.CURRENT_STAGE = STAGE_NAME
        sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
}
return this;