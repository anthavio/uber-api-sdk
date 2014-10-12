package net.anthavio.uber.web.vaadin;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
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

	private enum MarkerMode {
		PICKUP, DROPOFF;
		private GoogleMapMarker marker;

	}

	private static final long serialVersionUID = 1L;

	private GoogleMap googleMap;

	private GoogleMapMarker markerGroundZero = new GoogleMapMarker("You", null, false, "/uber/VAADIN/man.png");

	private GoogleMapMarker markerPickUp = new GoogleMapMarker("Pick Up", null, true, "/uber/VAADIN/pickup2.png");

	private GoogleMapMarker markerDropOff = new GoogleMapMarker("Drop Off", null, true, "/uber/VAADIN/dropoff2.png");

	private MarkerMode mode;

	private Button buttonGo = new Button("Go");

	public RideDefineView() {
		HorizontalButtonGroup buttonGroup = new HorizontalButtonGroup();
		buttonGroup.setWidth("100%");
		Button buttonPickUp = new Button("Pick Up");
		buttonPickUp.addClickListener(event -> {
			switchMarkerMode(MarkerMode.PICKUP);
		});
		buttonPickUp.setWidth("47%");
		Button buttonDropOff = new Button("Drop Off");
		buttonDropOff.addClickListener(event -> {
			switchMarkerMode(MarkerMode.DROPOFF);
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
		addComponent(buttonGo);

		buttonGo.addClickListener(event -> {
			LatLon start = markerPickUp.getPosition() != null ? markerPickUp.getPosition() : markerGroundZero.getPosition();
			LatLon end = markerDropOff.getPosition();
			UberTouchKitUI.getUberUI().rideTo(start.getLat(), start.getLon(), end.getLat(), end.getLon());
		});

		markerGroundZero.setId(1);
		markerGroundZero.setAnimationEnabled(false);

		markerPickUp.setId(2);
		markerPickUp.setAnimationEnabled(false);
		MarkerMode.PICKUP.marker = markerPickUp;

		markerDropOff.setId(3);
		markerDropOff.setAnimationEnabled(false);
		MarkerMode.DROPOFF.marker = markerDropOff;

		googleMap.addMapClickListener(new UberMapClickListener());
		googleMap.addMarkerClickListener(new UberMarkerClickListener());
		doReset();
		setSizeFull();
	}

	private void switchMarkerMode(MarkerMode mode) {
		this.mode = mode;
		if (googleMap.hasMarker(mode.marker)) {
			googleMap.setCenter(mode.marker.getPosition());
		}
	}

	private boolean isReadyToGo() {
		return (markerGroundZero.getPosition() != null || markerPickUp.getPosition() != null)
				&& markerDropOff.getPosition() != null;
	}

	protected void doReset() {
		mode = MarkerMode.DROPOFF;
		buttonGo.setEnabled(false);

		if (googleMap.hasMarker(markerDropOff)) {
			googleMap.removeMarker(markerDropOff);
			markerDropOff.setPosition(null);
		}
		if (googleMap.hasMarker(markerPickUp)) {
			googleMap.removeMarker(markerPickUp);
			markerPickUp.setPosition(null);
		}

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

	class UberMarkerClickListener implements MarkerClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void markerClicked(GoogleMapMarker clickedMarker) {
			if (clickedMarker != markerGroundZero) {
				googleMap.removeMarker(clickedMarker);
				clickedMarker.setPosition(null);
				if (clickedMarker == markerDropOff) {
					mode = MarkerMode.DROPOFF;
				} else {
					mode = MarkerMode.PICKUP;
				}

				buttonGo.setEnabled(isReadyToGo());
			}
		}

	}

	class UberMapClickListener implements MapClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void mapClicked(LatLon position) {

			mode.marker.setPosition(position);

			if (googleMap.hasMarker(mode.marker) == false) {
				googleMap.addMarker(mode.marker);
			}

			googleMap.setCenter(position);//maybe not

			buttonGo.setEnabled(isReadyToGo());

		}
	}

}
