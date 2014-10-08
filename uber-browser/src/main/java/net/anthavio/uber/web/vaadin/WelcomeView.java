package net.anthavio.uber.web.vaadin;

import net.anthavio.uber.client.UberClient;
import net.anthavio.uber.web.SessionData;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author martin.vanek
 *
 */
@VaadinView(name = "")
@UIScope
public class WelcomeView extends Panel implements View {

	private static final long serialVersionUID = 1L;

	@Autowired
	SessionData session;

	@Autowired
	UberClient uber;

	public WelcomeView() {
		VerticalLayout layout = new VerticalLayout();
		Button btnLogin = new Button("Sign In");

		btnLogin.addClickListener(event -> {
			String authorizationUrl = uber.getOauth2().getAuthorizationUrl("profile history_lite history");
			getUI().getPage().setLocation(authorizationUrl);
		});

		layout.addComponent(new Label("You must be signed in to Uber"));
		layout.addComponent(btnLogin);

		layout.setSizeFull();

		setContent(layout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (session.getBearerToken() != null) {
			getUI().getNavigator().navigateTo("profile");
		}
	}

}
