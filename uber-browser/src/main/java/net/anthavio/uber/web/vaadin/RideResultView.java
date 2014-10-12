package net.anthavio.uber.web.vaadin;

import java.util.List;

import net.anthavio.uber.client.model.UberPriceEstimates;
import net.anthavio.uber.client.model.UberPriceEstimates.PriceEstimate;
import net.anthavio.uber.client.model.UberProducts;
import net.anthavio.uber.client.model.UberProducts.Product;
import net.anthavio.uber.client.model.UberTimeEstimates;
import net.anthavio.uber.client.model.UberTimeEstimates.TimeEstimate;

import org.vaadin.spring.events.EventBus;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author martin.vanek
 *
 */
public class RideResultView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private TextField pickup = new TextField("Pick Up");
	private TextField dropoff = new TextField("Drop Off");

	private Table products = new Table("Products");
	private Table times = new Table("Times");
	private Table prices = new Table("Prices");

	private EventBus eventBus;

	RideResultView(EventBus eventBus) {
		this.eventBus = eventBus;

		products.addContainerProperty("Capacity", Integer.class, null);
		products.addContainerProperty("Display_name", String.class, null);
		products.addContainerProperty("Description", String.class, null);
		products.setPageLength(1);

		products.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				ProductDetailPopover popover = new ProductDetailPopover();

				// Show it relative to the navigation bar of
				// the current NavigationView.
				//popover.showRelativeTo(view.getNavigationBar());
				popover.showRelativeTo(products);
			}
		});
		addComponent(products);

		times.addContainerProperty("ETA", Integer.class, null);
		times.addContainerProperty("Display_name", String.class, null);
		times.setPageLength(1);
		addComponent(times);

		prices.addContainerProperty("Currency", String.class, null);
		prices.addContainerProperty("Low", Integer.class, null);
		prices.addContainerProperty("High", Integer.class, null);
		prices.addContainerProperty("Estimate", String.class, null);
		prices.addContainerProperty("Surge", Float.class, null);
		prices.addContainerProperty("Display_name", String.class, null);
		prices.setPageLength(1);
		addComponent(prices);

		setSizeFull();
	}

	public void setProducts(UberProducts products) {
		this.products.removeAllItems();
		List<Product> list = products.getProducts();
		for (Product item : list) {
			Object[] cells = new Object[] { item.getCapacity(), item.getDisplay_name(), item.getDescription() };
			this.products.addItem(cells, item.getProduct_id());
		}
		this.products.setPageLength(list.size());
	}

	public void setTimes(UberTimeEstimates estimates) {
		this.times.removeAllItems();
		List<TimeEstimate> list = estimates.getTimes();
		for (TimeEstimate item : list) {
			Object[] cells = new Object[] { item.getEstimate(), item.getDisplay_name() };
			this.times.addItem(cells, item.getProduct_id());
		}

		this.times.setPageLength(list.size());
	}

	public void setPrices(UberPriceEstimates estimates) {
		this.prices.removeAllItems();
		List<PriceEstimate> list = estimates.getPrices();
		for (PriceEstimate item : list) {
			Object[] cells = new Object[] { item.getCurrency_code(), item.getLow_estimate(), item.getHigh_estimate(),
					item.getEstimate(), item.getSurge_multiplier(), item.getDisplay_name() };
			this.prices.addItem(cells, item.getProduct_id());
		}

		this.prices.setPageLength(list.size());
	}

	class ProductDetailPopover extends Popover {

		private static final long serialVersionUID = 1L;

		public ProductDetailPopover() {
			setWidth("350px");
			setHeight("65%");

			// Have some details to display
			VerticalLayout layout = new VerticalLayout();

			NavigationView c = new NavigationView(layout);
			c.setCaption("ProductDetailPopover");
			setContent(c);
		}
	}
}
