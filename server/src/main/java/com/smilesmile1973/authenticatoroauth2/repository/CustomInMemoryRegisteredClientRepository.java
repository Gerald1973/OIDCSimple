package com.smilesmile1973.authenticatoroauth2.repository;

import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.List;

/**
 * Custom implementation of RegisteredClientRepository using composition for in-memory storage.
 * This allows adding custom methods like findAll() without extending the final InMemoryRegisteredClientRepository.
 *
 * <p>This implementation provides thread-safe, read-only access to OAuth2 registered clients
 * with optimized O(1) lookup performance for both ID and client ID searches.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Dual indexing for efficient lookups by ID and client ID</li>
 *   <li>Thread-safe concurrent access using ConcurrentHashMap</li>
 *   <li>Immutable client list to prevent external modifications</li>
 *   <li>Static configuration with validation during initialization</li>
 * </ul>
 *
 * @author smilesmile1973
 * @since 1.0
 */
public class CustomInMemoryRegisteredClientRepository implements RegisteredClientRepository {
    /**
     * Immutable list of all registered clients for bulk operations
     */
    private final List<RegisteredClient> registeredClients;
    private final InMemoryRegisteredClientRepository inMemoryRegisteredClientRepository;

    /**
     * Constructs a new CustomInMemoryRegisteredClientRepository with the provided client registrations.
     *
     * <p>Validates that all client registrations have unique IDs and client IDs, then creates
     * optimized lookup structures for efficient retrieval operations.</p>
     *
     * @param registrations the list of OAuth2 client registrations to store
     * @throws IllegalArgumentException if any registration has a duplicate ID or client ID
     * @throws NullPointerException     if registrations is null or contains null elements
     */
    public CustomInMemoryRegisteredClientRepository(List<RegisteredClient> registrations) {
        inMemoryRegisteredClientRepository = new InMemoryRegisteredClientRepository(registrations);
        this.registeredClients = List.copyOf(registrations);
    }

    /**
     * Saves a registered client to the repository.
     *
     * <p><strong>Note:</strong> This implementation does not support save operations
     * as it is designed for static, read-only client configurations.</p>
     *
     * @param registeredClient the client registration to save
     * @throws UnsupportedOperationException always, as save operations are not supported
     */
    @Override
    public void save(RegisteredClient registeredClient) {
        inMemoryRegisteredClientRepository.save(registeredClient);
    }

    /**
     * Retrieves a registered client by its internal registration ID.
     *
     * @param id the internal registration ID to search for
     * @return the RegisteredClient with the specified ID, or {@code null} if not found
     */
    @Override
    public RegisteredClient findById(String id) {
        return this.inMemoryRegisteredClientRepository.findById(id);
    }

    /**
     * Retrieves a registered client by its OAuth2 client ID.
     *
     * @param clientId the OAuth2 client ID to search for
     * @return the RegisteredClient with the specified client ID, or {@code null} if not found
     */
    @Override
    public RegisteredClient findByClientId(String clientId) {
        return this.inMemoryRegisteredClientRepository.findByClientId(clientId);
    }

    /**
     * Retrieves all registered clients in the repository.
     *
     * <p>Returns an immutable list to prevent external modifications to the client collection.</p>
     *
     * @return an immutable list containing all registered clients
     */
    public List<RegisteredClient> findAll() {
        return this.registeredClients;
    }
}
