package junit.org.rapidpm.vaadin.helloworld.server.junit5;

import junit.org.rapidpm.vaadin.helloworld.server.junit5.selenium.WebDriverSeleniumExtension;
import junit.org.rapidpm.vaadin.helloworld.server.junit5.vaadin.VaadinTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rapidpm.vaadin.helloworld.server.MyUI;

import static junit.framework.TestCase.assertNotNull;

/**
 *
 */

@VaadinTest
public class MyUITest {

  @ParameterizedTest(name = "{index} ==> ''{0}''")
  @ArgumentsSource(WebDriverSeleniumExtension.PageObjectProvider.class)
  void test001(final MyUIPageObject pageObject) {
    assertNotNull(pageObject);

    pageObject.loadPage();

    pageObject.inputA.get().sendKeys("5");
    pageObject.inputB.get().sendKeys("5");

    pageObject.button.get().click();

    String value = pageObject.output.get().getAttribute("value");
    Assertions.assertEquals("55", value);
  }
}
