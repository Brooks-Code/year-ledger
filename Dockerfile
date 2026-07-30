# 使用 Maven 构建
FROM maven:3.9.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 使用 JDK 运行
FROM openjdk:17-jdk-slim
WORKDIR /app
# 复制构建好的 jar 包，请根据实际生成的文件名修改 *.jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]