package junit.org.rapidpm.vaadin.helloworld.server.junit5.selenium;

import junit.org.rapidpm.vaadin.helloworld.server.junit5.MyUIPageObject;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.openqa.selenium.WebDriver;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static junit.org.rapidpm.vaadin.helloworld.server.junit5.selenium.WebDriverFunctions.*;

/**
 *
 */
public class WebDriverSeleniumExtension
    implements AfterTestExecutionCallback, BeforeAllCallback, TestExecutionExceptionHandler {

  public static final String WEBDRIVER = "webdriver";

  static Function<ExtensionContext, Namespace> namespaceFor() {
    return (ctx) -> Namespace.create(WebDriverSeleniumExtension.class,
                                     ctx.getTestClass().get().getName(),
                                     ctx.getTestMethod().get().getName()
    );
  }

  static Function<ExtensionContext, ExtensionContext.Store> store() {
    return (context) -> context.getStore(namespaceFor().apply(context));
  }

  static Function<ExtensionContext, WebDriver> webdriver() {
    return (context) -> store().apply(context).get(WEBDRIVER, WebDriver.class);
  }

  static BiConsumer<ExtensionContext, WebDriver> storeWebDriver() {
    return (context, webDriver) -> store().apply(context).put(WEBDRIVER, webDriver);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    final WebDriver webdriver = webdriver().apply(context);

    webdriver.close();
    webdriver.quit();

    store().apply(context).remove(WEBDRIVER);
  }


  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    System.setProperty("webdriver.chrome.driver", "_data/webdrivers/chromedriver-mac-64bit");
    System.setProperty("webdriver.opera.driver", "_data/webdrivers/operadriver-mac-64bit/operadriver");
    System.setProperty("webdriver.gecko.driver", "_data/webdrivers/geckodriver-mac-64bit");
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {

    takeScreenShot().accept(webdriver().apply(context));

    throw throwable;
  }


  public static class PageObjectProvider implements ArgumentsProvider {


    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
      return Stream
          .of(
              newWebDriverChrome(),
              newWebDriverOpera()
          )
          .map(Supplier::get)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .peek(d -> storeWebDriver().accept(context, d))
          .map(MyUIPageObject::new)
          .map(Arguments::of);
    }
  }
}
