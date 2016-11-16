#!/usr/bin/groovy

def call(body) {

	stage 'Deploy Staging' 
		def utils = new io.fabric8.Utils()
		def envStage = utils.environmentNamespace('staging')
		echo "deploying to environment: " envStage

		// this file is read as default, as it is produced by maven plugin f-m-p
		rc = readFile file: "target/classes/META-INF/fabric8/kubernetes.yml"
		echo "kubernetes rc = " + rc

		kubernetesApply(environment: envStage)
	

}
