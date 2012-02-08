package recng.recommendations.cache;

import recng.recommendations.domain.Product;

/**
 * Cached product data.
 *
 * @author jon
 *
 * @param <K>
 *            The generic type of the keys in this cache
 */
public interface ProductCache<K> {

    /**
     * Gets cached properties for a product. Node that the returned instance may
     * only contain a subset of the available product properties.
     */
    Product getProduct(K productId);

    boolean contains(K productId);

    /**
     * Caches product properties.
     */
    void cacheProduct(K productId, Product product);

    /**
     * Clears the cache.
     */
    void clearCache();

    /**
     * Removed a product from the cache.
     */
    void remove(K productId);

    int size();
}
