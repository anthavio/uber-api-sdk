package net.anthavio.uber;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 * @author martin.vanek
 *
 */
@Component
@ConfigurationProperties(prefix = "uber")
public class UberProperties {

	private String client_id;

	private String secret;

	private String redirect_url;

	public String getClient_id() {
		return client_id;
	}

	public String getSecret() {
		return secret;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRedirect_url() {
		return redirect_url;
	}

	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}

}
