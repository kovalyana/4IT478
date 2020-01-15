package cz.yanakoval.rukovoditel.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CreatingProjectTest extends AbstractGuiTest {
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
    public void shouldNotCreateProjectWithoutName() {
        // given
        driver.get(url);
        doLogin(driver);

        // when
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()=' Projects']")));
        WebElement projectsElem = driver.findElement(By.xpath("//*[text()=' Projects']"));
        projectsElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.btn-primary")));
        WebElement addProjectButton = driver.findElement(By.cssSelector("button.btn-primary"));
        addProjectButton.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".modal-footer .btn-primary-modal-action")));
        WebElement saveButton = driver.findElement(By.cssSelector(".modal-footer .btn-primary-modal-action"));
        saveButton.click();

        // then
        WebElement errorMessage = driver.findElement(By.className("alert"));
        assertEquals("Some fields are required. They have been highlighted above.", errorMessage.getText());
    }

    @Test
    public void shouldCreateNewProject() {
        // given
        driver.get(url);
        doLogin(driver);

        // when
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()=' Projects']")));
        WebElement projectsElem = driver.findElement(By.xpath("//*[contains(@class, 'page-sidebar-menu')]/*//*[text()=' Projects']"));
        projectsElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.btn-primary")));
        WebElement addProjectButton = driver.findElement(By.cssSelector("button.btn-primary"));
        addProjectButton.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_156")));
        WebElement priorityElem = driver.findElement(By.id("fields_156"));
        priorityElem.click();
        WebElement highPriorityElem = driver.findElement(By.cssSelector("[value='35']"));
        highPriorityElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_157")));
        WebElement statusElem = driver.findElement(By.id("fields_157"));
        statusElem.click();
        WebElement newStatusElem = driver.findElement(By.cssSelector("[value='37']"));
        newStatusElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_158")));
        WebElement nameElem = driver.findElement(By.id("fields_158"));
        String projectName = "Koval " + UUID.randomUUID().toString();
        nameElem.sendKeys(projectName);

        WebElement calendarElem = driver.findElement(By.cssSelector("i.fa-calendar"));
        calendarElem.click();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("td.active")));
        WebElement actualDateElem = driver.findElement(By.cssSelector("td.active"));
        actualDateElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".modal-footer .btn-primary-modal-action")));
        WebElement saveButton = driver.findElement(By.cssSelector(".modal-footer .btn-primary-modal-action"));
        saveButton.click();

        // then
        WebElement projectNameInPageBreadcrumb = driver.findElement(By.cssSelector(".page-breadcrumb li:nth-child(2) a"));
        assertEquals(projectName, projectNameInPageBreadcrumb.getText());

        deleteProjectByName(driver, projectName);
    }
}
