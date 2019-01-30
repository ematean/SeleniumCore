package core.support.objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentTest;

import core.apiCore.helpers.csvReader;
import core.helpers.Helper;
import core.support.configReader.Config;
import core.support.logger.logObject;
import core.uiCore.drivers.AbstractDriver;

/**
 * testInfo
 * 
 * columns: testID testName isFirstRun testClass currentDriver ... 1 testA true
 * classA A DriverA 2 testB true classA B DriverB
 * 
 * @author CAEHMAT
 *
 */

public class TestObject {

	// apiTest : api tests read from csv files through apiTestRunner
	// uiTest : non api tests
	public static enum testType {
		apiTest, uiTest
	}

	public static final String DEFAULT_TEST = "core";
	public static final String DEFAULT_APP = "auto";

	public List<WebDriver> webDriverList = new ArrayList<WebDriver>();
	public String app;
	public testType type;
	public String testId;
	public String testName;
	public String className;
	public String deviceName; // device name for mobile devices

	public String testFileClassName; // same as class name except for api tests
	public Boolean isTestMethod = false; // distinguish between before,after class and test method

	public DriverObject currentDriver;
	public Boolean isFirstRun = false; // is the test running from beginning
	public Boolean isForcedRestart = false; // incase of test failed or other situations

	public boolean isLoggedIn = true;
	public String loggedInUser;
	public String loggedInPassword;
	public int runCount = 0;
	public Boolean isTestPass = false;
	public Boolean isTestComplete = false;
	public Logger log;

	// api test info
	public int currentTestIndex = 0; // index for tests in csv files
	public int testCountInCsvFile = 0; // test count in csv file
	public String testCsvFileName;

	public Description description;
	public Throwable caughtThrowable = null;
	public ArrayList<String> failTrace = new ArrayList<String>();

	// extent report info
	public ExtentTest testFeature;
	public ExtentTest testScenerio; // current test scenario
	public List<ExtentTest> testSteps = new ArrayList<ExtentTest>(); // current test scenario
	public List<String> testSubSteps = new ArrayList<String>(); // current test scenario

	public String startTime; // start time of test in milliseconds
	public String randStringIdentifier; // random identifier for the test

	public List<logObject> testLog = new ArrayList<logObject>();
	public Map<String, String> languageMap = new ConcurrentHashMap<String, String>();
	public Map<String, ApiObject> apiMap = new ConcurrentHashMap<String, ApiObject>();// api keywords
	public Map<String, String> config = new ConcurrentHashMap<String, String>();

	public static ThreadLocal<String> currentTestName = new ThreadLocal<String>();
	public static ThreadLocal<String> currentTestId = new ThreadLocal<String>(); // key for testObject

	public String language;

	// key: testId
	public static Map<String, TestObject> testInfo = new ConcurrentHashMap<String, TestObject>();

	public TestObject withWebDriver(WebDriver webdriver) {
		this.webDriverList.add(webdriver);
		return this;
	}

	/**
	 * once per test, initialize test object maps driver with the test associates
	 * current driver with test
	 */
	public static void initializeTest(String testId) {
		DriverObject driver = new DriverObject().withApp("api");
		initializeTest(driver, testId);
	}

	/**
	 * once per test, initialize test object maps driver with the test associates
	 * current driver with test
	 */
	public static void initializeTest(DriverObject driver, String testId) {
		if (isBeforeTest(testId)) {
			TestObject test = new TestObject();
			test.withTestId(testId).withTestName(test.getTestName()).withTestStartTime(getTimeMiliseconds())
					.withApp(driver.app);
			TestObject.testInfo.put(testId, test);
			// loads all property values into config map
			Config.loadConfig(testId);
			// loads all the keywords for api references
			csvReader.getAllKeywords();

			TestObject.getTestInfo().type = testType.uiTest;
		}
	}

	/**
	 * returns true if webdriver has not started else returns true if isFirstRun is
	 * set to true
	 * 
	 * @return
	 */
	public static boolean isFirstRun() {
		if (AbstractDriver.getWebDriver() == null)
			return true;
		else {
			if (getTestInfo().isFirstRun)
				return true;
		}
		return false;
	}

	/**
	 * testInfo is a static list containing hashmap of test objects with test name
	 * as key if testObject is empty for testId, then default test is initialized
	 * and used
	 * 
	 * @return
	 */
	public static TestObject getTestInfo() {
		String testId = getTestId();

		if (testInfo.get(testId) == null) {
			TestObject.initializeTest(new DriverObject(), TestObject.DEFAULT_TEST);
			testId = DEFAULT_TEST;
		}
		return testInfo.get(testId);
	}

	/**
	 * updates default test app name to driver app name default test object tracks
	 * the test run app name used for report name
	 * 
	 * @param driver
	 */
	public static void updateAppName(DriverObject driver) {
		if (TestObject.getTestInfo(DEFAULT_TEST).app.equals(DEFAULT_APP))
			TestObject.getTestInfo(DEFAULT_TEST).withApp(driver.app);
	}

	public static void setTestName(String testName) {
		TestObject.currentTestName.set(testName);
	}

	public static void setTestId(String testId) {
		TestObject.currentTestId.set(testId);
	}

	public static void setTestId(String testclass, String testname) {
		TestObject.currentTestId.set(testclass + "-" + testname);
	}

	public static String getTestId() {
		String testId = TestObject.currentTestId.get();
		// if testId = null, set to default test
		if (testId == null || testId.isEmpty())
			testId = TestObject.DEFAULT_TEST;

		return testId;
	}

	public static boolean isTestObjectSet() {
		String testId = TestObject.currentTestId.get();
		if (testInfo.isEmpty())
			return false;
		if (testId == null || testInfo.get(testId) == null)
			return false;
		return true;
	}

	/**
	 * TODO: Remove is test runner returns true if test is running, not before
	 * class, or after class based on testid: classname - testname
	 * 
	 * @return
	 */
	public static boolean hasTestStarted() {
		String testId = getTestId();

		// indicates class - testname are set
		if (testId != null && testId.contains("-"))
			return true;

		if (testId == null || testInfo.get(testId) == null)
			return false;

		// applied to api test runner reading csv file
		if (TestObject.getTestInfo().testCountInCsvFile > 0)
			return true;
		return false;
	}

	public static void setLogging() {
		Logger log = Logger.getLogger("");
		TestObject.getTestInfo().log = log;
	}

	public static TestObject getTestInfo(String testId) {
		TestObject test = testInfo.get(testId);
		Helper.assertTrue("test id not found. testId: " + testId + " testInfo: " + testInfo.toString(), test != null);
		return test;
	}

	public static boolean isBeforeTest(String testId) {
		TestObject test = testInfo.get(testId);
		return (test == null);
	}

	public static String getTimeMiliseconds() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}

	/**
	 * resets test object after a test is complete with csv files, test is complete
	 * when all tests in csv files have finished for api csv tests, we're reseting
	 * the test count per csv file.
	 * 
	 * @return
	 */
	public void resetTestObject() {

		if (isTestComplete) {

			// do not reset default test
			if (testId.equals(DEFAULT_TEST))
				return;

			// do not reset these values
			String testname = TestObject.getTestInfo(testId).testName;
			String testFileClass = TestObject.getTestInfo(testId).testFileClassName;
			List<WebDriver> webDrivers = TestObject.getTestInfo(testId).webDriverList;

			TestObject.testInfo.put(testId,
					new TestObject().withTestId(testId).withTestName(testname).withTestFileClassName(testFileClass).withWebDriverList(webDrivers));

			// populate the config with default values
			TestObject.getTestInfo(testId).config.putAll(TestObject.getTestInfo(TestObject.DEFAULT_TEST).config);
		}
	}

	/**
	 * returns true if test is complete for csv api tests, if tests in csv file are
	 * complete
	 * 
	 * @return
	 */
	public static boolean isTestcomplete() {
		int currentTestIndex = TestObject.getTestInfo().currentTestIndex;
		int testCountInCsv = TestObject.getTestInfo().testCountInCsvFile;

		// no tests to run
		if (testCountInCsv == 0)
			return true;

		if (currentTestIndex == (testCountInCsv - 1)) {
			return true;
		}

		return false;
	}

	/**
	 * gets api object from api keywords each keyword is associated with a api call
	 * use api keywords csv files for definitions
	 * 
	 * @param key
	 * @return
	 */
	public static ApiObject getApiDef(String key) {
		csvReader.getAllKeywords();
		return TestObject.getTestInfo().apiMap.get(key);
	}

	public TestObject withIsFirstRun(Boolean isFirstRun) {
		this.isFirstRun = isFirstRun;
		return this;
	}

	public TestObject withIsForcedRestart(Boolean isForcedRestart) {
		this.isForcedRestart = isForcedRestart;
		return this;
	}

	public TestObject withCurrentDriver(DriverObject currentDriver) {
		this.currentDriver = currentDriver;
		return this;
	}

	public TestObject withTestId(String testId) {
		this.testId = testId;
		this.className = testId.contains("-") ? testId.split("-")[0] : testId;
		return this;
	}

	public TestObject withTestStartTime(String time) {
		this.startTime = String.valueOf(time);
		return this;
	}

	public TestObject withRandomIdentifier() {
		String rand = Helper.generateRandomString(30);
		this.randStringIdentifier = rand;
		return this;
	}

	public TestObject withTestName(String testName) {
		this.testName = testName;
		return this;
	}

	public TestObject withClassName(String className) {
		this.className = className;
		return this;
	}

	public TestObject withTestFileClassName(String testFileClassName) {
		this.testFileClassName = testFileClassName;
		return this;
	}
	
	public TestObject withWebDriverList(List<WebDriver>  webDriverList) {
		this.webDriverList = webDriverList;
		return this;
	}

	/**
	 * * testname is grabbed from test id test id is in format of "class - testname"
	 * if is before class and no testname exists, empty string is returned
	 * 
	 * @return
	 */
	public String getTestName() {
		String testName = testId.contains("-") ? testId.split("-")[1].trim() : testId;

		return testName;
	}

	public String getClassName() {
		String className = testId.split("-")[0];
		return className;
	}

	public TestObject withLoggedInUser(String loggedInUser) {
		this.loggedInUser = loggedInUser;
		return this;
	}

	public TestObject withLoggedInPassword(String loggedInPassword) {
		this.loggedInPassword = loggedInPassword;
		return this;
	}

	public TestObject withIsLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
		return this;
	}

	public boolean isUserLoggedIn() {
		return isLoggedIn;
	}

	public TestObject withRerunCount(int rerunCount) {
		this.runCount = rerunCount;
		return this;
	}

	public TestObject incremenetRunCount() {
		this.runCount++;
		return this;
	}

	public TestObject withIsTestPass(Boolean isTestPass) {
		this.isTestPass = isTestPass;
		return this;
	}

	public TestObject withDescription(Description description) {
		this.description = description;
		return this;
	}

	public TestObject withCaughtThrowable(Throwable caughtThrowable) {
		this.caughtThrowable = caughtThrowable;
		return this;
	}

	public TestObject withFailTrace(ArrayList<String> failTrace) {
		this.failTrace = failTrace;
		return this;
	}

	public TestObject withTestScenario(ExtentTest testScenerio) {
		this.testScenerio = testScenerio;
		return this;
	}

	public TestObject withApp(String app) {
		this.app = app;
		return this;
	}
}