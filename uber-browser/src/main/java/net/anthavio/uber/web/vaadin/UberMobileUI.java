package net.anthavio.uber.web.vaadin;

import org.vaadin.spring.touchkit.TouchKitUI;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@TouchKitUI(path = "/xui")
@Title("Uber Mobile Browser")
public class UberMobileUI extends UI {

	private static final long serialVersionUID = 1L;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		setContent(new Label("Hello! I'm the Uber Mobile UI"));
	}
}
