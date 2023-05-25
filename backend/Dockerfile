FROM maven:3.8.3-openjdk-8

RUN mkdir my_backend
WORKDIR /my_backend

COPY ./pom.xml .
RUN mvn dependency:go-offline

# COPY . /my_backend
COPY ./src /my_backend/src
EXPOSE 8080

CMD ["mvn", "spring-boot:run"]