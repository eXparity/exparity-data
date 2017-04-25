/*
 * 
 */

package org.exparity.io.internet;

import java.util.concurrent.TimeUnit;

/**
 * Factory for obtaining instances of {@link Internet}
 * 
 * @author Stewart Bissett
 */
public abstract class Internets {

	/**
	 * Obtain an instance of a {@link Internet} which is connected to the <i>real</i> internet
	 */
	public static Internet newInstance() {
		return new InternetPhysicalImpl();
	}

	/**
	 * Obtain an instance of a {@link Internet} which is connected to the <i>real</i> internet
	 */
	public static Internet newInstance(final int connTimeout, final TimeUnit connTimeUnit, final int readTimeout, final TimeUnit readTimeUnit) {
		return new InternetPhysicalImpl(connTimeout, connTimeUnit, readTimeout, readTimeUnit);
	}

	/**
	 * Obtain an instance of a {@link Internet} which is connected to the <i>real</i> internet but is read through a read through cache and uses the local cached version if found
	 */
	public static Internet newCachedInstance(final String cacheDir) {
		return new InternetWithCacheImpl(new InternetPhysicalImpl(), cacheDir);
	}

	/**
	 * Obtain an instance of a {@link Internet} which is connected to the <i>real</i> internet but is read through a read through cache and uses the local cached version if found
	 */
	public static Internet newCachedInstance(final String cacheDir, final int connTimeout, final TimeUnit connTimeUnit, final int readTimeout, final TimeUnit readTimeUnit) {
		return new InternetWithCacheImpl(new InternetPhysicalImpl(connTimeout, connTimeUnit, readTimeout, readTimeUnit), cacheDir);
	}

}
