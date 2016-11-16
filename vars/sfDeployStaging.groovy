#!/usr/bin/groovy

def call(body) {

	stage 'Deploy Staging' 
		def utils = new io.fabric8.Utils()
		def envStage = utils.environmentNamespace('staging')
		kubernetesApply(environment: envStage)
	

}
