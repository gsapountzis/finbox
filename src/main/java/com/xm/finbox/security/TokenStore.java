package com.xm.finbox.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Backing store for tokens.
 *
 * This is required for proper token refresh and expiration. Both can be handled
 * with JWT in a stateless manner but they become a little awkward. Token
 * refresh requires the server to regenerate the token on each access and the
 * client to update its token on each request and token expiration can be
 * handled by simply dropping the token client-side but the server would still
 * consider the token as valid.
 *
 * Ideally this should be implemented with a scalable cluster-scoped memory,
 * cache or database.
 *
 * XXX Can we get rid of this store ?
 *
 * @author sap
 *
 */
@Component
public class TokenStore {

	private static final Logger logger = LoggerFactory.getLogger(TokenStore.class);

	/**
	 * Map for holding the last access time for each token.
	 */
	private Map<String, Date> store = new HashMap<>();

	private Date lastGC = new Date();

	@Autowired
	private TokenUtil tokenUtil;

	@Value("${jwt.expiration}")
	private Long expirationSeconds;

	public synchronized void login(String token) {
		String id = tokenUtil.getIdFromToken(token);
		logger.trace("Token login {}", id);

		Date access = new Date();
		store.put(id, access);

		// Run GC if expiration timeout has passed
		if (isTokenExpired(lastGC)) {
			lastGC = new Date();
			gc();
		}
	}

	public synchronized void logout(String token) {
		String id = tokenUtil.getIdFromToken(token);
		logger.trace("Token logout {}", id);

		store.remove(id);
	}

	public synchronized void refresh(String token) {
		String id = tokenUtil.getIdFromToken(token);
		logger.trace("Token refresh {}", id);

		Date access = new Date();
		store.put(id, access);
	}

	public synchronized boolean isValid(String token) {
		String id = tokenUtil.getIdFromToken(token);
		logger.trace("Token validation {}", id);

		Date access = store.get(id);
		if (access == null) {
			return false;
		}
		return !isTokenExpired(access);
	}

	/**
	 * Garbage collect expired tokens.
	 *
	 * Invocation logic could be factored out.
	 */
	private void gc() {
		logger.trace("Before GC {}", store);

		// values() returns a Collection view, the collection supports element removal,
		// which removes the corresponding mapping from the map.
		store.values().removeIf(this::isTokenExpired);

		logger.trace("After GC {}", store);
	}

	/**
	 * Checks if a token has expired.
	 *
	 * @param access
	 *            the last access time of the time
	 *
	 * @return if the token has expired
	 */
	private boolean isTokenExpired(Date access) {
		Date now = new Date();
		Date expiration = new Date(access.getTime() + expirationSeconds * 1000);
		return expiration.before(now);
	}
}
