# Application Layer Load Balancer with Health Checks

This project implements a custom application layer load balancer using **Java** and **Spring Boot**, featuring **built-in health checks** and handles **concurrent requests** to ensure that incoming traffic is properly distributed among backend servers. The load balancer monitors server health and performs automatic failover in case of unhealthy servers, ensuring high availability and reliability.

## Features

- **Health checks** support with configurable auto health check frequency

- Easiliy Extensible 

- Supports **concurrent** requests

- Configurable for different load balancing algorithms and backend server settings
    * Currently available implementations
        - Round Robin
        - Weighted Round Robin

## UML Diagram 
<img src="readme.svg" alt="your-svg-image">
<div hidden>
```
@startuml

!theme cyborg-outline
top to bottom direction
skinparam linetype ortho

interface ILoadBalancerPolicy << interface >> {
   serverId: int
}
class LoadBalancerApplication {
  + LoadBalancerApplication(): 
  + main(String[]): void
}
class LoadBalancerController {
  + LoadBalancerController(): 
  - serverManager: ServerManager
  + handleRequest(HttpServletRequest, Map<String, String>): ResponseEntity<String>
   serverManager: ServerManager
}
class LoadBalancerPolicyFactory {
  + LoadBalancerPolicyFactory(): 
  - roundRobin: RoundRobin
  - weightedRoundRobin: WeightedRoundRobin
  + getPolicyImpl(String): ILoadBalancerPolicy
   weightedRoundRobin: WeightedRoundRobin
   roundRobin: RoundRobin
}
class RoundRobin {
  + RoundRobin(): 
   serverId: int
}
class ServerManager {
  + ServerManager(): 
  + healthCheckExecutor(): ResponseEntity<String>
  + init(): void
   loadBalancerPolicyFactory: LoadBalancerPolicyFactory
   healthyServer: String
}
class WeightedRoundRobin {
  + WeightedRoundRobin(): 
  + init(): void
   serverId: int
}

LoadBalancerController    "1" *-[#595959,plain]-> "serverManager\n1" ServerManager             
LoadBalancerPolicyFactory "1" *-[#595959,plain]-> "roundRobin\n1" RoundRobin                
LoadBalancerPolicyFactory "1" *-[#595959,plain]-> "weightedRoundRobin\n1" WeightedRoundRobin        
RoundRobin                 -[#008200,dashed]-^  ILoadBalancerPolicy       
ServerManager             "1" *-[#595959,plain]-> "loadBalancerPolicy\n1" ILoadBalancerPolicy       
ServerManager             "1" *-[#595959,plain]-> "factory\n1" LoadBalancerPolicyFactory 
WeightedRoundRobin         -[#008200,dashed]-^  ILoadBalancerPolicy       
@enduml


```
</div>
![](uml.svg)



