# Servicio de Comentarios para Reparalo

Este microservicio gestiona los comentarios de los tutoriales para la plataforma Reparalo, permitiendo a los usuarios autenticados crear, editar y eliminar sus propios comentarios, así como ver los comentarios de otros usuarios en los tutoriales.

## Características

- CRUD completo de comentarios por tutorial
- Validación de autoría (solo el autor puede editar/eliminar)
- Integración con el servicio de autenticación
- Paginación de resultados
- Ordenación por fecha (más recientes/más antiguos)

## Tecnologías Utilizadas

- **Spring Boot 3.x**: Framework base para el microservicio
- **Spring Security**: Para la seguridad y autorización
- **Firebase Authentication**: Para la verificación de tokens JWT
- **Firebase Firestore**: Como base de datos NoSQL para almacenar comentarios
- **Springdoc OpenAPI**: Para la documentación de la API (Swagger UI)
- **Java 17**: Versión del lenguaje utilizada
- **Maven**: Gestión de dependencias y construcción

## Estructura del Proyecto

```
src/main/java/com/reparalo/comment
├── config/           # Configuración de Spring, Firebase y Swagger
├── controller/       # Controladores REST
├── dto/              # Objetos de transferencia de datos
├── exception/        # Manejo de excepciones personalizado
├── model/            # Entidades del dominio
├── repository/       # Acceso a datos (Firestore)
├── security/         # Configuración de seguridad y filtros JWT
├── service/          # Lógica de negocio
└── CommentServiceApplication.java  # Punto de entrada
```

## Endpoints API

- `GET /api/comments/tutorial/{tutorialId}` - Obtener todos los comentarios de un tutorial
- `GET /api/comments/{id}` - Obtener un comentario específico
- `POST /api/comments` - Crear un nuevo comentario
- `PUT /api/comments/{id}` - Actualizar un comentario existente (solo autor)
- `DELETE /api/comments/{id}` - Eliminar un comentario (solo autor o admin)

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

4. El servicio estará disponible en: `http://localhost:8082`
5. Acceder a la documentación Swagger UI: `http://localhost:8082/swagger-ui.html`

## Seguridad

Todos los endpoints requieren autenticación mediante token JWT de Firebase, excepto los endpoints de lectura pública si se configuran así. La autorización se basa en roles y propiedad de los recursos:

- Solo el autor de un comentario puede editarlo o eliminarlo
- Los administradores pueden eliminar cualquier comentario
- Cualquier usuario autenticado puede crear comentarios

## Integración con Otros Servicios

Este microservicio se integra con:

- **Servicio de Autenticación**: Para validar tokens JWT y obtener información del usuario
- **Servicio de Tutoriales**: Para verificar la existencia de tutoriales antes de asociar comentarios
