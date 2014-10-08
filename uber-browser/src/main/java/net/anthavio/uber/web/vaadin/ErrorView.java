package net.anthavio.uber.web.vaadin;

import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * 
 * @author martin.vanek
 *
 */
@UIScope
@VaadinView(name = "error")
public class ErrorView extends Panel implements View {

	private static final long serialVersionUID = 1L;

	@Override
	public void enter(ViewChangeEvent event) {
		System.out.println(event.getNewView());
		setContent(new Label("Sorry for inconvenience"));
	}

}
