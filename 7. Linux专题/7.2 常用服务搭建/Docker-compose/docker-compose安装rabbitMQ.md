```
version: '3'
services:
    rabbitMQ:
      image: rabbitmq:3.7.8-management
      hostname: localhost
      ports:
        - "4369:4369"
        - "5671:5671"
        - "5672:5672"
        - "15671:15671"
        - "15672:15672"
      environment:
        - RABBITMQ_DEFAULT_USER=root
        - RABBITMQ_DEFAULT_PASS=root

````  
