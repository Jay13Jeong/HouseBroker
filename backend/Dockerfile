FROM maven:3.8.3-openjdk-8

RUN mkdir /uploadImgaes
RUN mkdir my_backend
WORKDIR /my_backend

COPY ./pom.xml .
# 메이븐 의존성 다운로드
RUN mvn dependency:go-offline

# COPY . /my_backend
COPY ./src /my_backend/src
EXPOSE 8080

#-Dspring-boot.run.fork=false는 애플리케이션 fork를 진행하지않고 현프로세스에서 진행함.(hot reload가능)
#CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.fork=false"]
 CMD ["mvn", "spring-boot:run"]