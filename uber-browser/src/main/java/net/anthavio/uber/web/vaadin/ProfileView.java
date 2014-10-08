package net.anthavio.uber.web.vaadin;

import java.util.Date;
import java.util.List;

import net.anthavio.uber.client.UberClient;
import net.anthavio.uber.client.model.UberUserActivity;
import net.anthavio.uber.client.model.UberUserActivity.UberUserActivityItem;
import net.anthavio.uber.client.model.UberUserProfile;
import net.anthavio.uber.web.SessionData;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author martin.vanek
 *
 */
@UIScope
@VaadinView(name = "profile")
public class ProfileView extends VerticalLayout implements ProtectedView {

	private static final long serialVersionUID = 1L;

	@Autowired
	SessionData session;

	@Autowired
	UberClient uber;

	FormLayout form = new FormLayout();
	TextField txtEmail = new TextField("Email");
	TextField txtName = new TextField("Name");
	Button butLogout = new Button("Logout");

	PagedTable tabHistory = new PagedTable("History");

	public ProfileView() {
		setMargin(true);
		VerticalLayout layout = new VerticalLayout();

		form.addComponent(txtName);
		form.addComponent(txtEmail);
		form.addComponent(butLogout);

		butLogout.addClickListener(event -> logout());

		tabHistory.addContainerProperty("Request Time", Date.class, null);
		tabHistory.addContainerProperty("Start Location", String.class, null);
		tabHistory.addContainerProperty("End Location", String.class, null);

		layout.addComponent(form);
		addComponent(layout);
	}

	private void logout() {
		session.setBearerToken(null);
		getUI().getNavigator().navigateTo("");
	}

	public void setProfile(UberUserProfile profile) {
		txtEmail.setReadOnly(false);
		txtEmail.setValue(profile.getEmail());
		txtEmail.setReadOnly(true);

		txtName.setReadOnly(false);
		txtName.setValue(profile.getFirst_name() + " " + profile.getLast_name());
		txtName.setReadOnly(true);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		UberUserProfile profile = uber.api().me(session.getBearerToken().getValue());
		setProfile(profile);

		UberUserActivity history = uber.api().history_v1(session.getBearerToken().getValue(), 0, 10);
		List<UberUserActivityItem> list = history.getHistory();
		//TODO link to trip detail view with all attributes...
		for (int i = 0; i < list.size(); ++i) {
			UberUserActivityItem item = list.get(i);
			Object[] cells = new Object[] { item.getRequest_time(), item.getStart_location().getAddress(),
					item.getEnd_location().getAddress() };
			tabHistory.addItem(cells, i);
		}

	}
}
