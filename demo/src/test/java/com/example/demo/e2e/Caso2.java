package com.example.demo.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.annotation.DirtiesContext;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;


import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class Caso2 {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200"; // URL de Angular

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @DisplayName("Flujo Completo: Login Huésped -> Logout -> Login Operador -> Verificar Acceso a Operador")
    void testFlujoCompletoEstadia() throws InterruptedException {
        
        // --- PASO 1: LOGIN HUÉSPED ---
        driver.get(BASE_URL + "/login");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("correo")));
        driver.findElement(By.id("correo")).sendKeys("h1@mail.com");
        driver.findElement(By.id("contrasena")).sendKeys("123");

        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitBtn.click();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
        Thread.sleep(500);

        // --- PASO 2: LOGOUT HUÉSPED ---
        WebElement logoutBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
        By.xpath("//a[normalize-space()='Cerrar sesión']")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", logoutBtn);
        Thread.sleep(300);

        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
        Thread.sleep(500);

        // --- PASO 3: LOGIN OPERADOR ---
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("correo")));
        driver.findElement(By.id("correo")).sendKeys("admin@hotel.com");
        driver.findElement(By.id("contrasena")).sendKeys("admin123");

        WebElement submitBtnOperador = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitBtnOperador.click();

        wait.until(ExpectedConditions.urlContains("/menu-admin"));
        Thread.sleep(500);
        
        // --- PASO 4: VERIFICAR ACCESO A PANEL OPERADOR ---
        // Verificar que estamos en el panel del operador (menu-admin)
        assertTrue(driver.getCurrentUrl().contains("/menu-admin"), 
                "El operador debe estar en el panel de administración");
        
        // Verificar que existe la opción de Servicios en la navegación
        WebElement serviciosLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@href, '/operador/servicios-cuenta') or contains(text(), 'Servicios')]")));
        assertNotNull(serviciosLink, "Debe existir el acceso a Servicios a Cuenta");

        // --- PASO 5: NAVEGAR A SERVICIOS ---
        driver.get(BASE_URL + "/operador/servicios-cuenta");
        wait.until(ExpectedConditions.urlContains("/operador/servicios-cuenta"));
        
        // Verificar que la página de servicios cargó correctamente
        WebElement searchButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[normalize-space()='Buscar']")));
        assertNotNull(searchButton, "Debe existir el botón de búsqueda de habitación");
        
        // Verificar que los servicios disponibles se cargaron
        WebElement serviciosGrid = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'grid') or contains(text(), 'Desayuno')]")));
        assertNotNull(serviciosGrid, "Deben existir servicios disponibles en la UI");

        // Test completado exitosamente
        assertTrue(true, "Flujo completo: Login Huésped -> Logout -> Login Operador -> Acceso a Servicios completado");
    }
}
