node {
  def app

  stage('Clone repository') {
    checkout scm
  }

  stage('Build image') {
    app = docker.build("envirocar/qad")
  }

  stage('Push image') {
    docker.withRegistry('http://registry:5000') {
      app.push("latest")
    }
  }
}

