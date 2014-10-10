package net.anthavio.uber.web.vaadin;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author martin.vanek
 *
 */
public class RideDefineView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private GoogleMap googleMap;

	private GoogleMapMarker markerGroundZero = new GoogleMapMarker("You", null, false, "/uber/VAADIN/here.png");

	private GoogleMapMarker markerPickUp = new GoogleMapMarker("Pick Up", null, true, "/uber/VAADIN/pickup.png");

	private GoogleMapMarker markerDropOff = new GoogleMapMarker("Drop Off", null, true, "/uber/VAADIN/dropoff.png");

	private boolean dropOff;

	public RideDefineView() {
		HorizontalButtonGroup buttonGroup = new HorizontalButtonGroup();
		buttonGroup.setWidth("100%");
		Button buttonPickUp = new Button("Pick Up");
		buttonPickUp.addClickListener(event -> {
			dropOff = false;
		});
		buttonPickUp.setWidth("47%");
		Button buttonDropOff = new Button("Drop Off");
		buttonDropOff.addClickListener(event -> {
			dropOff = true;
		});
		buttonDropOff.setWidth("47%");
		Button bottonReset = new Button("Reset");
		bottonReset.addClickListener(event -> doReset());

		buttonGroup.addComponent(buttonPickUp);
		buttonGroup.addComponent(buttonDropOff);
		buttonGroup.addComponent(bottonReset);
		addComponent(buttonGroup);

		googleMap = new GoogleMap("Uber Google Map");
		googleMap.setSizeFull();
		//int height = Page.getCurrent().getBrowserWindowHeight() - 150;
		//googleMap.setHeight(height + "px");
		addComponent(googleMap);
		setExpandRatio(googleMap, 1.0f);//this is magic!

		//HorizontalLayout after = new HorizontalLayout();
		addComponent(new Button("After"));

		markerGroundZero.setId(1);
		markerGroundZero.setAnimationEnabled(false);

		markerPickUp.setId(2);
		markerPickUp.setAnimationEnabled(false);

		markerDropOff.setId(3);
		markerDropOff.setAnimationEnabled(false);

		googleMap.addMapClickListener(new UberMapClickListener());

		doReset();
		setSizeFull();
	}

	protected void doReset() {
		if (markerDropOff.getPosition() != null) {
			googleMap.removeMarker(markerDropOff);
			markerDropOff.setPosition(null);
		}
		if (markerPickUp.getPosition() != null) {
			googleMap.removeMarker(markerPickUp);
			markerPickUp.setPosition(null);
		}
		dropOff = true;

		Geolocator.detect(new PositionCallback() {

			public void onSuccess(Position position) {
				LatLon location = new LatLon(position.getLatitude(), position.getLongitude());

				markerGroundZero.setPosition(location);
				if (googleMap.hasMarker(markerGroundZero) == false) {
					googleMap.addMarker(markerGroundZero);
				}

				googleMap.setCenter(location);
				googleMap.setZoom(18);
			}

			public void onFailure(int errorCode) {
				Notification.show("Cannot obtain your location: " + errorCode);
			}
		});
	}

	class UberMapClickListener implements MapClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void mapClicked(LatLon position) {
			GoogleMapMarker marker;
			if (dropOff) {
				marker = markerDropOff;
			} else {
				marker = markerPickUp;
			}

			marker.setPosition(position);
			if (googleMap.hasMarker(marker) == false) {
				googleMap.addMarker(marker);
			}

			googleMap.setCenter(position);

		}
	}

}
