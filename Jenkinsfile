// project specific variables
def defaultHelmUrl = 'https://get.helm.sh/helm-v3.0.2-linux-amd64.tar.gz'
def branchChartVersion = 'main'

// Project parameters
def helmReleaseName = 'product-service'
def k8sContext = 'holmes-new'
def k8sNamespace = 'playground'

def k8sCredentialId = 'kubeconfig'

pipeline {

    agent {
        label 'incubation'
    }

    stages {
        stage ('Install Helm') {
            steps {
                sh "wget -O helm.tar.gz ${defaultHelmUrl} && tar -zxvf helm.tar.gz"
            }
        }

        stage('Install Helm Chart From Repository') {
            steps {
                script {
                    withCredentials([file(credentialsId: k8sCredentialId, variable: 'KUBECONFIG')]) {
                        sh "${WORKSPACE}/linux-amd64/helm upgrade -i --atomic ${helmReleaseName} ./ --namespace ${k8sNamespace} --values custom_values.yaml --kube-context=${k8sContext}"
                    }
                }
            }
        }
    }
}