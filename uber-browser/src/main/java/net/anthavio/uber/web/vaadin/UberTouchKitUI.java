package net.anthavio.uber.web.vaadin;

import org.vaadin.spring.touchkit.TouchKitUI;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@TouchKitUI(path = "/mobile")
@Theme("touchkit")
@Title("Uber Mobile Browser")
@Widgetset("net.anthavio.uber.widgetset.UberTouchKitWidgetset")
public class UberTouchKitUI extends UI {

	private static final long serialVersionUID = 1L;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		VerticalLayout layout = new VerticalLayout();
		Label label = new Label("Hello! I'm the Uber Mobile UI");
		layout.addComponent(label);
		layout.addComponent(new Button("Push me!"));
		Geolocator.detect(new PositionCallback() {

			@Override
			public void onSuccess(Position position) {
				System.out.println("Position is " + position);

			}

			@Override
			public void onFailure(int errorCode) {
				System.out.println("Position failed " + errorCode);

			}
		});
		setContent(layout);
	}
}
