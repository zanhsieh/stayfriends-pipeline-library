#!/usr/bin/groovy

def call() {

	stage 'Deploy Staging' 
		def utils = new io.fabric8.Utils()
		def envStage = utils.environmentNamespace('staging')
		kubernetesApply(environment: envStage)
	

}
