pipeline {
  agent {
    node {
      label 'maven'
    }
  }
  parameters {
    string(name: 'PROJECT_VERSION', defaultValue: 'v0.0Beta', description: '')
    string(name: 'PROJECT_NAME', defaultValue: '', description: '')
  }
  stages {
    stage('拉取代码') {
      steps {
        git(credentialsId: 'github-id', url: 'https://github.com/wanjiashengfo/gulibase.git', branch: 'master', changelog: true, poll: false)
        sh 'echo 正在构建 $PROJECT_NAME 版本号： $PROJECT_VERSION'
      }
    }
  }
}