# Articoli Web Service - JWT version

This version is based on the following spring projects:
- `jwt-auth-server` (server JWT that creates and refreshes JWT tokens according to the provided credentials)
- `gestuser-web-service` (server that validates the provided credentials against a mongodb)

To run this project, the following environment variables need to be set:
- profilo: indicates which is the profile to be used when contacting the config server (https://github.com/cbulga/udemy-spring-boot-and-spring-cloud-git-config-repository. Possible values are: 'prod', 'test')
- seq: the final number of this microservice port (see application.yml ->   port: 505${seq}. Ex.: seq=1)