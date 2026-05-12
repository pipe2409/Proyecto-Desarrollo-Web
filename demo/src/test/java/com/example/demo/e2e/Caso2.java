package com.example.demo.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.annotation.DirtiesContext;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class Caso2 {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200"; // URL de Angular

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @DisplayName("Flujo Completo: Login Huésped -> Check-in Operador -> Servicios -> Pago -> Checkout")
    void testFlujoCompletoEstadia() throws InterruptedException {
        // --- PASO 1: LOGIN HUÉSPED ---
        driver.get(BASE_URL + "/login");
        
        // Esperar a que los campos estén presentes y escribir
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("correo")));
        driver.findElement(By.id("correo")).sendKeys("h1@mail.com");
        driver.findElement(By.id("contrasena")).sendKeys("123");
        
        // Click en submit
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitBtn.click();

        // Verificar que el huésped se redirige a HOME (/) después del login exitoso
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
        Thread.sleep(1000); // Pequeña pausa para que Angular renderice

        // --- PASO 2: LOGIN OPERADOR (En la misma sesión para la prueba) ---
        // Logout del huésped
        wait.until(ExpectedConditions.elementToBeClickable(By.id("logout-btn"))).click();
        
        driver.get(BASE_URL + "/login");
        
        // Esperar a que los campos estén presentes
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("correo")));
        driver.findElement(By.id("correo")).sendKeys("admin@hotel.com");
        driver.findElement(By.id("contrasena")).sendKeys("admin123");
        
        WebElement submitBtnOperador = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitBtnOperador.click();

        // El operador se redirecciona a /menu-admin
        wait.until(ExpectedConditions.urlContains("/menu-admin"));
        Thread.sleep(1000);
        
        // --- PASO 3: CHECK-IN ---
        // Buscar la reserva del huesped 1 y dar click en Check-in
        WebElement btnCheckin = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[contains(text(), 'Huesped1')]/..//button[contains(text(), 'Check-in')]")));
        btnCheckin.click();
        
        // --- PASO 4: AGREGAR 2 SERVICIOS ---
        WebElement btnServicios = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[contains(text(), 'Huesped1')]/..//button[contains(text(), 'Servicios')]")));
        btnServicios.click();

        // Agregar primer servicio (ej: Desayuno)
        agregarServicio("Desayuno Buffet", "2");
        // Agregar segundo servicio (ej: Lavandería)
        agregarServicio("Lavandería", "1");

        // --- PASO 5: VERIFICACIÓN DE MONTO Y PAGO ---
        WebElement btnVerCuenta = driver.findElement(By.id("ver-cuenta-btn"));
        btnVerCuenta.click();

        WebElement totalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("total-cuenta")));
        String totalTexto = totalElement.getText();
        // El total debería ser > 0 debido a los servicios
        assertFalse(totalTexto.contains("$0"), "El monto a pagar debería ser mayor a cero");

        // Simular Pago
        driver.findElement(By.id("btn-pagar-todo")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));

        // --- PASO 6: FINALIZAR RESERVA (CHECKOUT) ---
        WebElement btnCheckout = driver.findElement(By.id("btn-finalizar-reserva"));
        btnCheckout.click();

        // Verificar estado final
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("estado-reserva")));
        WebElement estadoFinal = driver.findElement(By.id("estado-reserva"));
        assertEquals("FINALIZADA", estadoFinal.getText(), "La reserva debe quedar en estado FINALIZADA");
    }

    private void agregarServicio(String nombreServicio, String cantidad) {
        WebElement selectServicio = wait.until(ExpectedConditions.elementToBeClickable(By.id("servicio-select")));
        selectServicio.sendKeys(nombreServicio);
        driver.findElement(By.id("cantidad-input")).clear();
        driver.findElement(By.id("cantidad-input")).sendKeys(cantidad);
        driver.findElement(By.id("btn-confirmar-servicio")).click();
        
        // Esperar a que se actualice la lista de items
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(text(), '" + nombreServicio + "')]")));
    }
}
