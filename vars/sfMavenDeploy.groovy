#!/usr/bin/groovy


def call(body) {

    container(name: 'maven') {

        stage 'Deploy' 
        sh 'mvn clean install -U org.apache.maven.plugins:maven-deploy-plugin:2.8.2:deploy'
        
    }

}
