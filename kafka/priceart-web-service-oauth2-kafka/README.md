# Priceart Web Service - OAuth2 version (Keycloak)

This version is based on a Keycloak docker instance to act as a OAuth2 server

To run this project, the following environment variables need to be set:
- profilo: indicates which is the profile to be used when contacting the config server (https://github.com/cbulga/udemy-spring-boot-and-spring-cloud-git-config-repository. Possible values are: '', 'git', 'github')
- seq: the final number of this microservice port (see application.yml ->   port: 507${seq}. Ex.: seq=1)