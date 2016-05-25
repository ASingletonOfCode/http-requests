package com.budjb.httprequests.delegate

import com.budjb.httprequests.HttpEntity
import com.budjb.httprequests.converter.EntityConverterManager

/**
 * A delegate for DSL-based requests.
 */
class RequestDSLDelegate {
    /**
     * A delegate for DSL-based request entities.
     */
    class EntityDSLDelegate {
        /**
         * Request entity.
         */
        Object entity

        /**
         * Request entity Content-Type.
         */
        String contentType

        /**
         * Character set of the entity.
         */
        String charset
    }

    /**
     * Request headers.
     */
    Map<String, Object> headers = [:]

    /**
     * Query parameters.
     */
    Map<String, Object> queryParameters = [:]

    /**
     * The read timeout of the HTTP connection, in milliseconds. Defaults to 0 (infinity).
     */
    int readTimeout = 0

    /**
     * The connection timeout of the HTTP connection, in milliseconds. Defaults to 0 (infinity).
     */
    int connectionTimeout = 0

    /**
     * Whether SSL certificates will be validated.
     */
    boolean sslValidated = true

    /**
     * Whether the client should automatically follow redirects.
     */
    boolean followRedirects = true

    /**
     * URI of the request.
     */
    String uri

    /**
     * Track whether the closure is inside of an entity closure.
     */
    private boolean inEntity = false

    /**
     * Whether to buffer the response entity in the {@link com.budjb.httprequests.HttpResponse} object so that it can be
     * ready multiple times.
     */
    boolean bufferResponseEntity = true

    /**
     * A list of configured entities.
     */
    private List<HttpEntity> entities = []

    /**
     * Entity converter manager.
     */
    private EntityConverterManager entityConverterManager

    /**
     * The accepted Content-Type of the response.
     */
    String accept

    /**
     * Constructor.
     *
     * @param entityConverterManager
     */
    RequestDSLDelegate(EntityConverterManager entityConverterManager) {
        this.entityConverterManager = entityConverterManager
    }

    /**
     * Loads the request delegate with the given closure.
     *
     * @param closure Closure that will configure the delegate.
     */
    void load(@DelegatesTo(RequestDSLDelegate) Closure closure) {
        closure = closure.clone() as Closure
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    /**
     * Adds an entity to the request.
     *
     * @param entity Pre-built {@link HttpEntity} instance.
     */
    void entity(HttpEntity entity) {
        entities.add(entity)
    }

    /**
     * Adds an entity to the request.
     *
     * @param closure A closure that will configure a request entity.
     */
    void entity(@DelegatesTo(EntityDSLDelegate) Closure closure) {
        inEntity = true
        EntityDSLDelegate delegate = new EntityDSLDelegate()
        closure = closure.clone() as Closure
        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        inEntity = false

        if (delegate.entity != null) {
            entities.add(entityConverterManager.write(delegate.entity, delegate.contentType, delegate.charset))
        }
    }

    /**
     * Returns the list of configured entities.
     *
     * @return The list of configured entities.
     */
    List<HttpEntity> getEntities() {
        return entities
    }

    /**
     * Sets the entity with the given object.
     *
     * @param entity An object that will be converted and set as the entity.
     */
    void setEntity(Object entity) {
        entities.clear()
        entities.add(entityConverterManager.write(entity, null, null))
    }

    /**
     * Sets the entity.
     *
     * @param entity Entity to set.
     */
    void setEntity(HttpEntity entity) {
        entities.clear()
        entities.add(entity)
    }
}
