# Sistema de Gestión de Hotel

Esta es una aplicación web para la gestión de un hotel, construida con Spring Boot.

## Características

*   **Gestión de Usuarios**: Los huéspedes pueden registrarse y gestionar sus cuentas.
*   **Gestión de Habitaciones**: Los administradores pueden gestionar los tipos de habitación y las habitaciones individuales.
*   **Gestión de Servicios**: Los administradores pueden gestionar los servicios ofrecidos por el hotel.
*   **Reservas**: Los huéspedes pueden hacer reservas de habitaciones.
*   **Facturación**: El sistema puede generar facturas para los huéspedes.

## Tecnologías Utilizadas

*   **Backend**: Spring Boot, Spring Web, Spring Data JPA
*   **Frontend**: Thymeleaf, HTML, CSS, JavaScript
*   **Base de Datos**: H2 Database
*   **Herramienta de Construcción**: Maven

## Primeros Pasos

Para obtener una copia local y ponerla en marcha, sigue estos sencillos pasos.

### Prerrequisitos

*   JDK 17 o posterior
*   Maven 3.6 o posterior

### Instalación

1.  Clona el repositorio
    ```sh
    git clone https://github.com/tu_usuario/Proyecto-Desarrollo-Web.git
    ```
2.  Navega al directorio `demo`
    ```sh
    cd Proyecto-Desarrollo-Web/demo
    ```
3.  Ejecuta la aplicación
    ```sh
    ./mvnw spring-boot:run
    ```

La aplicación estará disponible en `http://localhost:8080`.

## Estructura del Proyecto

El proyecto está estructurado como un proyecto estándar de Maven:

```
.
├── demo
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── example
│   │   │   │           └── demo
│   │   │   │               ├── controller
│   │   │   │               ├── entities
│   │   │   │               ├── repository
│   │   │   │               └── service
│   │   │   └── resources
│   │   │       ├── static
│   │   │       └── templates
│   └── pom.xml
└── docs
    ├── Diagrama entidad Relacion.png
    └── Diagramaclases.png
```

## Base de Datos

La aplicación utiliza una base de datos en memoria H2. La consola de la base de datos está disponible en `http://localhost:8080/h2-console` con las siguientes propiedades:

*   **Clase del Driver**: `org.h2.Driver`
*   **URL JDBC**: `jdbc:h2:mem:hoteldb`
*   **Nombre de Usuario**: `sa`
*   **Contraseña**: 

## Diagrama ER

![Diagrama de Entidad Relación](docs/Diagrama%20entidad%20Relacion.png)

## Diagrama de Clases

![Diagrama de Clases](docs/Diagramaclases.png)

## Licencia

Distribuido bajo la Licencia MIT. Consulta `LICENSE` para más información.
