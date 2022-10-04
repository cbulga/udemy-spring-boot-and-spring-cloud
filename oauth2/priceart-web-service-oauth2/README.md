# Priceart Web Service - OAuth2 version (Keycloak)

This version is based on a Keycloak docker instance to act as a OAuth2 server

To run this project, the following environment variables need to be set:
- profilo: indicates which is the profile to be used when contacting the config server (https://github.com/cbulga/udemy-spring-boot-and-spring-cloud-git-config-repository. Possible values are: '', 'git', 'github')
- seq: the final number of this microservice port (see application.yml ->   port: 507${seq}. Ex.: seq=1)

Folder structure summary:
- config-server: base config server to share the project configuration to all microservices
- jwt: the security is based on JWT tokens (the jwt-auth-server manages them acting as an identity provider)
- kafka: the config server uses kafka to share the project configuration to all microservices
- oauth2: the security is based on OAuth2 protocol (the identity provider that manages the authorization phase is keycloak: it returns a JWT token once the user has been successfully authenticated)

It is recommended to use the projects present in the following folders:
- config-server
- oauth2