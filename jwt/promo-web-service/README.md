# Promo Web Service - No secured version (the security is entirely managed by the gateway-jwt service)

To run this project, the following environment variables need to be set:
- profilo: indicates which is the profile to be used when contacting the config server (https://github.com/cbulga/udemy-spring-boot-and-spring-cloud-git-config-repository. Possible values are: 'prod', 'test')
- seq: the final number of this microservice port (see application.yml ->   port: 509${seq}. Ex.: seq=1)