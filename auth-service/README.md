# Servicio de Autenticación y Usuarios - Reparalo

Este microservicio gestiona la autenticación y los perfiles de usuario para la aplicación Reparalo, utilizando Firebase Authentication y Firestore.

## Características

- Verificación de tokens JWT de Firebase
- Gestión de perfiles de usuario en Firestore
- Manejo de roles (cliente, técnico, admin) mediante Custom Claims
- API REST segura con Spring Security
- Documentación con Swagger UI

## Requisitos Previos

- Java 17 o superior
- Maven
- Cuenta de Firebase con Authentication y Firestore habilitados

## Configuración de Firebase

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Habilita Authentication y Firestore
3. En la configuración del proyecto, ve a "Cuentas de servicio"
4. Genera una nueva clave privada (esto descargará un archivo JSON)
5. Renombra el archivo descargado a `serviceAccountKey.json`
6. Coloca este archivo en `src/main/resources/`

## Estructura del Proyecto

```
src/main/java/com/reparalo/authservice/
├── AuthServiceApplication.java       # Clase principal
├── config/                           # Configuraciones
│   ├── FirebaseConfig.java           # Configuración de Firebase
│   └── SecurityConfig.java           # Configuración de seguridad
├── controller/                       # Controladores REST
│   └── UsuarioController.java        # API de usuarios
├── dto/                              # Objetos de transferencia de datos
│   └── UsuarioDTO.java               # DTO para usuarios
├── model/                            # Modelos de datos
│   └── Usuario.java                  # Entidad Usuario
├── security/                         # Componentes de seguridad
│   └── FirebaseAuthenticationFilter.java  # Filtro JWT
└── service/                          # Servicios
    └── UsuarioService.java           # Servicio de usuarios
```

## Compilación y Ejecución

```bash
# Compilar el proyecto
mvn clean package

# Ejecutar el servicio
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

Alternativamente, puedes ejecutarlo directamente con Maven:

```bash
mvn spring-boot:run
```

El servicio estará disponible en `http://localhost:8081`

## Documentación de la API

La documentación Swagger UI está disponible en:

```
http://localhost:8081/swagger-ui.html
```

## Endpoints Principales

### Públicos

- `GET /api/v1/auth/public/verificar` - Verificar estado del servicio

### Protegidos (requieren token JWT)

- `GET /api/v1/auth/perfil` - Obtener perfil del usuario autenticado
- `POST /api/v1/auth/perfil` - Crear o actualizar perfil de usuario
- `PUT /api/v1/auth/perfil` - Actualizar perfil de usuario

### Solo Administradores

- `GET /api/v1/auth/admin/usuarios/{uid}` - Obtener usuario por UID
- `PUT /api/v1/auth/admin/usuarios/{uid}/rol` - Actualizar rol de usuario
- `DELETE /api/v1/auth/admin/usuarios/{uid}` - Eliminar usuario

## Pruebas con Postman

1. Configura una colección en Postman para el servicio
2. Para endpoints protegidos, necesitas incluir un token JWT válido de Firebase:
   - En la pestaña "Authorization", selecciona tipo "Bearer Token"
   - Ingresa el token JWT obtenido de Firebase Authentication

### Obtener un Token JWT para Pruebas

Puedes obtener un token JWT válido de Firebase de varias formas:

1. **Desde tu aplicación móvil**: Inicia sesión y extrae el token
2. **Usando Firebase Admin SDK**: Genera un token personalizado
3. **Usando Firebase REST API**: Autentica con email/password y obtén el token

## Integración con Aplicaciones Android

Para consumir este servicio desde una aplicación Android:

1. Configura Firebase Authentication en tu app Android
2. Cuando el usuario inicie sesión, obtén el token ID:

```kotlin
FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
    val idToken = result.token
    // Usar este token en las llamadas a la API
}
```

3. Incluye el token en las solicitudes HTTP:

```kotlin
// Usando Retrofit
@GET("api/v1/auth/perfil")
fun obtenerPerfil(@Header("Authorization") token: String): Call<Usuario>

// Llamada
api.obtenerPerfil("Bearer $idToken").enqueue(callback)
```

## Manejo de Errores

El servicio devuelve códigos de estado HTTP estándar:

- 200 OK: Operación exitosa
- 201 Created: Recurso creado exitosamente
- 400 Bad Request: Solicitud inválida
- 401 Unauthorized: Token inválido o expirado
- 403 Forbidden: Sin permisos suficientes
- 404 Not Found: Recurso no encontrado
- 500 Internal Server Error: Error del servidor

Los errores incluyen un mensaje descriptivo en formato JSON.
