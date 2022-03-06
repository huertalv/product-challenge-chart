// project specific variables
def defaultHelmUrl = 'https://get.helm.sh/helm-v3.0.2-linux-amd64.tar.gz'
def chartVersion = '1.2.0'

// Project parameters
def helmReleaseName = 'product-service'
def k8sContext = 'holmes-new'
def k8sNamespace = 'playground'

// Registry Credentials
def registry = 'https://my.registry.url/chartrepo/product-service'
def registryCredentialsId = 'registry_credentials'

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

        stage('Download Helm Chart') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: registryCredentialsId, usernameVariable: 'HELM_REPO_USR', passwordVariable: 'HELM_REPO_PWD')]){
                        sh "${WORKSPACE}/linux-amd64/helm repo add product-service-chart --username ${HELM_REPO_USR} --password '${HELM_REPO_PWD}' $registry"
                    }
                }
            }
        }

        stage('Install Helm Chart') {
            steps {
                script {
                    withCredentials([file(credentialsId: k8sCredentialId, variable: 'KUBECONFIG')]) {
                        sh "${WORKSPACE}/linux-amd64/helm upgrade -i --atomic ${helmReleaseName} product-service-chart --namespace ${k8sNamespace} --values custom_values.yaml --kube-context=${k8sContext} --version ${chartVersion}"
                    }
                }
            }
        }
    }
}