package net.anthavio.uber.web.vaadin;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import net.anthavio.uber.web.SessionData;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.navigator.SpringViewProvider;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import fi.jasoft.qrcode.QRCode;

/**
 * http://morevaadin.com/content/easy-theming-with-valo/
 * https://github.com/vaadin/vaadin/tree/master/uitest/src/com/vaadin/tests/themes/valo
 * 
 * @author martin.vanek
 *
 */
@VaadinUI(path = "/desktop")
//@PreserveOnRefresh
@Theme("uber-valo")
@Title("Uber Desktop Browser")
@Widgetset("net.anthavio.uber.widgetset.UberDesktopWidgetset")
public class UberDesktopUI extends UI implements ViewChangeListener {

	private static final long serialVersionUID = 1L;

	@Autowired
	SpringViewProvider viewProvider;

	@Autowired
	SessionData session;

	@Override
	protected void init(VaadinRequest request) {
		if (request.getParameter("mobile") == null && !getPage().getWebBrowser().isTouchDevice()) {
			//showNonMobileNotification();
		}
		Navigator navigator = new Navigator(this, this);
		navigator.addProvider(viewProvider);
		//navigator.setErrorProvider(new ErrorViewProvider());
		navigator.setErrorView(viewProvider.getView("")); //send to welcome
		setNavigator(navigator);

		navigator.addViewChangeListener(this);

		setErrorHandler(new CustomErrorHandler());
		/*
		Button btnLogin = new Button("Sign In");
		btnLogin.addClickListener(event -> System.out.println("Prdel " + event.getButton()));
		//btnLogin.setSizeFull();
		setContent(btnLogin);
		*/
	}

	@Override
	protected void refresh(VaadinRequest request) {
		//Executed when F5 and @PreserveOnRefresh
		super.refresh(request);
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

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		if (event.getNewView() instanceof ProtectedView) {
			if (session.getBearerToken() == null) {
				//Notification.show(" To continue, you must be logged into Uber");
				getUI().getNavigator().navigateTo("");
				return false;
			}
		}

		return true; //allow others
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
	}

	class CustomErrorHandler implements ErrorHandler {

		private static final long serialVersionUID = 1L;

		@Override
		public void error(com.vaadin.server.ErrorEvent event) {
			event.getThrowable().printStackTrace();
		}

	}

}
