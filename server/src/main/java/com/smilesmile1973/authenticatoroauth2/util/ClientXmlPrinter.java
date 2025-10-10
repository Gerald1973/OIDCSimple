package com.smilesmile1973.authenticatoroauth2.util;

import java.util.List;

import com.smilesmile1973.authenticatoroauth2.model.ClientXml;
import com.smilesmile1973.authenticatoroauth2.model.ClientsXml;

/**
 * Utility class for printing ClientXml objects in formatted Markdown tables.
 */
public final class ClientXmlPrinter {

    private static final String MASKED_SECRET = "*****";

    /**
     * Private constructor to prevent instantiation.
     */
    private ClientXmlPrinter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Print a single ClientXml object as a formatted Markdown table.
     *
     * @param client the ClientXml object to print
     */
    public static void printAsTable(ClientXml client) {
        if (client == null) {
            System.out.println("Client is null - cannot print.");
            return;
        }

        System.out.println();
        System.out.println("### Client Configuration");
        System.out.println();
        System.out.println("| Property | Value |");
        System.out.println("| :--- | :--- |");
        System.out.printf("| ID | %s |%n", escapeMarkdown(client.getId()));
        System.out.printf("| Client ID | %s |%n", escapeMarkdown(client.getClientId()));
        System.out.printf("| Client Secret | %s |%n", maskSecret(client.getClientSecret()));
        System.out.printf("| Client Name | %s |%n", escapeMarkdown(client.getClientName()));
        System.out.printf("| Authentication Methods | %s |%n", escapeMarkdown(client.getAuthenticationMethods()));
        System.out.printf("| Grant Types | %s |%n", escapeMarkdown(client.getGrantTypes()));
        System.out.printf("| Redirect URIs | %s |%n", escapeMarkdown(client.getRedirectUris()));
        System.out.printf("| Scopes | %s |%n", escapeMarkdown(client.getScopes()));
        System.out.printf("| Access Token Duration (s) | %s |%n",
                client.getAccessTokenDuration() != null ? client.getAccessTokenDuration() : "null");
        System.out.printf("| Refresh Token Duration (s) | %s |%n",
                client.getRefreshTokenDuration() != null ? client.getRefreshTokenDuration() : "null");
        System.out.println();
    }

    /**
     * Print all clients from a ClientsXml object as individual detailed tables.
     *
     * @param clientsXml the ClientsXml object containing multiple clients
     */
    public static void printAllClientsAsTables(ClientsXml clientsXml) {
        if (clientsXml == null || clientsXml.getClients() == null || clientsXml.getClients().isEmpty()) {
            System.out.println("No clients to display.");
            return;
        }

        List<ClientXml> clients = clientsXml.getClients();

        System.out.println();
        System.out.println("## OAuth2 Clients Configuration");
        System.out.println();
        System.out.printf("**Total Clients Loaded:** %d%n", clients.size());
        System.out.println();

        for (int i = 0; i < clients.size(); i++) {
            System.out.printf("#### Client %d/%d%n", i + 1, clients.size());
            printAsTable(clients.get(i));
        }
    }

    /**
     * Print all clients as a compact summary table.
     *
     * @param clientsXml the ClientsXml object containing multiple clients
     */
    public static void printAllClientsAsSummaryTable(ClientsXml clientsXml) {
        if (clientsXml == null || clientsXml.getClients() == null || clientsXml.getClients().isEmpty()) {
            System.out.println("No clients to display.");
            return;
        }

        List<ClientXml> clients = clientsXml.getClients();

        System.out.println();
        System.out.println("## OAuth2 Clients Summary");
        System.out.println();
        System.out.printf("**Total Clients:** %d%n", clients.size());
        System.out.println();
        System.out.println("| Client ID | Client Name | Access Token (s) | Refresh Token (s) |");
        System.out.println("| :--- | :--- | ---: | ---: |");

        for (ClientXml client : clients) {
            System.out.printf("| %s | %s | %s | %s |%n",
                    escapeMarkdown(client.getClientId()),
                    escapeMarkdown(client.getClientName()),
                    client.getAccessTokenDuration() != null ? client.getAccessTokenDuration() : "null",
                    client.getRefreshTokenDuration() != null ? client.getRefreshTokenDuration() : "null");
        }

        System.out.println();
    }

    /**
     * Mask a client secret for security purposes.
     *
     * @param secret the secret to mask
     * @return the masked secret
     */
    private static String maskSecret(String secret) {
        if (secret == null || secret.isEmpty()) {
            return "null";
        }
        return MASKED_SECRET;
    }

    /**
     * Escape special Markdown characters and handle null values.
     *
     * @param value the value to escape
     * @return the escaped value
     */
    private static String escapeMarkdown(String value) {
        if (value == null || value.isEmpty()) {
            return "null";
        }

        // Escape pipe characters which could break Markdown tables
        return value.replace("|", "\\|")
                .replace("\n", " ")
                .replace("\r", "");
    }
}
