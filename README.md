# VirtualLabsBE
AI project 2019/2020 Polytechnic of Turin

## Developers

* Ognibene Giuseppe: ***REMOVED***

* Pagano Alessandro: ***REMOVED***

* Rhaouati Hamza: ***REMOVED***

* Tolomei Leonardo: ***REMOVED***

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
* Run the following command:
```console
docker-compose up -d
```
* Enjoy!
    * VirtualLabs-BE is available at ``http://localhost:8080``
    * phpMyAdmin is available at ``http://localhost:8082``
    * MariaDB is available at ``http://localhost:3306``

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

## Other stuff

* https://docs.google.com/document/d/1skKK6x3YbBaqDJoYhsfk3NKiIwuAOlmXHtiWzrTeX10/edit

* https://drive.google.com/drive/u/0/folders/1dpIAYeQ7rLAE4yN67bUMnTTp-WR7Zx2s
