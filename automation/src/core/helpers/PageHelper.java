package core.helpers;

import java.util.ArrayList;

import org.openqa.selenium.Alert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import core.support.logger.TestLog;
import core.support.objects.TestObject;
import core.uiCore.drivers.AbstractDriver;
import core.uiCore.drivers.AbstractDriverJunit;
import core.uiCore.drivers.AbstractDriverTestNG;
import core.uiCore.webElement.EnhancedBy;
import core.uiCore.webElement.EnhancedWebElement;

public class PageHelper {

	/**
	 * maximizes web page
	 */
	public void maximizePage() {
		try {
			AbstractDriver.getWebDriver().manage().window().maximize();
		} catch (Exception e) {
			TestLog.logWarning("Page was not maximized");
			e.printStackTrace();
		}
	}

	/**
	 * sets page size
	 * 
	 * @param x
	 * @param y
	 */
	public void setPageSize(int x, int y) {
		Dimension dimension = new Dimension(x, y);
		AbstractDriver.getWebDriver().manage().window().setSize(dimension);
	}

	/**
	 * reload page
	 */
	public void refreshPage() {
		AbstractDriver.getWebDriver().navigate().refresh();
		Helper.wait.waitForSeconds(1);
	}

	/**
	 * switches frame to frame specified
	 * 
	 * @param frame
	 */
	public void switchIframe(EnhancedBy frame) {
		EnhancedWebElement frameElement = Element.findElements(frame);
		AbstractDriver.getWebDriver().switchTo().defaultContent(); // you are now outside both
		// frames
		AbstractDriver.getWebDriver().switchTo().frame(frameElement.get(0));
	}

	/**
	 * switches to default frame
	 */
	public void switchToDefaultFrame() {
		AbstractDriver.getWebDriver().switchTo().defaultContent();
	}

	/**
	 * dismisses alert by selecting ok or cancel
	 * 
	 * @param alert
	 */
	public String dimissAlert() {
		String alertText = "";
		try {
			Alert alert = AbstractDriver.getWebDriver().switchTo().alert();
			alertText = alert.getText();
			TestLog.ConsoleLog("dismissed alert");
			alert.dismiss();
		} catch (Exception e) {
			e.getMessage();
		}
		return alertText;
	}

	/**
	 * accepts alert by selecting ok or cancel
	 * 
	 * @param alert
	 */
	public String acceptAlert() {
		String alertText = "";
		try {
			Alert alert = AbstractDriver.getWebDriver().switchTo().alert();
			alertText = alert.getText();
			TestLog.ConsoleLog("accepted alert");
			alert.accept();
		} catch (Exception e) {
			e.getMessage();
		}
		return alertText;
	}

	/**
	 * returns alert message value
	 * 
	 * @return
	 */
	public String getAlertValue() {
		String alertText = "";
		try {
			Alert alert = AbstractDriver.getWebDriver().switchTo().alert();
			alertText = alert.getText();
		} catch (Exception e) {
			e.getMessage();
		}
		return alertText;
	}

	/**
	 * return the current window handle
	 * 
	 * @return
	 */
	public String currentWindow() {
		return AbstractDriver.getWebDriver().getWindowHandle();
	}

	/**
	 * switch to the new opened window
	 * 
	 * @param defaultWindow
	 */
	public void switchToNewWindow(String defaultWindow) {
		for (String winHandle : AbstractDriver.getWebDriver().getWindowHandles()) {
			if (!winHandle.equals(defaultWindow))
				AbstractDriver.getWebDriver().switchTo().window(winHandle);
		}
	}

	/**
	 * close the window and return to the defaultWindow
	 * 
	 * @param defaultWindow
	 */
	public void CloseAndReturn(String defaultWindow) {
		AbstractDriver.getWebDriver().close();
		AbstractDriver.getWebDriver().switchTo().window(defaultWindow);
	}

	/**
	 * gets page source
	 */
	public void refreshPageSource() {
		AbstractDriver.getWebDriver().getPageSource();
	}

	/**
	 * verify page title
	 * 
	 * @param appTitle
	 */
	public void verifyTitle(String appTitle) {
		Helper.assertEquals(AbstractDriver.getWebDriver().getTitle(), appTitle);
	}

	/**
	 * switch webdriver use for switching between different drivers
	 * 
	 * @param driver
	 */
	public void switchDriver(WebDriver driver) {

		if (driver.getTitle() != null || !driver.getTitle().isEmpty())
			TestLog.logPass("swtiching to " + driver.getTitle());
		else
			TestLog.logPass("swtiching to " + TestObject.getTestInfo().app);

		if (AbstractDriver.isJunit()) {
			AbstractDriverJunit.setWebDriver(driver);
		} else if (AbstractDriver.isTestNG()) {
			AbstractDriverTestNG.setWebDriver(driver);
		}
	}

	/**
	 * switch to next tab circles back to initial tab if it reaches the last tab
	 */
	public void switchToNextTab() {
		ArrayList<String> tabs = new ArrayList<String>(AbstractDriver.getWebDriver().getWindowHandles());
		int currentIndex = tabs.indexOf(AbstractDriver.getWebDriver().getWindowHandle());

		// circle back to the first tab
		if (currentIndex == (tabs.size() - 1))
			currentIndex = -1;

		try {
			AbstractDriver.getWebDriver().switchTo().window(tabs.get(currentIndex + 1));
		} catch (Exception e) {
			Helper.assertFalse("tab does not exist");
		}
	}

	/**
	 * switch to previous tab circle back to the last tab
	 */
	public void switchToPreviousTab() {
		ArrayList<String> tabs = new ArrayList<String>(AbstractDriver.getWebDriver().getWindowHandles());
		int currentIndex = tabs.indexOf(AbstractDriver.getWebDriver().getWindowHandle());

		// circle back to last index
		if (currentIndex == 0)
			currentIndex = tabs.size();

		try {
			AbstractDriver.getWebDriver().switchTo().window(tabs.get(currentIndex - 1));
		} catch (Exception e) {
			Helper.assertFalse("tab does not exist");
		}
	}

	/**
	 * switch to tab by index
	 */
	public void switchToTab(int index) {
		ArrayList<String> tabs = new ArrayList<String>(AbstractDriver.getWebDriver().getWindowHandles());

		try {
			AbstractDriver.getWebDriver().switchTo().window(tabs.get(index));
		} catch (Exception e) {
			Helper.assertFalse("tab does not exist");
		}
	}

	/**
	 * switch to tab by index
	 */
	public void verifyNumberOfTabs(int tabs) {
		ArrayList<String> tabCount = new ArrayList<String>(AbstractDriver.getWebDriver().getWindowHandles());
		Helper.assertEquals(tabs, tabCount.size());
	}

	/**
	 * returns the title of the page
	 * 
	 * @return
	 */
	public String getPageTitle() {
		return AbstractDriver.getWebDriver().getTitle();
	}

	/**
	 * returns the current url
	 * 
	 * @return
	 */
	public String getCurrentUrl() {
		return AbstractDriver.getWebDriver().getCurrentUrl();
	}

	/**
	 * gets page source
	 * 
	 * @return
	 */
	public String getPageSource() {
		return AbstractDriver.getWebDriver().getPageSource();
	}

	/**
	 * navigates back
	 */
	public void navigateBack() {
		AbstractDriver.getWebDriver().navigate().back();
	}

	/**
	 * navigate forward
	 */
	public void navigateForward() {
		AbstractDriver.getWebDriver().navigate().forward();
	}

	/**
	 * delete all cookies
	 */
	public void deleteAllCookies() {
		AbstractDriver.getWebDriver().manage().deleteAllCookies();
	}

	/**
	 * delete cookie named
	 * 
	 * @param name
	 */
	public void deleteCookieNamed(String name) {
		AbstractDriver.getWebDriver().manage().deleteCookieNamed(name);
	}

	/**
	 * brings current browser to front
	 */
	public void bringPageToFront() {
		try {
			String currentWindowHandle = AbstractDriver.getWebDriver().getWindowHandle();
			AbstractDriver.getWebDriver().switchTo().window(currentWindowHandle);
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	/**
	 * navigate to a different url
	 * @param url
	 */
	public void swtichUrl(String url) {
		AbstractDriver.getWebDriver().get(url);
		Helper.waitForPageToLoad();
	}
}