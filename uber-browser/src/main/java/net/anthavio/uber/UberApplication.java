package net.anthavio.uber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot launcher
 * 
 * https://github.com/peholmst/vaadin4spring/tree/master/spring-boot-sample
 * 
 * https://github.com/vaadin/parking-demo
 * 
 * @author martin.vanek
 *
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan
public class UberApplication extends SpringBootServletInitializer {

	@Autowired
	private UberProperties uber;

	@Bean
	public UberSettings UberSettings() {
		UberSettings settings = new UberSettings(uber.getClient_id(), uber.getSecret(), uber.getRedirect_url());
		return settings;
	}

	@Bean
	public UberClient UberClient() {
		return new UberClient(UberSettings());
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(UberApplication.class/*, MvcSpringConfig.class*/);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(UberApplication.class, args);
	}

}
/*
//@TouchKitUI
//@Theme("sample")
@Title("Touch Root UI")
class TouchRootUI extends UI {

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		setContent(new CssLayout(new Label("Hello! I'm TouchRootUI!"), new Link("Go to other UI", new ExternalResource(
				"anotherUI"))));
	}
}

@VaadinUI
//@Theme("sample")
@Title("Root UI")
class RootUI extends UI {

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		setContent(new CssLayout(new Label("Hello! I'm the root UI!"), new Link("Go to other UI", new ExternalResource(
				"anotherUI"))));
	}
}

@VaadinUI(path = "/anotherUI")
//@Widgetset("org.vaadin.spring.boot.sample.AppWidgetSet")
@Title("Another UI")
class AnotherUI extends UI {

	@Autowired
	SpringViewProvider viewProvider;
	@Autowired
	ErrorView errorView;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		Navigator navigator = new Navigator(this, this);
		navigator.setErrorView(errorView);
		navigator.addProvider(viewProvider);
		setNavigator(navigator);
	}
}

@VaadinView(name = "")
@UIScope
class MyDefaultView extends VerticalLayout implements View {

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		addComponent(new Label(String.format("%s: It's %s and I was just entered!", getClass().getSimpleName(), new Date())));

		//Chart chart = new Chart();
		//chart.getConfiguration().addSeries(new ListSeries(1, 2, 3));

		//addComponent(chart);
	}
}

@VaadinView(name = "myView", ui = AnotherUI.class)
@UIScope
class MyView extends VerticalLayout implements View {

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		addComponent(new Label(String.format("%s: It's %s and I was just entered!", getClass().getSimpleName(), new Date())));
	}
}

@VaadinView(name = "hello/world", ui = AnotherUI.class)
@Scope("prototype")
class MyViewWithCustomName extends VerticalLayout implements View {

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		addComponent(new Label(String.format("%s: It's %s and I was just entered!", getClass().getSimpleName(), new Date())));
	}
}

@VaadinComponent
@UIScope
class ErrorView extends VerticalLayout implements View {

	private Label message;

	ErrorView() {
		setMargin(true);
		addComponent(message = new Label());
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		message.setValue(String.format("No such view: %s", event.getViewName()));
	}
}
*/