pipeline {
  agent {
    node {
      label 'maven'
    }

  }
 environment {
        DOCKER_CREDENTIAL_ID = 'dockerhub-id'
        GITHUB_CREDENTIAL_ID = 'github-id'
        KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
        REGISTRY = 'docker.io'
        DOCKERHUB_NAMESPACE = 'wanjiashengfolcx'
        GITHUB_ACCOUNT = 'wanjiashengfo'
        APP_NAME = 'devops-java-sample'
        SONAR_CREDENTIAL_ID = 'sonar-qube'
        BRANCH_NAME = 'master'
  }
  stages {
    stage('拉取代码') {
      steps {
        git(credentialsId: 'github-id', url: 'https://github.com/wanjiashengfo/gulibase.git', branch: 'master', changelog: true, poll: false)
        sh 'echo 正在构建 $PROJECT_NAME 版本号： $PROJECT_VERSION 将会提交给$REGISTRY  镜像仓库'
        sh "echo 正在完整编译项目"
        container ('maven') {
          sh "mvn clean install -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml"
        }

      }
    }
    stage('sonar代码质量分析') {
      steps {
        container ('maven') {
          withCredentials([string(credentialsId: "$SONAR_CREDENTIAL_ID", variable: 'SONAR_TOKEN')]) {
            withSonarQubeEnv('sonar') {
             sh "echo 当前目录 `pwd`"

             sh "mvn sonar:sonar -gs `pwd`/mvn-settings.xml -Dsonar.branch=$BRANCH_NAME -Dsonar.login=$SONAR_TOKEN"
            }
          }
          timeout(time: 1, unit: 'HOURS') {
            waitForQualityGate abortPipeline: true
          }
        }
      }
    }

    stage ('构建镜像 推送最新镜像') {
        steps {
            container ('maven') {
                sh 'mvn -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml clean package'
                sh 'cd $PROJECT_NAME && docker build -f Dockerfile -t $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
                withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                    sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:latest '
                    sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:latest '
                }
            }
        }
    }
    stage('部署到k8s') {
      steps {
        input(id: 'deploy-to-dev-$PROJECT_NAME', message: '是否将$PROJECT_NAME部署到集群中？')
        kubernetesDeploy(configs: '$PROJECT_NAME/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
      }
    }
    stage('发布版本'){
      when{
        expression{
          return params.PROJECT_VERSION =~ /v.*/
        }
      }
      steps {
          container ('maven') {
            input(id: 'release-image-with-tag', message: '发布当前版本镜像吗?')
              withCredentials([usernamePassword(credentialsId: "$GITHUB_CREDENTIAL_ID", passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                sh 'git config --global user.email "493000667@qq.com" '
                sh 'git config --global user.name "wanjiashengfo" '
                sh 'git tag -a $PROJECT_VERSION -m "$PROJECT_VERSION" '
                sh 'git push http://$GIT_USERNAME:$GIT_PASSWORD@github.com/$GITHUB_ACCOUNT/gulimall.git --tags --ipv4'
              }
            sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
            sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
        }
      }
    }
  }
  parameters {
    string(name: 'PROJECT_VERSION', defaultValue: 'v0.0Beta', description: '')
    string(name: 'PROJECT_NAME', defaultValue: '', description: '')
  }
}