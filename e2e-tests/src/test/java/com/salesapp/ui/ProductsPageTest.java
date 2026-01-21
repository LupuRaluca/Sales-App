package com.salesapp.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductsPageTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.get("http://localhost:5173/");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void homePageShouldLoad() {

        String title = driver.getTitle();
        System.out.println("Titlul paginii este: " + title);

        assertTrue(driver.getCurrentUrl().contains("localhost"), "Nu suntem pe localhost!");
    }
}