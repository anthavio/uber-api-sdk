package net.anthavio.uber.web;

import java.io.Serializable;

import net.anthavio.httl.auth.OAuthTokenResponse;
import net.anthavio.uber.client.UberToken;
import net.anthavio.uber.client.UberToken.TokenType;
import net.anthavio.uber.client.model.UberUserProfile;

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

	private OAuthTokenResponse tokenResponse;

	private UberUserProfile userProfile;

	private Location location;

	private UberToken bearerToken;

	public void logout() {
		this.userProfile = null;
		this.tokenResponse = null;
		this.bearerToken = null;
		this.location = null;
	}

	public Location getLocation() {
		if (location == null) {
			throw new IllegalArgumentException("Current Location is not set");
		}
		return location;
	}

	public boolean isLocation() {
		return location != null;
	}

	public void setLocation(double latitude, double longitude) {
		this.location = new Location(latitude, longitude);
	}

	public UberUserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UberUserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public OAuthTokenResponse getTokenResponse() {
		return tokenResponse;
	}

	public void setTokenResponse(OAuthTokenResponse tokenResponse) {
		this.tokenResponse = tokenResponse;
		this.bearerToken = new UberToken(TokenType.BEARER, tokenResponse.getAccess_token());
	}

	public UberToken getBearerToken() {
		return this.bearerToken;
	}

	public static class Location implements Serializable {

		private static final long serialVersionUID = 1L;

		private final double latitude;

		private final double longitude;

		public Location(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}

	}

}
