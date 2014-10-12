package net.anthavio.uber.web.vaadin;

import net.anthavio.httl.auth.OAuthTokenResponse;
import net.anthavio.uber.client.UberClient;
import net.anthavio.uber.client.model.UberPriceEstimates;
import net.anthavio.uber.client.model.UberProducts;
import net.anthavio.uber.client.model.UberTimeEstimates;
import net.anthavio.uber.client.model.UberUserActivity;
import net.anthavio.uber.web.SessionData;
import net.anthavio.uber.web.SessionData.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author martin.vanek
 *
 */
@Service
public class UberService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SessionData session;

	@Autowired
	private UberClient uber;

	public boolean isLoggedIn() {
		return session.getUserProfile() != null;
	}

	public String getAuthorizationUrl() {
		return uber.auth().getAuthorizationUrl("profile history_lite history");
	}

	public String oauthCodeCallback(String code) {
		OAuthTokenResponse tokenResponse = uber.auth().access(code).get();
		session.setTokenResponse(tokenResponse);

		return "redirect:/vui/mobile?oauth_done";
	}

	public String oauthErrorCallback(String error) {
		return "redirect:/vui/mobile?oauth_error=" + error;
	}

	public void setLocation(double latitude, double longitude) {
		session.setLocation(latitude, longitude);

	}

	public UberUserActivity loadHistory_v1(int offset, int limit) {
		return uber.api().history_v1(session.getTokenResponse().getAccess_token(), offset, limit);
	}

	/**
	 * Gimme cabs around my position
	 */
	public UberProducts uberProducts() {
		Location location = session.getLocation();
		return uber.api().products(session.getBearerToken(), location.getLatitude(), location.getLongitude());
	}

	public UberPriceEstimates uberPrice(double latitude, double longitude) {
		Location location = session.getLocation();
		return uber.api().price(session.getBearerToken(), location.getLatitude(), location.getLongitude(), latitude,
				longitude);
	}

	/**
	 * Estimate how far are cabs from my position
	 */
	public UberTimeEstimates uberTime() {
		Location location = session.getLocation();
		return uber.api().time(session.getBearerToken(), location.getLatitude(), location.getLongitude(), null, null);
	}

}
