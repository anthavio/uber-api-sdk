package net.anthavio.uber.web.vaadin;

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
	UberService service;

	public WelcomeView() {
		VerticalLayout layout = new VerticalLayout();
		Button btnLogin = new Button("Sign In");

		btnLogin.addClickListener(event -> {
			getUI().getPage().setLocation(service.getAuthorizationUrl());
		});

		layout.addComponent(new Label("You must be signed in to Uber"));
		layout.addComponent(btnLogin);

		layout.setSizeFull();

		setContent(layout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (UberTouchKitUI.getUberUI().isLoggedIn()) {
			getUI().getNavigator().navigateTo("profile");
		}
	}

}
