# Eureka server

Eureka server is a service discovery that helps us to know where each microservice is located (useful to manage horizontal redundancy)

### environment variables example (required when starting this springboot project):
`uspwd=kSnZ/ISHShFEPeok00UCYUqFSUu3P+co;adpwd=1QhYQyk24MvDG9X/UetDKIChLEREECij;enpwd=amalaPazzaInterAmala`

### Eureka endpoints
<!-- TOC -->
* [GET /eureka/apps](http://localhost:8761/eureka/apps) (visualliza i dettagli di tutte le app registrate)
* [GET /eureka/apps/[appID]](http://localhost:8761/eureka/apps/[appID]) (visualizza i dettagli di una app specifica)
* [GET /eureka/apps/[appID]/[instanceID]](http://localhost:8761/eureka/apps/[appID]/[instanceID]) (visualizza i dettagli di una istanza di una app specifica)
* [PUT /eureka/apps/[appID]/[instanceID]](http://localhost:8761/eureka/apps/[appID]/[instanceID]) (invia un heartbeat ad una specifica istanza di una app)
* [DELETE /eureka/apps/[appID]/[instanceID]](http://localhost:8761/eureka/apps/[appID]/[instanceID]) (elimina una specifica istanza dell'app selezionata)
* [POST /eureka/apps/[appID]](http://localhost:8761/eureka/apps/[appID]) (aggiunge una istanza dell'app selezionata)
<!-- TOC -->