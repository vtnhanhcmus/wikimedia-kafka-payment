# Use the Jenkins LTS image with JDK 17 as a base image
FROM jenkins/jenkins:lts-jdk17

# Switch to root user for installation
USER root

# Install Docker dependencies
RUN apt-get update && \
    apt-get install -y apt-transport-https ca-certificates curl software-properties-common

# Add Docker repository and install Docker using the official script
RUN curl -fsSL https://get.docker.com | sh

# Switch back to the Jenkins user
USER jenkins