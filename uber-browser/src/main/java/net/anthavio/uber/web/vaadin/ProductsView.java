package net.anthavio.uber.web.vaadin;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * https://github.com/tjkaal/GoogleMapsVaadin7
 * 
 * https://github.com/mstahv/v-leaflet/blob/master/src/test/java/org/vaadin/addon/leaflet/demoandtestapp/BasicTest.java
 * 
 * https://github.com/tjkaal/GoogleMapsVaadin7/blob/master/googlemaps-demo/src/main/java/com/vaadin/tapio/googlemaps/demo/DemoUI.java
 * 
 * 
 * https://sites.google.com/site/gmapsdevelopment/
 * 
 * https://vaadin.com/valo#modularity
 * https://github.com/vaadin/vaadin/tree/master/uitest/src/com/vaadin/tests/themes/valo
 * 
 * https://vaadin.com/demo
 * http://demo.vaadin.com/TouchKit
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

	private LMap leafletMap = new LMap();

	private GoogleMap googleMap = new GoogleMap("Uber Google Map");

	public ProductsView() {
		Panel panel = new Panel();
		HorizontalButtonGroup buttonGroup = new HorizontalButtonGroup();
		//group.addStyleName("v-component-group");
		addComponent(buttonGroup);
		Button buttonPickup = new Button("Pick Up");
		buttonGroup.addComponent(buttonPickup);
		Button buttonDropOff = new Button("Drop Off");
		buttonGroup.setWidth("100%");
		//buttonDropOff.setEnabled(false);
		//buttonDropOff.addStyleName("v-pressed");

		buttonGroup.addComponent(buttonDropOff);

		buttonGroup.setHeightUndefined();
		VerticalLayout layout = new VerticalLayout(buttonGroup, googleMap);
		panel.setContent(layout);
		addComponent(panel);
		panel.setSizeFull();
		layout.setSizeFull();
		googleMap.setSizeFull();
		//setSizeFull();
	}

	private LTileLayer buildLayer() {
		LTileLayer mapLayer = new LTileLayer();
		//http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png
		//Map data © <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors
		mapLayer.setUrl("http://{s}.tile.osm.org/{z}/{x}/{y}.png");
		mapLayer.setAttributionString("&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors");
		mapLayer.setDetectRetina(true);
		return mapLayer;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		Geolocator.detect(new PositionCallback() {
			public void onSuccess(Position position) {
				LatLon location = new LatLon(position.getLatitude(), position.getLongitude());
				googleMap.setCenter(location);
				googleMap.setZoom(18);
				GoogleMapMarker marker = new GoogleMapMarker("Me", location, false, "/uber/VAADIN/here.png");
				googleMap.addMarker(marker);
				googleMap.addMapClickListener(new UberMapClickListener());

				//leafletMap.setCenter(position.getLatitude(), position.getLongitude());
				//leafletMap.setCenter(position.getLatitude(), position.getLongitude());
				/*
				service.setLocation(position.getLatitude(), position.getLongitude());
				UberPriceEstimates prices = service.uberPrice(position.getLatitude() + 1, position.getLongitude() + 1);
				System.out.println(prices);

				UberTimeEstimates times = service.uberTime();
				System.out.println(times);
				double accuracy = position.getAccuracy();
				*/
				/*
				LMarker leafletMarker = new LMarker(position.getLatitude(), position.getLongitude());
				leafletMarker.setTitle("Me!");
				leafletMap.addComponent(leafletMarker);
				*/
				System.out.println("Done");
			}

			public void onFailure(int errorCode) {
				Notification.show("Cannot obtain you location: " + errorCode);
			}
		});

	}

	LeafletClickListener listener = new LeafletClickListener() {

		@Override
		public void onClick(LeafletClickEvent event) {
			if (event.getPoint() != null) {
				Notification.show(String.format("Clicked %s @ %.4f,%.4f", event.getConnector().getClass().getSimpleName(),
						event.getPoint().getLat(), event.getPoint().getLon()));

				if (event.getSource() == leafletMap) {
					LMarker leafletMarker = new LMarker(event.getPoint());
					leafletMarker.addClickListener(listener);
					leafletMap.addComponent(leafletMarker);
				}
			} else if (event.getSource() instanceof LMarker) {
				leafletMap.removeComponent((LMarker) event.getSource());
			} else {
				Notification.show(String.format("Clicked %s", event.getConnector().getClass().getSimpleName()));
			}
			/*
			if (delete.getValue() && event.getSource() instanceof AbstractLeafletLayer) {
				leafletMap.removeComponent((Component) event.getConnector());
			}
			*/
		}
	};

	class UberMapClickListener implements MapClickListener {

		private static final long serialVersionUID = 1L;

		GoogleMapMarker marker;

		@Override
		public void mapClicked(LatLon position) {
			if (marker == null) {
				marker = new GoogleMapMarker("Pickup", position, true, "/uber/VAADIN/from.png");
				marker.setAnimationEnabled(false);
				googleMap.addMarker(marker);
			} else {
				marker.setPosition(position);
			}
			googleMap.setCenter(position);

		}
	}
}
