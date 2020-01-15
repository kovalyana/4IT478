package cz.yanakoval.rukovoditel.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertEquals;

public class LoginTest extends AbstractGuiTest {
    private WebDriver driver;

    @Before
    public void init() {
        chooseDriver();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    public void shouldLoginUsingValidCredentials() {
        // given
        driver.get(url);

        // when
        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys("rukovoditel");

        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("vse456ru");

        WebElement loginButton = driver.findElement(By.className("btn"));
        loginButton.click();

        // then
        WebElement pageTitle = driver.findElement(By.className("page-title"));
        assertEquals("Welcome to the Rukovoditel – your new assistant in business management!", pageTitle.getText());
    }

    @Test
    public void shouldNotLoginUsingInvalidPassword() {
        // given
        driver.get(url);

        // when
        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys("rukovoditel");

        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("vse45ru");

        WebElement loginButton = driver.findElement(By.className("btn"));
        loginButton.click();

        // then
        WebElement errorMessage = driver.findElement(By.className("alert"));
        assertEquals("×\n" + "No match for Username and/or Password.", errorMessage.getText());
    }

    @Test
    public void shouldLogsOffLoggedUser() {
        // given
        driver.get(url);
        doLogin(driver);

        // when
        WebElement usernameElem = driver.findElement(By.className("username"));
        usernameElem.click();

        WebElement logoffElem = driver.findElement(By.cssSelector("ul.dropdown-menu :last-child a i"));
        logoffElem.click();

        // then
        WebElement formTitle = driver.findElement(By.className("form-title"));
        assertEquals("Login", formTitle.getText());
    }
}
