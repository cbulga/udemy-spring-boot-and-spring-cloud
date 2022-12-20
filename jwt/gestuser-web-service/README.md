# GestUser Web Service - JWT version

This version is based on the following spring projects:
- `config-server` (configuration server that provides all the microservices configuration files)

To run this project, the following environment variables need to be set:
- `profilo`: indicates which is the profile to be used when contacting the config server (https://github.com/cbulga/udemy-spring-boot-and-spring-cloud-git-config-repository. Possible values are: `prod`, `test`)
- `seq`: the final number of this microservice port (see `bootstrap.yml` -> `port: 801${seq}`. Ex.: `seq=9`)
- `ramo`: the git branch to use when getting the configuration from the Config Server