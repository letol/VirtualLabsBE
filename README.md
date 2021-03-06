# VirtualLabsBE
Politecnico di Torino - Master's Degree in Computer Engineering.

VirtualLabs is the project of the Internet Applications course of the academic year 2019/2020.

This is the VirtualLabs backend repository. For the frontend part refer to [VirtualLabsFE](https://github.com/pinoOgni/VirtualLabsFE).

## Developers

* [Giuseppe Ognibene](https://github.com/pinoOgni)

* [Alessandro Pagano](https://github.com/alexCodeRider)

* [Hamza Rhaouati](https://github.com/ReddaHawk)

* [Leonardo Tolomei](https://github.com/letol)

## Run latest image

Use this procedure to run the latest version of VirtualLabs-BE server uploaded on DockerHub.
With this procedure you can avoid cloning the entire repository.

* Create a new file called `docker-compose.yml` with this content:
```yaml
version: '3.3'

services:
  #service 1: definition of mysql database
  db:
    image: mariadb:latest
    container_name: ai-mariadb
    environment:
      - MYSQL_ROOT_PASSWORD=ai-mariadb
      - MYSQL_USER=root
    ports:
      - "3306:3306"
    restart: always


  #service 2: definition of phpMyAdmin
  phpmyadmin:
    image: phpmyadmin/phpmyadmin:latest
    container_name: my-php-myadmin
    ports:
      - "8082:80"
    restart: always

    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ai-mariadb


  #service 3: definition of spring-boot app
  virtual-labs-be:
    image: l3t0l/virtual-labs-be:latest
    container_name: virtual-labs-be
    ports:
      - "8080:8080"
    restart: always

    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://ai-mariadb:3306/customer?createDatabaseIfNotExist=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ai-mariadb
```
* Run the following commands:
```console
docker-compose pull
docker-compose up -d
```
* Enjoy!
    * VirtualLabs-BE is available at ``http://localhost:8080``
        * Default username: ``admin``
        * Default password: ``admin``
    * phpMyAdmin is available at ``http://localhost:8082``
        * Default username: ``root``
        * Default password: ``ai-mariadb``
    * MariaDB is available at ``http://localhost:3306``
        * Default username: ``root``
        * Default password: ``ai-mariadb``

## Build from code

Use this procedure to build your own VirtualLabs-BE server

* Clone this repository and open a terminal inside the new folder
* Run ``mvn install -DskipTests`` to build from code and create a jar file into ``Target`` Folder
* Run ``docker-compose up --build -d``. This command will build a container image that can execute the jar. It will 
  also download MariaDB and phpMyAdmin images. Finally, it will run all the containers.
  
## Stop and remove the containers
To stop the containers run:
```console
docker stop virtual-labs-be my-php-myadmin ai-mariadb
```
To remove the containers run:
```console
docker rm virtual-labs-be my-php-myadmin ai-mariadb
```

## Documentation

Project Requirements: https://docs.google.com/document/d/1skKK6x3YbBaqDJoYhsfk3NKiIwuAOlmXHtiWzrTeX10/edit
