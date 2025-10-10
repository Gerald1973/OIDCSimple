# OIDCSimple - OAuth2/OIDC Authorization Server & Client

A comprehensive OAuth2/OpenID Connect (OIDC) implementation using Spring Boot. This project provides a complete authorization server and client setup with XML-based configuration for users and OAuth2 clients.

## 🏗️ Project Architecture

This repository contains two main modules:

### 🔐 Authorization Server (`server/`)
- OAuth2 authorization server handling authentication, token issuance, and revocation
- OpenID Connect (OIDC) support for identity management
- XML-based user and client configuration
- Admin endpoints for token management
- Custom token customization and authorization services

### 👤 OAuth2 Client (`client/`)
- OAuth2 client application demonstrating secure resource access
- Integration with the authorization server for authentication
- Protected endpoints requiring specific scopes
- Token-aware WebClient for API calls

## ✨ Key Features

- **Authorization Code Grant** with Refresh Tokens
- **OpenID Connect (OIDC)** for identity management
- **XML-based Configuration** for users and OAuth2 clients
- **Admin Token Management** (revocation, listing, user lookup)
- **Scope-based Authorization** (`openid`, `articles.read`, etc.)
- **Custom Token Customization** with configurable TTL
- **Comprehensive Logging** for security and OAuth2 debugging
- **Production-ready Security** configuration

## 🛠️ Prerequisites

- **Java 17** or higher
- **Maven 3.x**
- **IDE** (IntelliJ IDEA, VS Code with Java extensions)
- **Git** for version control

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/OIDCSimple.git
cd OIDCSimple
```

### 2. Build the Projects
```bash
# Build authorization server
cd server
mvn clean install

# Build client
cd ../client
mvn clean install
```

### 3. Start the Authorization Server
```bash
cd server
mvn spring-boot:run
```
Authorization server will be available at: `http://localhost:9080`

### 4. Start the Client Application
```bash
cd client
mvn spring-boot:run
```
Client application will be available at: `http://localhost:8081`

## ⚙️ Configuration

### 🔧 User Configuration (`users.xml`)

Users are defined in `server/src/main/resources/users.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<users>
    <user username="admin" password="{noop}password" roles="ADMIN"/>
    <user username="user1" password="{noop}secret" roles="USER"/>
</users>
```

#### User Configuration Fields:
- **`username`**: Unique identifier for the user
- **`password`**: Password with encoding prefix (`{noop}` for plain text, `{bcrypt}` for BCrypt)
- **`roles`**: Comma-separated roles (ADMIN, USER, etc.)

### 🏢 Client Configuration (`clients.xml`)

OAuth2 clients are configured in `server/src/main/resources/clients.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<clients>
    <client id="articles-client-id">
        <clientId>articles-client</clientId>
        <clientSecret>{noop}secret</clientSecret>
        <clientName>Articles Client</clientName>
        <authenticationMethods>client_secret_basic</authenticationMethods>
        <grantTypes>authorization_code,refresh_token</grantTypes>
        <redirectUris>http://127.0.0.1:8081/login/oauth2/code/articles-client-oidc,http://127.0.0.1:8081/authorized</redirectUris>
        <scopes>openid,articles.read</scopes>
        <accessTokenDuration>18000</accessTokenDuration>
        <refreshTokenDuration>86400</refreshTokenDuration>
    </client>
</clients>
```

#### Client Configuration Fields:
- **`id`**: Internal unique identifier
- **`clientId`**: OAuth2 client identifier
- **`clientSecret`**: Client secret with encoding prefix
- **`clientName`**: Human-readable client name
- **`authenticationMethods`**: Supported authentication methods
  - `client_secret_basic`: HTTP Basic authentication
  - `client_secret_post`: POST form parameters
  - `client_secret_jwt`: JWT assertion
  - `private_key_jwt`: Private key JWT
  - `none`: Public client
- **`grantTypes`**: Supported OAuth2 grant types
  - `authorization_code`: Authorization Code Grant
  - `refresh_token`: Refresh Token Grant
  - `client_credentials`: Client Credentials Grant
- **`redirectUris`**: Comma-separated authorized redirect URIs
- **`scopes`**: Comma-separated OAuth2 scopes
- **`accessTokenDuration`**: Access token TTL in seconds
- **`refreshTokenDuration`**: Refresh token TTL in seconds

### 📋 Application Properties

#### Authorization Server (`server/src/main/resources/application.properties`)
```properties
# Server Configuration
server.port=9080

# OAuth2 Authorization Server
spring.security.oauth2.authorizationserver.issuer=http://fake.localhost:9080

# Logging Configuration
logging.level.root=INFO
logging.level.org.springframework.web=INFO
logging.level.org.springframework.security=INFO
logging.level.org.springframework.security.oauth2=DEBUG
```

#### OAuth2 Client (`client/src/main/resources/application.properties`)
```properties
# Server Configuration
server.port=8081

# OAuth2 Client Registration
spring.security.oauth2.client.registration.articles-client-oidc.provider=spring
spring.security.oauth2.client.registration.articles-client-oidc.client-id=articles-client
spring.security.oauth2.client.registration.articles-client-oidc.client-secret=secret
spring.security.oauth2.client.registration.articles-client-oidc.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.articles-client-oidc.redirect-uri=http://127.0.0.1:8081/login/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.articles-client-oidc.scope=openid
spring.security.oauth2.client.registration.articles-client-oidc.client-name=articles-client-oidc

# OAuth2 Provider Configuration
spring.security.oauth2.client.provider.spring.issuer-uri=http://localhost:9080
```

## 🌐 API Endpoints

### 🔐 Authorization Server Endpoints

#### OAuth2 Core Endpoints
- **`GET /oauth2/authorize`** - Authorization endpoint for OAuth2 flows
- **`POST /oauth2/token`** - Token endpoint for access/refresh tokens
- **`POST /oauth2/revoke`** - Token revocation endpoint
- **`GET /.well-known/openid_configuration`** - OIDC discovery endpoint

#### Admin Endpoints (Requires ADMIN role)
- **`POST /admin/revoke-token`** - Revoke a specific token
- **`GET /admin/list-tokens/{username}`** - List all tokens for a user
- **`GET /admin/current-user`** - Get current authenticated user info
- **`GET /admin/logout`** - Logout current user

### 👤 OAuth2 Client Endpoints

- **`GET /`** - Home page (redirects to principal info)
- **`GET /principal/info`** - Get authenticated user information
- **`GET /principal/token`** - Get current token details
- **`GET /principal/logout`** - Logout from client
- **`GET /articles`** - Protected resource (requires `articles.read` scope)
- **`GET /util/server-time`** - Utility endpoint for server time
- **`GET /util/timezones`** - Utility endpoint for available timezones

## 🔄 OAuth2 Flow Example

1. **Access Protected Resource**: Navigate to `http://localhost:8081/articles`
2. **Redirect to Authorization Server**: Client redirects to `http://localhost:9080/oauth2/authorize`
3. **User Authentication**: Login with credentials from `users.xml` (e.g., `admin`/`password`)
4. **Authorization Grant**: User grants access to requested scopes
5. **Token Exchange**: Client exchanges authorization code for access token
6. **Access Protected Resource**: Client uses access token to fetch articles

## 🔧 Development

### Project Structure
```
OIDCSimple/
├── server/                          # Authorization Server
│   ├── src/main/java/
│   │   └── com/smilesmile1973/authenticatoroauth2/
│   │       ├── config/             # Security and OAuth2 configuration
│   │       ├── controller/         # REST endpoints
│   │       ├── model/              # XML mapping models
│   │       ├── service/            # Business logic services
│   │       └── util/               # Utility classes
│   ├── src/main/resources/
│   │   ├── users.xml              # User configuration
│   │   ├── clients.xml            # OAuth2 client configuration
│   │   └── application.properties # Server properties
│   └── pom.xml                    # Maven dependencies
├── client/                         # OAuth2 Client
│   ├── src/main/java/
│   │   └── com/smilesmile1973/clientoauth2/
│   │       ├── config/            # Security and WebClient configuration
│   │       └── controller/        # REST endpoints
│   ├── src/main/resources/
│   │   └── application.properties # Client properties
│   └── pom.xml                   # Maven dependencies
└── .vscode/                      # VS Code configuration
    ├── launch.json              # Debug configurations
    └── settings.json           # Workspace settings
```

### 🐛 Debugging

#### VS Code Configuration
The project includes pre-configured launch settings for debugging both applications:

- **AuthenticatorOauth2Application**: Debug authorization server
- **ClientOauth2Application**: Debug OAuth2 client

#### Logging
- Security events: `INFO` level
- OAuth2 flows: `DEBUG` level
- Customize logging in `application.properties`

### 🧪 Testing

#### Manual Testing Flow
1. Start both applications
2. Access `http://localhost:8081/articles`
3. Login with `admin`/`password`
4. Verify token exchange and resource access

#### Admin Token Management
1. Login as admin user
2. Access `http://localhost:9080/admin/list-tokens/user1`
3. Test token revocation via `POST /admin/revoke-token`

## 🚨 Security Considerations

### Production Deployment
- **Replace `{noop}` passwords** with `{bcrypt}` encoded passwords
- **Use HTTPS** for all communications
- **Secure client secrets** and use proper key management
- **Configure proper CORS** settings
- **Use production-grade databases** instead of in-memory storage
- **Implement rate limiting** for token endpoints
- **Regular security audits** and dependency updates

### Network Configuration
- Use `127.0.0.1` instead of `localhost` for redirect URIs to avoid hostname resolution issues
- Configure proper firewall rules for production environments

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

If you encounter any issues or have questions:

1. Check the logs for detailed error messages
2. Verify configuration files (`users.xml`, `clients.xml`, `application.properties`)
3. Ensure proper port availability (9080, 8081)
4. Create an issue on GitHub with detailed information

---

**Built with ❤️ using Spring Boot, Spring Security, and OAuth2/OIDC standards**
