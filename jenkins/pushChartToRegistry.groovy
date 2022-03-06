  // Helm install version
  def helmUrl = 'https://get.helm.sh/helm-v3.2.1-linux-amd64.tar.gz'
  def helmPlugin = 'https://github.com/chartmuseum/helm-push'

  // Registry credentials
  def credentialsId = 'registry_credentials'

  // Registry URL
  def registryUrl = "https://my.registry.url/chartrepo/product-service"

  pipeline {
    agent {
      label 'incubation'
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

      stage ('Install helm and plugin') {
        steps {
          sh "mkdir -p bin && cd bin && wget --no-check-certificate $helmUrl && tar -xzf helm* && rm helm* && mv linux-amd64/* ./ && rm -rf linux-amd64"
          sh "./bin/helm plugin install $helmPlugin"
        }
      }

      stage ('Push chart to Registry') {
        steps {
          dir('chart') {
            withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'HELM_REPO_USERNAME', passwordVariable: 'HELM_REPO_PASSWORD')]) {
              sh "../bin/helm repo add product-service-chart --username '${HELM_REPO_USERNAME}' --password '${HELM_REPO_PASSWORD}' \"${registryUrl}\" "
              echo "Pushing Chart to Registry"
              sh "../bin/helm cm-push . product-service-chart"
            }
          }
        }
      }

    }
  }