package com.example.demo.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
public class Caso1 {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String BASE_URL = "http://localhost:4200";
    private final String BACK_URL = "http://localhost:8080";
    private final int TIPO_HABITACION_ID = 1;

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void caso1_registroReservaYSegundaReservaConHabitacionDiferente() throws Exception {
        String correo = "cliente" + System.currentTimeMillis() + "@test.com";
        String contrasena = "123456";

        LocalDate inicio = LocalDate.now().plusWeeks(1);
        LocalDate fin = inicio.plusDays(3);

        LocalDate inicioSegunda = inicio.plusDays(1);
        LocalDate finSegunda = fin.plusDays(1);

        // 1. Usuario llega al landing page
        driver.get(BASE_URL);

        WebElement reservarAhora = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[routerLink='/login'], a[routerlink='/login']")
                )
        );

        reservarAhora.click();

        wait.until(ExpectedConditions.urlContains("/login"));

        // 2. Usuario va al registro porque no tiene cuenta
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Regístrate aquí"))).click();

        wait.until(ExpectedConditions.urlContains("/registro"));

        // 3. Primer registro incorrecto por correo inválido
        llenarRegistro("Carlos", "Prueba", "correo-invalido", contrasena);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement correoInput = driver.findElement(By.id("correo"));

        assertTrue(
                correoInput.getAttribute("class").contains("ng-invalid"),
                "El correo inválido debe marcar el campo como inválido"
        );

        assertTrue(
                driver.getCurrentUrl().contains("/registro"),
                "Con correo inválido, el usuario debe permanecer en registro"
        );

        // 4. Corrige correo y se registra correctamente
        correoInput.clear();
        correoInput.sendKeys(correo);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/login"));

        // 5. Login con el usuario registrado
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("correo"))).sendKeys(correo);
        driver.findElement(By.id("contrasena")).sendKeys(contrasena);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));

        Integer huespedId = obtenerHuespedIdDesdeLocalStorage();

        assertNotNull(
                huespedId,
                "Después del login debe existir huespedId en localStorage"
        );

        // 6. Primera reserva para la siguiente semana
        driver.get(BASE_URL + "/reservar-tipo/" + TIPO_HABITACION_ID);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fechaInicio")));

        String primeraHabitacionAlert = crearReservaYObtenerHabitacionDesdeAlert(inicio, fin);

        JsonNode reservasDespuesPrimera = obtenerReservasDelHuesped(huespedId);

        assertTrue(
                reservasDespuesPrimera.size() >= 1,
                "Debe existir al menos una reserva después de crear la primera"
        );

        JsonNode primeraReserva = reservasDespuesPrimera.get(reservasDespuesPrimera.size() - 1);

        int primeraHabitacionId = primeraReserva.get("habitacion").get("id").asInt();
        String primeraHabitacionCodigo = primeraReserva.get("habitacion").get("codigo").asText();

        assertNotNull(
                primeraHabitacionCodigo,
                "La primera reserva debe tener habitación asignada"
        );

        assertFalse(
                primeraHabitacionCodigo.isBlank(),
                "El código de la primera habitación no debe estar vacío"
        );

        assertTrue(
                primeraHabitacionAlert.contains(primeraHabitacionCodigo),
                "El alert debe mostrar la habitación asignada en la primera reserva"
        );

        // 7. Verificar que la primera habitación ya no esté disponible para las mismas fechas
        int statusReservaMismaHabitacion = intentarReservarHabitacionEspecifica(
                primeraHabitacionId,
                huespedId,
                inicio,
                fin
        );

        assertEquals(
                400,
                statusReservaMismaHabitacion,
                "La primera habitación ya no debe estar disponible para las mismas fechas"
        );

        // 8. Segunda reserva con fechas interceptadas
        driver.get(BASE_URL + "/reservar-tipo/" + TIPO_HABITACION_ID);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fechaInicio")));

        String segundaHabitacionAlert = crearReservaYObtenerHabitacionDesdeAlert(inicioSegunda, finSegunda);

        JsonNode reservasDespuesSegunda = obtenerReservasDelHuesped(huespedId);

        assertTrue(
                reservasDespuesSegunda.size() >= 2,
                "El huésped debe tener al menos dos reservas creadas"
        );

        JsonNode segundaReserva = reservasDespuesSegunda.get(reservasDespuesSegunda.size() - 1);

        int segundaHabitacionId = segundaReserva.get("habitacion").get("id").asInt();
        String segundaHabitacionCodigo = segundaReserva.get("habitacion").get("codigo").asText();

        assertNotNull(
                segundaHabitacionCodigo,
                "La segunda reserva debe tener habitación asignada"
        );

        assertFalse(
                segundaHabitacionCodigo.isBlank(),
                "El código de la segunda habitación no debe estar vacío"
        );

        assertTrue(
                segundaHabitacionAlert.contains(segundaHabitacionCodigo),
                "El alert debe mostrar la habitación asignada en la segunda reserva"
        );

        // 9. Verificar que ambas habitaciones sean diferentes
        assertNotEquals(
                primeraHabitacionId,
                segundaHabitacionId,
                "La segunda reserva debe asignar una habitación diferente porque las fechas se interceptan"
        );

        assertNotEquals(
                primeraHabitacionCodigo,
                segundaHabitacionCodigo,
                "El código de habitación de la segunda reserva debe ser diferente al de la primera"
        );
    }

    private void llenarRegistro(String nombre, String apellido, String correo, String contrasena) {
        driver.findElement(By.id("nombre")).clear();
        driver.findElement(By.id("nombre")).sendKeys(nombre);

        driver.findElement(By.id("apellido")).clear();
        driver.findElement(By.id("apellido")).sendKeys(apellido);

        driver.findElement(By.id("correo")).clear();
        driver.findElement(By.id("correo")).sendKeys(correo);

        driver.findElement(By.id("contrasena")).clear();
        driver.findElement(By.id("contrasena")).sendKeys(contrasena);

        driver.findElement(By.id("cedula")).clear();
driver.findElement(By.id("cedula")).sendKeys(String.valueOf(System.currentTimeMillis()).substring(3));
        driver.findElement(By.id("telefono")).clear();
        driver.findElement(By.id("telefono")).sendKeys("3001234567");

        driver.findElement(By.id("direccion")).clear();
        driver.findElement(By.id("direccion")).sendKeys("Bogotá");

        driver.findElement(By.id("nacionalidad")).clear();
        driver.findElement(By.id("nacionalidad")).sendKeys("Colombiana");
    }

    private String crearReservaYObtenerHabitacionDesdeAlert(LocalDate inicio, LocalDate fin) {
        setDateValue("fechaInicio", inicio.toString());
        setDateValue("fechaFin", fin.toString());

        WebElement personas = driver.findElement(By.id("personas"));
        personas.clear();
        personas.sendKeys("2");

        driver.findElement(By.className("btn-confirmar")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        String mensaje = alert.getText();

        alert.accept();

        assertTrue(
                mensaje.contains("Reserva creada correctamente"),
                "Debe mostrarse mensaje de reserva creada correctamente"
        );

        assertTrue(
                mensaje.contains("Habitación asignada:"),
                "El alert debe incluir la habitación asignada"
        );

        return extraerHabitacionAsignada(mensaje);
    }

    private void setDateValue(String id, String value) {
        WebElement input = driver.findElement(By.id(id));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                input,
                value
        );
    }

    private String extraerHabitacionAsignada(String mensaje) {
        String texto = "Habitación asignada:";
        int index = mensaje.indexOf(texto);

        assertTrue(index >= 0, "El alert debe incluir la habitación asignada");

        return mensaje.substring(index + texto.length()).trim();
    }

    private Integer obtenerHuespedIdDesdeLocalStorage() {
        Object valor = ((JavascriptExecutor) driver).executeScript(
                "return localStorage.getItem('huespedId');"
        );

        if (valor == null) {
            return null;
        }

        return Integer.parseInt(valor.toString());
    }

    private JsonNode obtenerReservasDelHuesped(Integer huespedId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACK_URL + "/api/reservas/huesped/" + huespedId))
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(
                200,
                response.statusCode(),
                "Debe poder consultar las reservas del huésped"
        );

        JsonNode reservas = mapper.readTree(response.body());

        assertTrue(
                reservas.isArray(),
                "La respuesta de reservas debe ser un arreglo"
        );

        return reservas;
    }

    private int intentarReservarHabitacionEspecifica(
            int habitacionId,
            int huespedId,
            LocalDate inicio,
            LocalDate fin
    ) throws Exception {
        String json = """
                {
                  "habitacionId": %d,
                  "huespedId": %d,
                  "cantidadPersonas": 2,
                  "fechaInicio": "%s",
                  "fechaFin": "%s"
                }
                """.formatted(habitacionId, huespedId, inicio, fin);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACK_URL + "/api/reservas/crear"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode();
    }
}