package net.anthavio.uber.web.vaadin;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * 
 * @author vanek
 *
 */
public class DesktopMobileUIProvider extends UIProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		String userAgent = event.getRequest().getHeader("user-agent").toLowerCase();

		if (userAgent.contains("webkit") || userAgent.contains("windows phone 8") || userAgent.contains("windows phone 9")) {
			return UberMobileUI.class;
		} else {
			return UberDesktopUI.class;
		}
	}

}
