package com.example.indicab.data.cache

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryCache<K, V> @Inject constructor() : Cache<K, V> {
    private val cache = HashMap<K, V>()

    override fun get(key: K): V? = cache[key]

    override fun put(key: K, value: V) {
        cache[key] = value
    }

    override fun remove(key: K) {
        cache.remove(key)
    }

    override fun clear() {
        cache.clear()
    }
}
