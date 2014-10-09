package net.anthavio.uber.web.vaadin;

import net.anthavio.uber.client.model.UberPriceEstimates;
import net.anthavio.uber.client.model.UberTimeEstimates;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author martin.vanek
 *
 */
@UIScope
@VaadinView(name = "products")
public class ProductsView extends VerticalLayout implements ProtectedView {

	private static final long serialVersionUID = 1L;

	@Autowired
	UberService service;

	public ProductsView() {

	}

	@Override
	public void enter(ViewChangeEvent event) {
		Geolocator.detect(new PositionCallback() {
			public void onSuccess(Position position) {
				service.setLocation(position.getLatitude(), position.getLongitude());
				UberPriceEstimates prices = service.uberPrice(position.getLatitude() + 1, position.getLongitude() + 1);
				System.out.println(prices);

				UberTimeEstimates times = service.uberTime();
				System.out.println(times);
				double accuracy = position.getAccuracy();

			}

			public void onFailure(int errorCode) {
				Notification.show("Cannot obtain you location: " + errorCode);
			}
		});
	}
}
