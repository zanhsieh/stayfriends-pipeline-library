#!/usr/bin/groovy

def call(body) {

    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    if ( !config.name ) {
    	config.name = env.JOB_NAME
    }
    echo "deploy config = " + config

    //container(name: 'client') {

	stage 'Deploy Staging' 
		def utils = new io.fabric8.Utils()
		def envStage = utils.environmentNamespace('staging')
		echo "deploying to environment: " + envStage

		// this file is read as default, as it is produced by maven plugin f-m-p
		rcName = "target/classes/META-INF/fabric8/kubernetes.yml"
		rc = ""
		if ( fileExists(rcName) ) {
			rc = readFile file: rcName
		} else {
			// alternative is for frontend project to generate the resource descriptions
   //    		withEnv(["KUBERNETES_NAMESPACE=${utils.getNamespace()}"]) {
   //      		rc = getKubernetesJson {
   //        			port = 80
			// 		label = 'nginx'
			// 		icon = 'https://cdn.rawgit.com/fabric8io/fabric8/dc05040/website/src/images/logos/nodejs.svg'
			// 		version = config.version
			//     }
			// }

			rc = sfKubernetesResourceWebapp {
				name = config.name
				container = config.container
				version = config.version
				port = 80
				image = "${env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST}:${env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT}/${env.KUBERNETES_NAMESPACE}/${config.name}:${config.version}"
				//icon = "https://cdn.rawgit.com/fabric8io/fabric8/dc05040/website/src/images/logos/nodejs.svg"
			}
		}

		echo "applying kubernetes rc: " + rc
		kubernetesApply(file: rc, environment: envStage)
		//sh "kubectl apply -f target/classes/META-INF/fabric8/kubernetes.yml"
	//}
}
