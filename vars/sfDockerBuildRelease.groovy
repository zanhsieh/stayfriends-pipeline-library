#!/usr/bin/groovy

def call(body) {

    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "build config = " + config

    def imageVersion = config.version
    
    container('client') {
      stage 'Build Release'
        imageVersion = performCanaryRelease {
          version = imageVersion
      }
    }

    return imageVersion
}
