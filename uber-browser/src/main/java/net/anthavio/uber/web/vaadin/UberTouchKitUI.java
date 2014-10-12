package net.anthavio.uber.web.vaadin;

import java.io.File;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.anthavio.httl.auth.OAuthTokenResponse;
import net.anthavio.uber.client.UberClient;
import net.anthavio.uber.client.model.UberPriceEstimates;
import net.anthavio.uber.client.model.UberProducts;
import net.anthavio.uber.client.model.UberTimeEstimates;
import net.anthavio.uber.client.model.UberUserProfile;
import net.anthavio.uber.web.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.touchkit.TouchKitUI;

import com.vaadin.addon.touchkit.extensions.LocalStorage;
import com.vaadin.addon.touchkit.extensions.LocalStorageCallback;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import fi.jasoft.qrcode.QRCode;

@TouchKitUI(path = "/mobile")
@Theme("touchkit")
@Title("Uber Mobile Browser")
@Widgetset("net.anthavio.uber.widgetset.UberTouchKitWidgetset")
public class UberTouchKitUI extends UI {

	private static final Logger log = LoggerFactory.getLogger(UberTouchKitUI.class.getName());

	private static final long serialVersionUID = 1L;

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";

	@Autowired
	private SessionData session;

	@Autowired
	private UberClient uber;

	@Autowired
	EventBus eventBus;

	ProfileView profileView;
	RideDefineView rideView;
	RideResultView resultsView;
	UberTabBarView tabBarView;

	/**
	 * https://vaadin.com/book/-/page/mobile.components.html
	 */
	@Override
	protected void init(VaadinRequest request) {

		if (request.getParameter("mobile") == null && !getPage().getWebBrowser().isTouchDevice()) {
			//showNonMobileNotification();
		}

		//NavigationManager manager = new NavigationManager(new RideDefineView());
		//setContent(manager);

		if (session.getUserProfile() == null) {

			restoreSessionFromLocalContent();

			if (session.getTokenResponse() == null) {

			} else {
				UberUserProfile profile = uber.api().me(session.getTokenResponse().getAccess_token());
				session.setUserProfile(profile);
				if (request.getParameter("oauth_done") != null) {
					//Copy values into HTML5 Local Store
					storeTokenResponse(session.getTokenResponse());
				}
			}

			if (request.getParameter("oauth_error") != null) {
				Notification.show("OAuth says NO because: " + request.getParameter("oauth_error"));
				Button button = new Button("Try again");
				button.addClickListener(event -> sendToUberLogin());
				setContent(button);
				return;
			}
		}

		if (isLoggedIn()) {
			profileView = new ProfileView();
			rideView = new RideDefineView();
			resultsView = new RideResultView(eventBus);
			tabBarView = new UberTabBarView();
			setContent(tabBarView);
			setProfile(session.getUserProfile());

		} else if (request.getParameter("reinit") == null) {
			getPage().setLocation("?reinit");

		} else {
			Button button = new Button("Login");
			button.addClickListener(event -> sendToUberLogin());
			setContent(button);
			Notification.show("You need to be logged into Uber");
		}
		log.info("init DONE!" + request.getParameterMap());
	}

	/**
	 * redirect to Uber OAuth page
	 */
	public void sendToUberLogin() {
		getPage().setLocation(uber.auth().getAuthorizationUrl("profile history_lite history"));
	}

	/**
	 * Mainly for Views to get typed UberTouchKitUI for business methods
	 */
	public static UberTouchKitUI getUberUI() {
		return (UberTouchKitUI) UI.getCurrent();
	}

	public void setProfile(UberUserProfile profile) {
		tabBarView.setEnabled(true);
		//tabBarView.getProfileTab().setEnabled(true);
		//tabBarView.getRideTab().setEnabled(true);
		profileView.setProfile(profile);
	}

	public void doLogout() {
		tabBarView.setEnabled(false);

		LocalStorage storage = LocalStorage.get();
		storage.put("expires_in", null);
		storage.put("access_token", null);
		storage.put("refresh_token", null);

		getUI().getSession().close();
		getPage().setLocation("?logout");
	}

	public boolean isNotLoggedIn() {
		return session.getUserProfile() == null;
	}

	public boolean isLoggedIn() {
		return session.getUserProfile() != null;
	}

	public void rideTo(double startLat, double startLon, double endLat, double endLon) {
		Tab resultsTab = tabBarView.getResultsTab();
		resultsTab.setEnabled(true);
		tabBarView.setSelectedTab(resultsTab);
		UberProducts products = uber.api().products(session.getBearerToken(), startLat, startLon);
		resultsView.setProducts(products);
		UberTimeEstimates times = uber.api().time(session.getBearerToken(), startLat, startLon, null, null);
		resultsView.setTimes(times);
		UberPriceEstimates prices = uber.api().price(session.getBearerToken(), startLat, startLon, endLat, endLon);
		resultsView.setPrices(prices);
	}

	/**
	 * Because it is asynchronous. It will no happen in time of first UI init
	 */
	private void restoreSessionFromLocalContent() {
		LocalGet("expires_in", new OnValueCallback() {

			@Override
			public void onValue(String expires_in) {
				Date expire = parseDate(expires_in, DATE_FORMAT);
				if (expire.after(new Date())) {
					// access_token is still fresh 
					LocalGet("access_token", access_token -> {
						//Copy values into Session
							LocalGet("refresh_token", refresh_token -> {
								OAuthTokenResponse tokenResponse = new OAuthTokenResponse(access_token);
								tokenResponse.setRefresh_token(refresh_token);
								tokenResponse.setExpires_in((int) (expire.getTime() - System.currentTimeMillis()) / 1000);
								session.setTokenResponse(tokenResponse);
							});

						});
				} else {
					// refresh expired access_token
					LocalGet("refresh_token", refresh_token -> {
						OAuthTokenResponse tokenResponse = uber.auth().refresh(refresh_token).get();
						session.setTokenResponse(tokenResponse);
						//Copy values into HTML5 Local Store
							storeTokenResponse(tokenResponse);
						});

				}
			}
		});
	}

	/*
		private String getLocalStoreValue(String key) {
			VaadinRequest request = VaadinService.getCurrentRequest();
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					return cookie.getValue();
				}
			}
			return null;
		}
	*/
	/**
	 * Store OAuth TokenResponse into HTML5 local store 
	 */
	private void storeTokenResponse(OAuthTokenResponse tokenResponse) {
		LocalStorage storage = LocalStorage.get();
		storage.put("access_token", tokenResponse.getAccess_token());
		storage.put("refresh_token", tokenResponse.getRefresh_token());
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(Calendar.SECOND, tokenResponse.getExpires_in());
		String expires_in = new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime());
		storage.put("expires_in", expires_in);
	}

	@Override
	protected void refresh(VaadinRequest request) {
		//Executed when F5 and @PreserveOnRefresh
		super.refresh(request);
	}

	class UberTabBarView extends TabBarView {

		private static final long serialVersionUID = 1L;

		Tab profileTab;
		Tab rideTab;
		Tab resultsTab;

		public UberTabBarView() {
			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

			profileTab = addTab(profileView, "Profile", new FileResource(new File(basepath + "/VAADIN/man.png")));

			rideTab = addTab(rideView, "Search", new FileResource(new File(basepath + "/VAADIN/pickup.png")));

			resultsTab = addTab(resultsView, "Result");
			resultsTab.setEnabled(false);
		}

		public Tab getRideTab() {
			return rideTab;
		}

		public Tab getProfileTab() {
			return profileTab;
		}

		public Tab getResultsTab() {
			return resultsTab;
		}

	}

	private static Date parseDate(String value, String dateFormat) {
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException("Wrong date value: " + value);
		}
		try {
			return new SimpleDateFormat(dateFormat).parse(value);
		} catch (ParseException px) {
			throw new IllegalArgumentException("Wrong date value: " + value, px);
		}

	}

	class CustomErrorHandler implements ErrorHandler {

		private static final long serialVersionUID = 1L;

		@Override
		public void error(com.vaadin.server.ErrorEvent event) {
			event.getThrowable().printStackTrace();
		}

	}

	@FunctionalInterface
	interface OnValueCallback {

		public void onValue(String value);
	}

	static void LocalGet(String key, OnValueCallback callback) {
		LocalStorage.get().get(key, new LocalStorageCallback() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSuccess(String value) {
				log.debug("LocalStorage get key: " + key + " onSuccess: " + value);
				if (value != null && !value.equals("null")) {
					callback.onValue(value);
				}
			}

			@Override
			public void onFailure(FailureEvent error) {
				log.warn("LocalStorage get key: " + key + " onFailure: " + error);
			}
		});
	}

	private void showNonMobileNotification() {
		try {
			URL appUrl = Page.getCurrent().getLocation().toURL();
			String myIp = Inet4Address.getLocalHost().getHostAddress();
			String qrCodeUrl = appUrl.toString().replaceAll("localhost", myIp);

			QRCode qrCode = new QRCode("You appear to be running this demo on a non-mobile device.\n"
					+ "Parking is intended for mobile devices primarily. "
					+ "Please read the QR code on your touch device to access the demo.", qrCodeUrl);
			qrCode.setWidth("150px");
			qrCode.setHeight("150px");

			//CssLayout qrCodeLayout = new CssLayout(qrCode);
			//qrCodeLayout.setSizeFull();

			Window window = new Window("Mobile application", qrCode);
			window.setWidth(500.0f, Unit.PIXELS);
			window.setHeight(200.0f, Unit.PIXELS);
			//window.addStyleName("qr-code");
			window.setModal(true);
			window.setResizable(false);
			window.setDraggable(false);
			addWindow(window);
			window.center();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
