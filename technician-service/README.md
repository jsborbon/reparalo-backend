# Servicio de Técnicos para Reparalo

Este microservicio gestiona los perfiles profesionales de técnicos para la plataforma Reparalo, permitiendo a los técnicos registrarse, actualizar su perfil profesional y a los usuarios buscar técnicos por especialidad y ubicación.

## Características

- Gestión completa de perfiles profesionales
- Filtrado por especialidad y ubicación geográfica
- Sistema de valoraciones y reseñas
- Autogestión de perfiles para técnicos
- Verificación de credenciales profesionales

## Tecnologías Utilizadas

- **Spring Boot 3.x**: Framework base para el microservicio
- **Spring Security**: Para la seguridad y autorización
- **Firebase Authentication**: Para la verificación de tokens JWT
- **Firebase Firestore**: Como base de datos NoSQL para almacenar perfiles
- **Springdoc OpenAPI**: Para la documentación de la API (Swagger UI)
- **Java 17**: Versión del lenguaje utilizada
- **Maven**: Gestión de dependencias y construcción

## Estructura del Proyecto

```
src/main/java/com/reparalo/technician
├── config/           # Configuración de Spring, Firebase y Swagger
├── controller/       # Controladores REST
├── dto/              # Objetos de transferencia de datos
├── exception/        # Manejo de excepciones personalizado
├── model/            # Entidades del dominio
├── repository/       # Acceso a datos (Firestore)
├── security/         # Configuración de seguridad y filtros JWT
├── service/          # Lógica de negocio
└── TechnicianServiceApplication.java  # Punto de entrada
```

## Endpoints API

- `GET /api/technicians` - Obtener todos los técnicos (con filtros opcionales)
- `GET /api/technicians/{id}` - Obtener un perfil de técnico específico
- `GET /api/technicians/specialties` - Listar todas las especialidades disponibles
- `GET /api/technicians/locations` - Listar todas las ubicaciones con técnicos
- `POST /api/technicians` - Crear un nuevo perfil de técnico
- `PUT /api/technicians/{id}` - Actualizar un perfil existente (solo propietario)
- `DELETE /api/technicians/{id}` - Eliminar un perfil (solo propietario o admin)
- `POST /api/technicians/{id}/reviews` - Añadir una reseña a un técnico
- `GET /api/technicians/{id}/reviews` - Obtener reseñas de un técnico

## Configuración y Ejecución

### Requisitos Previos

- Java 17 o superior
- Maven 3.8 o superior
- Credenciales de Firebase (archivo JSON de configuración)

### Pasos para Ejecutar

1. Clonar el repositorio
2. Configurar las credenciales de Firebase:

   - Coloca el archivo JSON de configuración en `src/main/resources/firebase-service-account.json`
   - O configura la variable de entorno `GOOGLE_APPLICATION_CREDENTIALS`

3. Ejecutar con Maven:

   ```bash
   mvn spring-boot:run
   ```

4. El servicio estará disponible en: `http://localhost:8083`
5. Acceder a la documentación Swagger UI: `http://localhost:8083/swagger-ui.html`

## Seguridad

Todos los endpoints requieren autenticación mediante token JWT de Firebase, excepto los endpoints de lectura pública. La autorización se basa en roles y propiedad de los recursos:

- Solo el técnico propietario puede editar o eliminar su perfil
- Los administradores pueden gestionar todos los perfiles
- Cualquier usuario autenticado puede ver perfiles y añadir reseñas
- Solo usuarios con rol de técnico pueden crear perfiles profesionales

## Integración con Otros Servicios

Este microservicio se integra con:

- **Servicio de Autenticación**: Para validar tokens JWT y obtener información del usuario
- **Servicio de Tutoriales**: Para asociar especialidades con categorías de tutoriales
