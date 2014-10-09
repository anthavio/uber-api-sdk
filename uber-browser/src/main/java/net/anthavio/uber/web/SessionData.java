package net.anthavio.uber.web;

import java.io.Serializable;

import net.anthavio.uber.client.UberToken;
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

	private UberToken bearerToken;

	private UberUserProfile userProfile;

	private Location location;

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

	public UberToken getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(UberToken bearerToken) {
		this.bearerToken = bearerToken;
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
