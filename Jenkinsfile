pipeline {
    agent any
    tools {
        maven 'maven_3.5.3'
    }
    stages {
        stage('Build & unit test') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Server test') {
            steps {
                echo 'Server test step'
//                sh 'mvn verify -Dskip.unit.tests'
            }
        }
        stage("publish to nexus") {
            when {
                expression {
                    pom = readMavenPom file: "pom.xml";

                    def server_version = "curl -v http://server:9211/version".execute().text
                    echo "*** Version: ${pom.version}";

                    if ( !pom.version.contains('SNAPSHOT') ) {
                        return 1
                    }
                    println "The version is a SNAPSHOT version and cannot be pushed to Nexus by Jenkins."
                    // If you want to, you can publish this snapshot-version from a command-prompt with 'mvn deploy'
                    return 0
                }
            }
            environment {
                NEXUS_VERSION = "nexus3"
                NEXUS_PROTOCOL = "http"
                NEXUS_URL = "server:8081"
                NEXUS_REPOSITORY = "repository/maven-releases"
                NEXUS_CREDENTIAL_ID = "jenkins"
            }
            steps {
                script {
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"

                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: pom.groupId,
                            version: pom.version,
                            repository: NEXUS_REPOSITORY,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [
                                [artifactId: pom.artifactId,
                                classifier: '',
                                file: artifactPath,
                                type: pom.packaging],

                                [artifactId: pom.artifactId,
                                classifier: '',
                                file: "pom.xml",
                                type: "pom"]
                            ]
                        );
                    } else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
            }
        }
    }
//    post {
//        always {
//            junit 'target/surefire-reports/*.xml'
//            junit 'target/failsafe-reports/*.xml'
//        }
//    }
}

