package at.reisisoft.jku.ce.adaptivelearning.vaadin.ui.core;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import at.reisisoft.jku.ce.adaptivelearning.ProductData;
import at.reisisoft.jku.ce.adaptivelearning.core.LogHelper;
import at.reisisoft.jku.ce.adaptivelearning.html.HtmlLabel;
import at.reisisoft.jku.ce.adaptivelearning.html.HtmlUtils;
import at.reisisoft.jku.ce.adaptivelearning.vaadin.ui.MainUI;
import at.reisisoft.jku.ce.adaptivelearning.vaadin.ui.QuestionManager;
import at.reisisoft.jku.ce.adaptivelearning.vaadin.ui.topic.accounting.AccountingQuestionManager;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("vaadin")
@PreserveOnRefresh
@Title("Loading page...")
public class VaadinUI extends UI {
	private Navigator navigator;

	public VaadinUI() {
		// Set up the Navigator
		navigator = new Navigator(this, this);

		// Create Welcome Screen
		MainUI mainScreen = new MainUI();
		mainScreen.setMargin(true);
		Button start = new Button("Start", e -> {
			navigator.navigateTo(Views.TEST.toString());
		});
		start.setWidth("40%");
		start.setHeight("40%");
		mainScreen.addComponent(new HtmlLabel(HtmlUtils.center("h1",
				"Welcome to " + productData)));
		mainScreen
		.addComponent(new HtmlLabel(HtmlUtils.center("h2",
				"Click the <b>" + start.getCaption()
				+ "</b> Button to start!")));
		mainScreen.addComponent(start);
		mainScreen.setComponentAlignment(start, Alignment.MIDDLE_CENTER);

		navigator.addView(Views.DEFAULT.toString(), mainScreen);
		// Question view
		// Change this to the questionManager you like
		final QuestionManager manager = new AccountingQuestionManager(
				"Accounting Quiz");
		navigator.addView(Views.TEST.toString(), manager);
		navigator.setErrorView(mainScreen);
		LogHelper.logInfo("Startup completed");
	}

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = VaadinUI.class)
	public static class Servlet extends VaadinServlet {
		@Override
		protected void servletInitialized() throws ServletException {
			super.servletInitialized();
			// Get the question folder as defined in WEB-INF/web.xml
			questionFolderName = getServletConfig().getServletContext()
					.getInitParameter(initKey);
			File file = new File(questionFolderName);
			boolean isWorking = file.exists() && file.isDirectory()
					|| file.mkdirs();
			if (!isWorking) {
				questionFolderName = null;
			}
		}

		private static String questionFolderName = null;
		private final static String initKey = "at.reisisoft.jku.ce.adaptivelearning.questionfolder";

		/**
		 * Gets the question folder name
		 *
		 * @return NULL if not set, or the String is not a valid folder / a
		 *         folder at this location could not be created.
		 */
		public static String getQuestionFolderName() {
			return questionFolderName;
		}
	}

	private static ProductData productData = new ProductData();

	public static ProductData getProductData() {
		return productData;
	}

	public static void setCurrentPageTitle(ViewChangeEvent e) {
		Page.getCurrent().setTitle(
				(e.getViewName().length() == 0 ? Views.DEFAULT.toString() : e
						.getViewName())
						+ " - "
						+ VaadinUI.getProductData().getProduct()
						+ " v"
						+ VaadinUI.getProductData().getVersion());

	}

	@Override
	protected void init(VaadinRequest request) {

	}
}