# OIDCSimple - OAuth2/OIDC Authorization Server \& Client

OIDCSimple is a complete **Spring Boot OAuth2 + OpenID Connect (OIDC)** solution providing both an **Authorization Server** and an **OAuth2 Client**.
It supports configuration through **XML files**, **external configuration properties**, and **full Docker Compose deployment**.

***

## 🏗️ Project Architecture

| Module | Description |
| :-- | :-- |
| **server/** | Provides the OAuth2 Authorization and OIDC Server. Includes authentication, token issuance, revocation, and external XML-based configuration. |
| **client/** | Demonstrates an OAuth2 Client implementing Authorization Code Flow and accessing protected OIDC resources. |


***

## ✨ Features

- OAuth2 Authorization Code flow with refresh tokens
- OpenID Connect (OIDC) for identity management
- XML and external property configurations
- Admin endpoints for token management
- Custom token TTL and scope definitions
- Compatible with **Spring Boot 3.5+** and **Java 17+**
- Fully runnable with **Docker Compose**

***

## 🛠️ Requirements

- Java 17 or newer
- Maven 3.x+
- Docker \& Docker Compose (optional, for deployment)
- Git, VS Code, or IntelliJ IDEA for development

***

## 🚀 Quick Start (Maven)

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/OIDCSimple.git
cd OIDCSimple
```


### 2. Build the Modules

```bash
cd server && mvn clean install
cd ../client && mvn clean install
```


### 3. Run the Server

```bash
cd server
mvn spring-boot:run
```

Server runs at: [http://localhost:9080](http://localhost:9080)

### 4. Run the Client

```bash
cd client
mvn spring-boot:run
```

Client runs at: [http://localhost:8081](http://localhost:8081)

***

## ⚙️ Configuration Files

Internal configuration files (used by default):

```
server/src/main/resources/
├── users.xml
├── clients.xml
└── application.properties
```

To override them, you can use **external configuration files**.

***

## 🧩 External Configuration

### 1. Example Folder Structure

```
~/git/OIDCSimple/externalconfig/
├── users.xml
├── clients.xml
└── application.properties
```


### 2. External Application Properties

```properties
server.port=9080
server.servlet.session.timeout=300s

# Logging
logging.level.root=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.oauth2=DEBUG

# External XML paths
users.config.path=${user.home}/git/OIDCSimple/externalconfig/users.xml
clients.config.path=${user.home}/git/OIDCSimple/externalconfig/clients.xml
```


### 3. Example users.xml

```xml
<users>
  <user username="adminExternal" password="{noop}password" roles="ADMIN"/>
  <user username="userExt" password="{noop}secret" roles="USER"/>
</users>
```


### 4. Example clients.xml

```xml
<clients>
  <client>
    <id>external-client-id</id>
    <clientId>external-client</clientId>
    <clientSecret>{noop}extsecret</clientSecret>
    <clientName>External Application</clientName>
    <authenticationMethods>client_secret_basic</authenticationMethods>
    <grantTypes>authorization_code,refresh_token</grantTypes>
    <redirectUris>http://127.0.0.1:8081/login/oauth2/code/external-client-oidc</redirectUris>
    <scopes>openid,profile,email</scopes>
    <accessTokenDuration>300</accessTokenDuration>
    <refreshTokenDuration>900</refreshTokenDuration>
  </client>
</clients>
```


### 5. Launch with External Config

```bash
mvn spring-boot:run -Dspring.config.location="file:${user.home}/git/OIDCSimple/externalconfig/application.properties"
```

Or via your IDE (VM Options):

```
-Dspring.config.location=file:${user.home}/git/OIDCSimple/externalconfig/application.properties
```


***

## 🐳 Docker Compose Deployment

### 1. Create docker-compose.yml

```yaml
version: '3.8'

services:
  oidc-server:
    build: ./server
    container_name: oidcsimple-server
    ports:
      - "9080:9080"
    volumes:
      - ./externalconfig:/externalconfig
    environment:
      - SPRING_CONFIG_LOCATION=file:/externalconfig/application.properties
    networks:
      - oidc-network

  oidc-client:
    build: ./client
    container_name: oidcsimple-client
    ports:
      - "8081:8081"
    depends_on:
      - oidc-server
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    networks:
      - oidc-network

networks:
  oidc-network:
    driver: bridge
```


### 2. Run Both Services

```bash
docker-compose up --build
```
***

## 🔐 API Endpoints

### Authorization Server

| Endpoint | Method | Description |
| :-- | :-- | :-- |
| `/oauth2/authorize` | GET | Start of OAuth2 Authorization Code flow |
| `/oauth2/token` | POST | Token issuance endpoint |
| `/oauth2/revoke` | POST | Token revocation |
| `/.well-known/openid-configuration` | GET | Standard OIDC discovery document |
| `/admin/list-tokens/{username}` | GET | List tokens for a given user |
| `/admin/revoke-token` | POST | Revoke a token |
| `/admin/current-user` | GET | Info about the current authenticated user |

### OAuth2 Client

| Endpoint | Method | Description |
| :-- | :-- | :-- |
| `/` | GET | Home page |
| `/principal/info` | GET | Get OIDC principal info |
| `/principal/token` | GET | Token details |
| `/articles` | GET | Protected resource requiring scope `articles.read` |
| `/util/server-time` | GET | Return current server time |


***

## 🔄 Example OAuth2 Flow

1. Open `http://localhost:8081/articles`
2. Redirected to `http://localhost:9080/oauth2/authorize`
3. Log in with credentials (`admin` / `password`)
4. Approve requested scopes
5. Retrieve access and refresh tokens
6. Access protected resource `/articles`

***

## 🔧 Dockerfile Example

**server/Dockerfile**

```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 9080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**client/Dockerfile**

```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```


***

## 🔒 Security Best Practices

- Replace `{noop}` with `{bcrypt}` in production
- Always use HTTPS in production deployments
- Secure environment variables and client secrets
- Enable rate limiting on `/token` endpoints
- Regularly rotate keys and tokens

***

## 📜 License

This project is licensed under the **MIT License**.

***

## 🤝 Contributing

1. Fork the repository
2. Create a new branch: `feature/my-enhancement`
3. Commit your work: `git commit -m "Add feature"`
4. Push your branch: `git push origin feature/my-enhancement`
5. Open a Pull Request

***

