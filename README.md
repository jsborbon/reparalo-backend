# Reparalo Backend - Arquitectura de Microservicios

Este proyecto contiene el backend para la aplicación móvil "Reparalo. Repara en Casa", implementado como una arquitectura de microservicios utilizando Spring Boot y Firebase.

## Estructura del Proyecto

El backend está dividido en cuatro microservicios independientes:

1. **Servicio de Autenticación y Usuarios**

   - Verificación de JWT de Firebase
   - Gestión de perfiles de usuario desde Firestore
   - Manejo de roles (cliente, técnico, admin)

2. **Servicio de Tutoriales**

   - CRUD completo de tutoriales
   - Filtrado por categoría y dificultad
   - Restricciones de acceso basadas en roles

3. **Servicio de Comentarios**

   - CRUD de comentarios por tutorial
   - Validación de autoría
   - Requiere autenticación

4. **Servicio de Técnicos**
   - Gestión de perfiles profesionales
   - Filtros por especialidad y ubicación
   - Autogestión de perfiles para técnicos

## Tecnologías Utilizadas

- **Spring Boot 3.x**: Framework base para todos los microservicios
- **Spring Security**: Para la seguridad y autorización
- **Firebase Authentication**: Para la autenticación de usuarios
- **Firebase Firestore**: Como base de datos NoSQL
- **Springdoc OpenAPI**: Para la documentación de la API (Swagger UI)
- **Java 17**: Versión del lenguaje utilizada
- **Maven**: Gestión de dependencias y construcción

## Configuración y Ejecución

Cada microservicio puede ejecutarse de forma independiente. Consulta el README específico de cada servicio para obtener instrucciones detalladas sobre su configuración y ejecución.

## Comunicación entre Servicios

Los microservicios se comunican entre sí mediante API REST. En futuras versiones, se podría implementar comunicación basada en eventos o colas de mensajes.

## Documentación de la API

Cada microservicio expone su documentación Swagger UI en la ruta `/swagger-ui.html`.

## Seguridad

Todos los microservicios utilizan Firebase Authentication para la verificación de tokens JWT. La autorización basada en roles se implementa a nivel de cada servicio.
