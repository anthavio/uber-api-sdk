package net.anthavio.uber.web.vaadin;

import org.vaadin.spring.VaadinUI;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@VaadinUI(path = "/xui")
@Title("Uber Browser")
public class UberVaadinUI extends UI {

	private static final long serialVersionUID = 1L;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		setContent(new Label("Hello! I'm the UberVaadinUI"));
	}
}
