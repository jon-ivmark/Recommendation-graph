package recng.recommendations.cache;

import recng.cache.Cache;
import recng.recommendations.domain.Product;

/**
 * A class containing cached product data.
 *
 * @author jon
 *
 * @param <K>
 *            The generic type of the keys in this cache
 */
public class ProductCacheImpl<K> implements ProductCache<K> {

    private final Cache<K, Product> cache;

    /**
     * @param cache
     *            The underlying cache.
     */
    public ProductCacheImpl(Cache<K, Product> cache) {
        this.cache = cache;
    }

    public Product getProduct(K productId) {
        return cache.get(productId);
    }

    public void cacheProduct(K productId, Product product) {
        cache.cache(productId, product);
    }

    public void clearCache() {
        cache.clear();
    }

    public void remove(K productId) {
        cache.evict(productId);
    }

    @Override
    public boolean contains(K productId) {
        return cache.contains(productId);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public String toString() {
        return "ProductCacheImpl [cache=" + cache + "]";
    }
}
