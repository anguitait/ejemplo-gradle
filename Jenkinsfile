pipeline {
    agent any
    environment {
        NEXUS_USER         = credentials('user-nexus')
        NEXUS_PASSWORD     = credentials('password-nexus')
    }
    parameters {
        choice(
            name:'compileTool',
            choices: ['Maven', 'Gradle'],
            description: 'Seleccione herramienta de compilacion'
        )
    }
    stages {
        stage("Pipeline"){
            steps {
                script {
                    switch(params.compileTool) {
                        case 'Maven':
                            def ejecucion = load 'maven.groovy'
                            ejecucion.call()
                        break;
                        case 'Gradle':
                            def ejecucion = load 'gradle.groovy'
                            ejecucion.call()
                        break;
                    }
                }
            }
        }
    }
    post {
        success {
                slackSend (color: '#00FF00', message: "[Luis Anguita]['${env.JOB_NAME}']")
        }
        failure{
                slackSend (color: '#FF0000', message: "[Luis Anguita]['${env.JOB_NAME}']")
        }
    }
}