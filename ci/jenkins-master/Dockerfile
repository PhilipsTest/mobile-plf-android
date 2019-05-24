FROM jenkins/jenkins:2.89.2

# install our plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

ENV LDAP_GROUP_NAME_ADMIN=ggNLVEHVRES2-CDPP_Eindhoven

# fetch custom TICs plugin from artifactory
USER root
RUN curl https://artifactory-ehv.ta.philips.com:8082/artifactory/platform-plugins-release-local/com/tiobe/tics-jenkins/tics-jenkins-1.0.9.tgz -o ./tics.tgz
RUN tar -xvzf ./tics.tgz && cp *.jpi /usr/share/jenkins/ref/plugins

USER jenkins
# deploy custom initialization scripts 
COPY config/*.groovy /usr/share/jenkins/ref/init.groovy.d/

# configure default jobs
COPY jobs/ /usr/share/jenkins/ref/jobs/
