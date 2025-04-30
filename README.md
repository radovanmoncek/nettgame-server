### nettgame server

is an example game server built upon my Nettgame game server framework.

#### How can I try?

- Make sure you either have Docker installed or the Java platform (optimally version > 21)
- Download the .jar from releases (needs DB, and special configuration) or pull the Docker Image (requires DB and .env) or run the already configured docker-compose.yaml (requires .env) *I strongly advise use of the last method mentioned*

#### Dependencies

- <https://github.com/radovanmoncek/nettgame> (.jar)
- <https://github.com/nettgame-tables> (.jar)

```xml        
        <!-- https://mvnrepository.com/artifact/com.mysql/mysql-connector-j -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.2.0</version>
            <scope>runtime</scope>
        </dependency>
```

#### Perforamnce analysis

![First run - .jar artifact](https://github.com/radovanmoncek/nettgame-server/blob/development/metrics/jar_test_1_source_client_WireShark.png)

![Second run - .jar artifact](https://github.com/radovanmoncek/nettgame-server/blob/development/metrics/jar_test_2_source_client_WireShark.png)

![First run - Docker container](https://github.com/radovanmoncek/nettgame-server/blob/development/metrics/docker_test_1_source_client_WireShark.png)

![Second run - Docker container](https://github.com/radovanmoncek/nettgame-server/blob/development/metrics/docker_test_2_source_client_WireShark.png)

#### UML class diagram

![UML class diagram of the nettgame example server](https://github.com/radovanmoncek/nettgame-server/blob/development/design/Nettgame_server_class_diagram.png)
