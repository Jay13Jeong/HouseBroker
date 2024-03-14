FROM maven:3.8.3-openjdk-8

RUN mkdir /uploadImages
RUN mkdir /my_backend

# 소스 코드 복사
COPY ./src /my_backend/src

WORKDIR /my_backend
COPY ./pom.xml .
# 메이븐 의존성 다운로드 및 빌드
RUN mvn clean package -DskipTests
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.33.0/opentelemetry-javaagent.jar target/
# ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar target/
ENV JAVA_TOOL_OPTIONS "-javaagent:target/opentelemetry-javaagent.jar"

CMD ["java", "-jar", "target/houseBroker.jar", "--spring.profiles.active=prod"]

