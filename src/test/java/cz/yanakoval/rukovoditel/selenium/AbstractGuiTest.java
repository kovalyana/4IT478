package cz.yanakoval.rukovoditel.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractGuiTest {
    protected static String url = "https://digitalnizena.cz/rukovoditel";

    protected void chooseDriver() {
        String systemName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (systemName.contains("windows")) {
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        } else if (systemName.contains("mac os")) {
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver");
        }
    }

    protected void doLogin(WebDriver driver) {

        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys("rukovoditel");

        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("vse456ru");

        WebElement loginButton = driver.findElement(By.className("btn"));
        loginButton.click();
    }

    protected String createNewProject(WebDriver driver) {

        WebElement projectsElem = driver.findElement(By.xpath("//*[text()=' Projects']"));
        projectsElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.btn-primary")));
        WebElement addProjectButton = driver.findElement(By.cssSelector("button.btn-primary"));
        addProjectButton.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_158")));
        WebElement nameElem = driver.findElement(By.id("fields_158"));
        String projectName = "Koval " + UUID.randomUUID().toString();
        nameElem.sendKeys(projectName);

        WebElement calendarElem = driver.findElement(By.cssSelector("i.fa-calendar"));
        calendarElem.click();
        WebElement actualDateElem = driver.findElement(By.cssSelector("td.active"));
        actualDateElem.click();

        WebElement saveButton = driver.findElement(By.className("btn-primary-modal-action"));
        saveButton.click();

        return projectName;
    }

    protected void deleteProjectByName(WebDriver driver, String projectName) {
        searchProjectsStartsWithKoval(driver);

        findTableRowByProjectName(driver, projectName).ifPresent(row -> {
            WebElement deleteElem = row.findElement(By.cssSelector("[title^='Delete']"));
            deleteElem.click();
        });

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("uniform-delete_confirm")));
        WebElement confirmCheckbox = driver.findElement(By.id("uniform-delete_confirm"));
        confirmCheckbox.click();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".modal-footer .btn-primary-modal-action")));
        WebElement deleteButton = driver.findElement(By.cssSelector(".modal-footer .btn-primary-modal-action"));
        deleteButton.click();
    }

    protected void openTaskInfoByName(WebDriver driver, String taskName) {
        new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.data_listing_processing")));
        findTableRowByProjectName(driver, taskName).ifPresent(row -> {
            WebElement infoElem = row.findElement(By.cssSelector("[title^='Info']"));
            infoElem.click();
        });
    }

    protected void deleteTaskByName(WebDriver driver, String taskName) {
        new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.data_listing_processing")));
        findTableRowByProjectName(driver, taskName).ifPresent(row -> {
            WebElement deleteElem = row.findElement(By.cssSelector("[title^='Delete']"));
            deleteElem.click();
        });

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
        WebElement deleteButton = driver.findElement(By.className("btn-primary-modal-action"));
        deleteButton.click();
    }

    protected void searchProjectsStartsWithKoval(WebDriver driver) {
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(@class, 'page-sidebar-menu')]/*//*[text()=' Projects']")));
        WebElement projectsElem = driver.findElement(By.xpath("//*[contains(@class, 'page-sidebar-menu')]/*//*[text()=' Projects']"));
        projectsElem.click();

        if (driver.findElements(By.cssSelector("span.reset_search")).size() != 0) {
            driver.findElement(By.cssSelector("span.reset_search")).click();
        }

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("entity_items_listing66_21_search_keywords")));
        WebElement searchInput = driver.findElement(By.id("entity_items_listing66_21_search_keywords"));
        searchInput.clear();
        searchInput.sendKeys("Koval");
        WebElement searchButton = driver.findElement(By.cssSelector("[title^='Search']"));
        searchButton.click();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.data_listing_processing")));
        new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.data_listing_processing")));
    }

    private Optional<WebElement> findTableRowByProjectName(WebDriver driver, String elemName) {
        return driver
                .findElements(By.cssSelector("tbody tr"))
                .stream()
                .filter(row -> {
                    List<WebElement> matchedCells = row.findElements(By.partialLinkText(elemName));
                    return !matchedCells.isEmpty();
                })
                .findFirst();
    }
}
