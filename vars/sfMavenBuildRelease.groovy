#!/usr/bin/groovy


def call(body) {

    container(name: 'maven') {

        stage 'Build Release' 

            def versionPrefix = ""
            try {
              versionPrefix = VERSION_PREFIX
            } catch (Throwable e) {
              versionPrefix = "1.0"
            }

            def canaryVersion = "${versionPrefix}.${env.BUILD_NUMBER}"
            mavenCanaryRelease {
                version = canaryVersion
            }
        
    }

}
