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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TaskTest extends AbstractGuiTest {
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
    public void shouldCreateNewTask() {
        // given
        driver.get(url);
        doLogin(driver);
        String projectName = createNewProject(driver);

        // when
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.btn-primary")));
        WebElement addTaskButton = driver.findElement(By.cssSelector("button.btn-primary"));
        addTaskButton.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_167")));
        WebElement typeElem = driver.findElement(By.id("fields_167"));
        typeElem.click();
        WebElement taskTypeElem = driver.findElement(By.cssSelector("[value='42']"));
        taskTypeElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_168")));
        WebElement nameElem = driver.findElement(By.id("fields_168"));
        String taskName = "Koval " + UUID.randomUUID().toString();
        nameElem.sendKeys(taskName);

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_169")));
        WebElement statusElem = driver.findElement(By.id("fields_169"));
        statusElem.click();
        WebElement newStatusElem = driver.findElement(By.cssSelector("[value='46']"));
        newStatusElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_170")));
        WebElement priorityElem = driver.findElement(By.id("fields_170"));
        priorityElem.click();
        WebElement mediumPriorityElem = driver.findElement(By.cssSelector("[value='55']"));
        mediumPriorityElem.click();

        WebElement codesnippetElem = driver.findElement(By.id("cke_16"));
        codesnippetElem.click();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("cke_80_textarea")));
        WebElement textareaElem = driver.findElement(By.id("cke_80_textarea"));
        String taskDescription = "Some description";
        textareaElem.sendKeys(taskDescription);

        WebElement okElem = driver.findElement(By.className("cke_dialog_ui_button_ok"));
        okElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
        WebElement saveButton = driver.findElement(By.className("btn-primary-modal-action"));
        saveButton.click();

        // then
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[title^='Info']")));
        openTaskInfoByName(driver, taskName);
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tbody :nth-child(5) td div")));
        WebElement taskTypeInInfoElem = driver.findElement(By.cssSelector("tbody :nth-child(4) td div"));
        WebElement taskStatusElem = driver.findElement(By.cssSelector("tbody :nth-child(5) td div"));
        WebElement taskPriorityElem = driver.findElement(By.cssSelector("tbody :nth-child(6) td div"));
        WebElement taskNameElem = driver.findElement(By.className("caption"));
        WebElement taskDescriptionElem = driver.findElement(By.cssSelector(".fieldtype_textarea_wysiwyg code"));

        assertEquals("Task", taskTypeInInfoElem.getText());
        assertEquals("New", taskStatusElem.getText());
        assertEquals("Medium", taskPriorityElem.getText());
        assertEquals(taskName, taskNameElem.getText());
        assertEquals(taskDescription, taskDescriptionElem.getText());

        WebElement tasksLink = driver.findElement(By.partialLinkText("Tasks"));
        tasksLink.click();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[title^='Info']")));
        deleteTaskByName(driver, taskName);
        deleteProjectByName(driver, projectName);
    }

    @Test
    public void shouldCheckTaskFilter() {
        // given
        driver.get(url);
        doLogin(driver);
        String projectName = createNewProject(driver);

        // when
        for (int i = 0; i < 7; i++) {
            new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.btn-primary")));
            WebElement addTaskButton = driver.findElement(By.cssSelector("button.btn-primary"));
            addTaskButton.click();

            new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_168")));
            WebElement nameElem = driver.findElement(By.id("fields_168"));
            int temp = i + 1;
            String taskName = "Koval " + temp;
            nameElem.sendKeys(taskName);

            WebElement statusElem = driver.findElement(By.id("fields_169"));
            statusElem.click();

            WebElement statusValue = driver.findElement(By.cssSelector("#fields_169 :nth-child(" + temp + ")"));
            statusValue.click();

            new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
            WebElement saveButton = driver.findElement(By.className("btn-primary-modal-action"));
            saveButton.click();
        }

        new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.data_listing_processing")));
        List<WebElement> matchedCellsNOW = driver.findElements(By.cssSelector("tbody tr"));
        assertEquals(0, checkStatusesInArray(createArrayWithStatuses("NOW"), 3).size());

        WebElement statusFilerElem = driver.findElement(By.className("filters-preview-condition-include"));
        statusFilerElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".modal-footer .btn-primary-modal-action")));
        WebElement deleteOpenFilterElem = driver.findElement(By.xpath("//*[text()='Open']/ancestor::*[@class='search-choice']/a"));
        deleteOpenFilterElem.click();

        WebElement saveButton = driver.findElement(By.cssSelector(".modal-footer .btn-primary-modal-action"));
        saveButton.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.data_listing_processing")));
        List<WebElement> matchedCellsNW = driver.findElements(By.cssSelector("tbody tr"));
        assertEquals(0, checkStatusesInArray(createArrayWithStatuses("NW"), 2).size());

        WebElement deleteFilersElem = driver.findElement(By.cssSelector("[title^='Remove Filter']"));
        deleteFilersElem.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.data_listing_processing")));
        List<WebElement> matchedCellsALL = driver.findElements(By.cssSelector("tbody tr"));
        assertEquals(0, checkStatusesInArray(createArrayWithStatuses("ALL"), 7).size());

        //then
        assertEquals(3, matchedCellsNOW.size());
        assertEquals(2, matchedCellsNW.size());
        assertEquals(7, matchedCellsALL.size());

        WebElement allItemsCheckbox = driver.findElement(By.id("select_all_items"));
        allItemsCheckbox.click();

        WebElement withSelectedButton = driver.findElement(By.cssSelector(".col-sm-5 .btn-group"));
        withSelectedButton.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".col-sm-5 .btn-group ul li:last-child")));
        WebElement deleteSelectedButton = driver.findElement(By.cssSelector(".col-sm-5 .btn-group ul li:last-child"));
        deleteSelectedButton.click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#delete_selected_form .btn-primary-modal-action")));
        WebElement deleteButton = driver.findElement(By.cssSelector("#delete_selected_form .btn-primary-modal-action"));
        deleteButton.click();

        deleteProjectByName(driver, projectName);

    }

    private List<String> createArrayWithStatuses(String statusesAbbreviation) {
        List<String> statuses = new ArrayList<>();
        switch (statusesAbbreviation) {
            case "NW":
                statuses.add("New");
                statuses.add("Waiting");
                break;
            case "NOW":
                statuses.add("New");
                statuses.add("Waiting");
                statuses.add("Open");
                break;
            case "ALL":
                statuses.add("New");
                statuses.add("Waiting");
                statuses.add("Done");
                statuses.add("Closed");
                statuses.add("Paid");
                statuses.add("Canceled");
                break;
        }
        return statuses;
    }

    private List<String> checkStatusesInArray(List<String> statuses, int size) {
        for (int i = 0; i < size; i++) {
            int j = i + 1;
            String statusText = driver.findElement(By.cssSelector("tbody :nth-child(" + j + ") :nth-child(7)")).getText();
            statuses.remove(statusText);
        }
        return statuses;
    }
}
