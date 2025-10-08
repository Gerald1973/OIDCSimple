# OIDCSimple

A simple OAuth2/OpenID Connect (OIDC) authorization server and client implementation using Spring Boot. This project demonstrates basic authentication flows, token management, and secure API access.

## Overview

This repository contains two main modules:

- **Server (AuthenticatorOauth2)**: An OAuth2 authorization server that handles user authentication, token issuance, and revocation. It supports OIDC for identity management and uses an XML file for user storage.
- **Client (ClientOauth2)**: An OAuth2 client application that integrates with the server for login and protected resource access (e.g., fetching articles).

Key features:

- Authorization Code Grant with Refresh Tokens.
- OpenID Connect (OIDC) for user info.
- Admin endpoints for token revocation and listing.
- In-memory user management loaded from `users.xml`.
- Logging configured for security and OAuth2 debugging.

## Prerequisites

- Java 17 or higher.
- Maven 3.x.
- Optional: IDE like IntelliJ IDEA or VS Code with Java extensions (see `.vscode` configurations for launch settings).

## Installation

1. Clone the repository:

```sh
git clone https://github.com/yourusername/OIDCSimple.git
cd OIDCSimple
```

2. Build the projects using Maven:

```sh
cd server
mvn clean install
cd ../client
mvn clean install
```

## Configuration

Configurations are defined in `application.properties` files for both modules.

### Server Configuration (`server/src/main/resources/application.properties`)

- Server runs on port 9080.
- Issuer URI: `http://fakelocalhost:9080`.
- Client registration for `articles-client` with scopes `openid` and `articles.read`.
- Users loaded from `users.xml` (e.g., admin/password with ADMIN role, user1/secret with USER role).

### Client Configuration (`client/src/main/resources/application.properties`)

- Client runs on port 8081.
- Connects to the server at `http://fakelocalhost:9080`.
- Registrations for OIDC login and authorization code grant.

Note: Use `http://127.0.0.1` for local redirects to avoid hostname resolution issues.

## Usage

### Running the Applications

1. Start the server:

```sh
cd server
mvn spring-boot:run
```

- Access the authorization server at `http://localhost:9080`.

2. Start the client:

```sh
cd client
mvn spring-boot:run
```

- Access the client at `http://localhost:8081`.

### Key Endpoints

#### Server (Authorization Server)

- `/oauth2/authorize`: Authorization endpoint.
- `/oauth2/token`: Token issuance.
- `/admin/revoke-token`: Revoke a token (POST, admin only).
- `/admin/list-tokens/{username}`: List tokens for a user (GET, admin only).
- `/admin/current-user`: Check current authenticated user (GET).
- `/admin/logout`: Logout current user (GET).

#### Client

- `/oauth2/authorization/articles-client-oidc`: Initiate OIDC login.
- `/articles`: Protected endpoint to fetch articles (requires `articles.read` scope).
- `/principal/info`: Get user principal info.
- `/principal/token`: Get token details.
- `/principal/logout`: Logout from client.

### Example Flow

1. Access client protected endpoint (e.g., `http://localhost:8081/articles`).
2. Redirected to server for login (use credentials from `users.xml`).
3. After authorization, client receives tokens and accesses resources.

## Development Notes

- **Security**: Uses Spring Security with form login. Custom `OAuth2AuthorizationService` for principal-based token lookup.
- **Logging**: Set to INFO/DEBUG for security events.
- **VS Code Integration**: Use `launch.json` for debugging both modules.
- **Dependencies**: Managed via Maven (Spring Boot 3.3.x, OAuth2 modules).

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details (if not present, assume open-source for demonstration purposes).
