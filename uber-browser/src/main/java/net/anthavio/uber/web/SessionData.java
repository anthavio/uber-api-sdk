package net.anthavio.uber.web;

import java.io.Serializable;

import net.anthavio.uber.client.UberToken;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * 
 * @author martin.vanek
 *
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionData implements Serializable {

	private static final long serialVersionUID = 1L;

	private UberToken bearerToken;

	public UberToken getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(UberToken bearerToken) {
		this.bearerToken = bearerToken;
	}

}
