# 阶段 1：使用 Maven 构建
FROM maven:3.9.4-eclipse-temurin-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 阶段 2：使用 JDK 8 运行（替换为体积更小的 alpine 镜像）
FROM eclipse-temurin:8-jdk-alpine
WORKDIR /app

# 复制构建好的 jar 包
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# 核心：通过环境变量 PORT 监听云平台分配的端口，若未分配则默认 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT:-8080}"]