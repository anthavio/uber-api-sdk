package net.anthavio.uber.web.vaadin;

import net.anthavio.uber.client.UberClient;
import net.anthavio.uber.client.UberToken;
import net.anthavio.uber.client.UberToken.TokenType;
import net.anthavio.uber.client.model.UberPriceEstimates;
import net.anthavio.uber.client.model.UberProducts;
import net.anthavio.uber.client.model.UberTimeEstimates;
import net.anthavio.uber.client.model.UberUserActivity;
import net.anthavio.uber.client.model.UberUserProfile;
import net.anthavio.uber.web.SessionData;
import net.anthavio.uber.web.SessionData.Location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author martin.vanek
 *
 */
@Service
public class UberService {

	@Autowired
	private SessionData session;

	@Autowired
	private UberClient uber;

	public boolean isLoggedIn() {
		return session.getUserProfile() != null;
	}

	public void logout() {
		session.setBearerToken(null);
		session.setUserProfile(null);
	}

	public String loginInitiate() {
		return uber.getOauth2().getAuthorizationUrl("profile history_lite history");
	}

	public String loginSuccess(String accessToken) {
		UberUserProfile profile = uber.api().me(accessToken);
		session.setUserProfile(profile);
		session.setBearerToken(new UberToken(TokenType.BEARER, accessToken));
		return "redirect:/vui/mobile#!" + "profile";//SettingsView.NAME;
	}

	public String loginFailed(String reason) {
		return "redirect:/vui/#!" + "";//SettingsView.NAME;
	}

	public UberUserProfile getUserProfile() {
		return session.getUserProfile();
	}

	public void setLocation(double latitude, double longitude) {
		session.setLocation(latitude, longitude);

	}

	public UberUserActivity loadHistory_v1(int offset, int limit) {
		return uber.api().history_v1(session.getBearerToken().getValue(), offset, limit);
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
	 * Estimate how far are cab from me
	 */
	public UberTimeEstimates uberTime() {
		Location location = session.getLocation();
		return uber.api().time(session.getBearerToken(), location.getLatitude(), location.getLongitude(), null, null);
	}
}
