package net.anthavio.uber.web.mvc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.anthavio.httl.auth.OAuthTokenResponse;
import net.anthavio.uber.client.UberClient;
import net.anthavio.uber.client.UberToken;
import net.anthavio.uber.client.UberToken.TokenType;
import net.anthavio.uber.web.SessionData;

import org.apache.commons.lang.UnhandledException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

	@Autowired
	private SessionData sessionData;

	@Autowired
	private UberClient client;

	@RequestMapping(value = "request", method = RequestMethod.GET)
	public void oauthRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String loginUrl = client.getOauth2().getAuthorizationUrl("profile history", "state");
		//redirect user to login page
		response.sendRedirect(loginUrl);
	}

	@RequestMapping(value = "callback", params = "error")
	public String oauthCallbackError(@RequestParam(value = "error") String error,
			@RequestParam(value = "error_description", required = false) String error_description) {

		//When user clicks Deny -> error=access_denied
		System.out.println("Authentication failed " + error + " " + error_description);

		return "redirect:/vui/#!" + "error";//SettingsView.NAME;
	}

	@RequestMapping(value = "callback", params = "code")
	public String oauthCallback(@RequestParam(value = "code") String code) {

		OAuthTokenResponse tokenResponse = client.getOauth2().access(code).get();
		sessionData.setBearerToken(new UberToken(TokenType.BEARER, tokenResponse.getAccess_token()));
		System.out.println(tokenResponse);

		return "redirect:/vui/#!" + "success";//SettingsView.NAME;
	}

	private URL getCallbackUrl(javax.servlet.http.HttpServletRequest request) {
		try {
			URL requestUrl = new URL(request.getRequestURL().toString());
			URL oauthCallbackUrl = new URL(requestUrl.getProtocol(), requestUrl.getHost(), requestUrl.getPort(),
					request.getContextPath() + "/oauth/callback");
			return oauthCallbackUrl;
		} catch (MalformedURLException mux) {
			throw new UnhandledException(mux);
		}
	}
}
