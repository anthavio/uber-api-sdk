package net.anthavio.uber.web.vaadin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.server.VaadinServlet;

/**
 * Servlet for serving static Vaadin content.
 */
public class StaticContentServlet extends VaadinServlet {

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			if (pathInfo.endsWith("/themes/touchkit/styles.css")) {
				response.setContentType("text/css");
				response.setHeader("Cache-Control", "max-age=1000000");
				response.getWriter().print("\n");
				return;
			}
		}
		super.service(request, response);
	}

}
