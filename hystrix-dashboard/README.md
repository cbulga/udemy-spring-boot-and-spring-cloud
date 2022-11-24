# Hystrix Dashboard

Hystrix is the Netflix Circuit Breaker implementation. This project is in maintenance mode, so the Spring Cloud has
replaced it with a new Circuit Breaker implementation: Resilience4J.

Hystrix Dashboard consists of a web dashboard collecting data about the health status of each connected microservice.
Unfortunately, Resilience4J has not a web dashboard, so we have to wait for it...