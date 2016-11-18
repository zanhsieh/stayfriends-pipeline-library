#!/usr/bin/groovy

// Create Kubernetes resource description for a generic web application,
// including deployment and service descriptions.

def call(body) {

    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "k8s resource config = " + config

    if ( !config.name ) error("name is null")
    if ( !config.version ) error("version is null")

    // json must start with "{"
    def rc = """{
      "apiVersion" : "v1",
      "kind" : "Template",
      "labels" : { },
      "metadata" : {
        "annotations" : {
          "description" : "${config.name} in ${config.container}",
          "fabric8.${config.name}/iconUrl" : "${config.icon}"
        },
        "labels" : { },
        "name" : "${config.name}"
      },
      "objects" : [{
        "kind": "Service",
        "apiVersion": "v1",
        "metadata": {
            "name": "${config.name}",
            "creationTimestamp": null,
            "labels": {
                "group": "${config.group}",
                "project": "${config.name}",
                "provider": "fabric8",
                "expose": "true",
                "version": "${config.version}"
            },
            "annotations": {
                "fabric8.${config.name}/iconUrl" : "${config.icon}",
                "prometheus.io/port": "${config.port}",
                "prometheus.io/scheme": "http",
                "prometheus.io/scrape": "true"
            }
        },
        "spec": {
            "ports": [
                {
                    "protocol": "TCP",
                    "port": 80,
                    "targetPort": ${config.port}
                }
            ],
            "selector": {
                "group": "${config.group}",
                "project": "${config.name}",
                "provider": "fabric8",
            },
            "type": "LoadBalancer",
            "sessionAffinity": "None"
        }
    },
    {
        "kind": "Deployment",
        "apiVersion": "extensions/v1beta1",
        "metadata": {
            "name": "${config.name}",
            "generation": 1,
            "creationTimestamp": null,
            "labels": {
                "group": "${config.group}",
                "project": "${config.name}",
                "provider": "fabric8",
                "version": "${config.version}"
            },
            "annotations": {
                "fabric8.${config.name}/iconUrl" : "${config.icon}"
            }
        },
        "spec": {
            "replicas": 1,
            "selector": {
                "matchLabels": {
                    "group": "${config.group}",
                    "project": "${config.name}",
                    "provider": "fabric8"
                }
            },
            "template": {
                "metadata": {
                    "creationTimestamp": null,
                    "labels": {
                        "group": "${config.group}",
                        "project": "${config.name}",
                        "provider": "fabric8",
                        "version": "${config.version}"
                    }
                },
                "spec": {
                    "containers": [
                        {
                            "name": "${config.name}",
                            "image": "${config.image}",
                            "ports": [
                                {
                                    "name": "web",
                                    "containerPort": ${config.port},
                                    "protocol": "TCP"
                                }
                            ],
                            "env": [
                                {
                                    "name": "KUBERNETES_NAMESPACE",
                                    "valueFrom": {
                                        "fieldRef": {
                                            "apiVersion": "v1",
                                            "fieldPath": "metadata.namespace"
                                        }
                                    }
                                }
                            ],
                            "resources": {},
                            "terminationMessagePath": "/dev/termination-log",
                            "imagePullPolicy": "IfNotPresent",
                            "securityContext": {}
                        }
                    ],
                    "restartPolicy": "Always",
                    "terminationGracePeriodSeconds": 30,
                    "dnsPolicy": "ClusterFirst",
                    "securityContext": {}
                }
            }
        }
    }]}
"""

    echo 'using Kubernetes resources:\n' + rc
    return rc

}
