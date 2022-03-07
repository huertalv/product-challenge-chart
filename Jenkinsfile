// project specific variables
def defaultHelmUrl = 'https://get.helm.sh/helm-v3.0.2-linux-amd64.tar.gz'
def branchChartVersion = 'main'

// Project parameters
def helmReleaseName = 'product-service'
def k8sContext = 'k8s-context'
def k8sNamespace = 'k8s-namespace'

def k8sCredentialId = 'kubeconfig'

pipeline {

    agent {
        label 'incubation'
    }

    options {
      skipDefaultCheckout true
    }

    stages {

      stage('Checkout repository') {
        steps {
          dir('chart') {
            checkout scm
            sh 'rm -rf .git && rm Jenkinsfile'
          }
        }
      }

      stage ('Install Helm') {
        steps {
          sh "wget -O helm.tar.gz ${defaultHelmUrl} && tar -zxvf helm.tar.gz"
        }
      }

      stage('Install Helm Chart From Repository') {
        steps {
          dir('chart') {
            script {
              withCredentials([file(credentialsId: k8sCredentialId, variable: 'KUBECONFIG')]) {
                sh "${WORKSPACE}/linux-amd64/helm upgrade -i --atomic ${helmReleaseName} ./ --namespace ${k8sNamespace} --kube-context=${k8sContext}"
              }
            }
          }
        }
      }

    }
}