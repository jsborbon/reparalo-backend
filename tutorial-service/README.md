# Servicio de Tutoriales para Reparalo

Este microservicio gestiona los tutoriales de reparación para la plataforma Reparalo, permitiendo a técnicos y administradores crear, editar y eliminar tutoriales, mientras que los usuarios pueden visualizarlos, filtrarlos por categoría y dificultad, y darles like.

## Características

- CRUD completo de tutoriales
- Filtrado por categoría y nivel de dificultad
- Contador de vistas y likes
- Autenticación y autorización basada en Firebase
- Documentación API con Swagger/OpenAPI

## Estructura del Proyecto

```
tutorial-service/
├── src/main/java/com/reparalo/tutorialservice/
│   ├── config/                  # Configuraciones (Firebase, Seguridad)
│   ├── controller/              # Controladores REST
│   ├── dto/                     # Objetos de transferencia de datos
│   ├── model/                   # Modelos de dominio
│   ├── security/                # Filtros de seguridad
│   ├── service/                 # Lógica de negocio
│   └── TutorialServiceApplication.java  # Clase principal
└── src/main/resources/
    └── application.properties   # Configuración de la aplicación
```

## Endpoints API

### Públicos (sin autenticación)

- `GET /api/v1/tutoriales/public` - Obtener todos los tutoriales activos
- `GET /api/v1/tutoriales/public/categoria/{categoria}` - Filtrar por categoría
- `GET /api/v1/tutoriales/public/dificultad/{dificultad}` - Filtrar por dificultad
- `GET /api/v1/tutoriales/public/{id}` - Obtener un tutorial específico
- `POST /api/v1/tutoriales/public/{id}/like` - Dar like a un tutorial

### Protegidos (requieren autenticación)

- `POST /api/v1/tutoriales/tecnico` - Crear un nuevo tutorial (ADMIN, TECNICO)
- `PUT /api/v1/tutoriales/tecnico/{id}` - Actualizar un tutorial (ADMIN, TECNICO)
- `DELETE /api/v1/tutoriales/tecnico/{id}` - Eliminar un tutorial (ADMIN, TECNICO)

## Configuración

El servicio requiere un archivo de credenciales de Firebase para la autenticación. Asegúrate de tener el archivo `firebase-service-account.json` en el classpath.

## Ejecución

```bash
# Compilar el proyecto
mvn clean package

# Ejecutar el servicio
java -jar target/tutorial-service-0.0.1-SNAPSHOT.jar
```

El servicio estará disponible en http://localhost:8082

## Documentación API

La documentación Swagger estará disponible en:
http://localhost:8082/swagger-ui.html
