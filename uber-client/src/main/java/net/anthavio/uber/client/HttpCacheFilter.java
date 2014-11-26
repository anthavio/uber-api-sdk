package net.anthavio.uber.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.anthavio.cache.CacheBase;
import net.anthavio.cache.CacheEntry;
import net.anthavio.cache.impl.HeapMapCache;
import net.anthavio.httl.HttlCacheKeyProvider;
import net.anthavio.httl.HttlExecutionChain;
import net.anthavio.httl.HttlExecutionFilter;
import net.anthavio.httl.HttlRequest;
import net.anthavio.httl.HttlResponse;
import net.anthavio.httl.HttlSender.Multival;
import net.anthavio.httl.cache.CachedResponse;

/**
 * Uber uses Etag quite a lot so it would be shame not to take advantage of it
 * 
 * @author martin.vanek
 *
 */
public class HttpCacheFilter implements HttlExecutionFilter, Closeable {

	private final boolean localCache;

	private final CacheBase<HttlRequest, CachedResponse> cache;

	private final long evictionSeconds;

	public HttpCacheFilter(String url, int cacheTime, TimeUnit unit) {
		HttlCacheKeyProvider keyProvider = new HttlCacheKeyProvider(url);
		this.cache = new HeapMapCache<HttlRequest, CachedResponse>(keyProvider);
		this.localCache = true;

		this.evictionSeconds = unit.toSeconds(cacheTime);
	}

	public HttpCacheFilter(CacheBase<HttlRequest, CachedResponse> cache, int cacheTime, TimeUnit unit) {

		if (cache == null) {
			throw new IllegalArgumentException("Null cache");
		}
		this.cache = cache;
		this.localCache = false;

		this.evictionSeconds = unit.toSeconds(cacheTime);
	}

	@Override
	public HttlResponse filter(HttlRequest request, HttlExecutionChain chain) throws IOException {
		CacheEntry<CachedResponse> entry = cache.get(request);
		if (entry != null) {
			CachedResponse value = entry.getValue();
			String[] etag = new String[] { "If-None-Match", value.getFirstHeader("Etag") };
			String[] lastModified = { "If-Modified-Since", value.getFirstHeader("Last-Modified") };
			request = new CachingHttlRequest(request, new String[][] { etag, lastModified });
		}
		HttlResponse response = chain.next(request);
		if (response.getHttpStatusCode() == 304) { // HTTP 304 Not Modified
			//lovely hit
			response.close();
			return entry.getValue();
		} else {
			//new version from remote
			String etag = response.getFirstHeader("Etag");
			String lastModified = response.getFirstHeader("Last-Modified");
			if (etag != null || lastModified != null) {
				CachedResponse cachedResponse = new CachedResponse(request, response);
				cache.set(request, cachedResponse, evictionSeconds, TimeUnit.SECONDS);
				return cachedResponse;
			} else {
				//No caching headers in response... (or error maybe)
				return response;
			}
		}

	}

	@Override
	public void close() throws IOException {
		if (localCache) {
			cache.close();
		}
	}
}

class CachingHttlRequest extends HttlRequest {

	private static final long serialVersionUID = 1L;

	public CachingHttlRequest(HttlRequest request, String[][] cachingHeaders) {
		super(request.getSender(), request.getMethod(), request.getUrlPath(), request.getParameters(), visit(
				cachingHeaders, request.getHeaders()), request.getBody(), request.getReadTimeoutMillis());

	}

	private static Multival<String> visit(String[][] cachingHeaders, Multival<String> headers) {
		Multival<String> ret = headers.clone();
		for (String[] header : cachingHeaders) {
			if (header[1] != null) { //skip null values
				ret.set(header[0], header[1]);
			}
		}
		return ret;
	}

}
