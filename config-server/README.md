# Config server

Basic config server, able to share a common configuration with all microservices involved in this project.
According to the profile used by this project, different scenarios can be managed:
- `application.yml`: the configuration is shared using this configuration file
- `application-git.yml`: the configuration is based on a file present in the local file system
- `application-github.yml`: the configuration is retrieved from a remote github repository

### environment variables example (required when starting this springboot project):
`uspwd=kSnZ/ISHShFEPeok00UCYUqFSUu3P+co;adpwd=1QhYQyk24MvDG9X/UetDKIChLEREECij;enpwd=amalaPazzaInterAmala`