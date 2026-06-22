alertaVecinal

Sistema de alerta vecinal basado en microservicios para la gestión de incidentes, patrullas, asignaciones y seguimiento operativo.

Tecnologías
Java 25
Spring Boot 3.5.15
Spring Cloud 2025.0.3
Maven
MySQL
Eureka Server
OpenFeign
Spring Security + JWT
RabbitMQ
Spring Cloud Gateway (WebMVC)
Lombok
OpenPDF
Arquitectura

El proyecto está compuesto por varios servicios independientes, cada uno con su propio Maven project. No existe un POM raíz.

Servicios
Servicio	Puerto	Base de datos	Dependencias
eureka-server	8761	—	—
auth-service	8083	auth_db	Eureka
incident-service	8081	incident_db	Eureka
serenasgo-service	8082	serenasgo_db	Eureka, auth-service, incident-service
api-gateway	8080	—	Eureka


Prefijo	Servicio destino
/api/auth/**	auth-service
/api/incidents/**	incident-service
/api/serenazgo/**	serenasgo-service
/api/patrullas/**	serenasgo-service
/api/asignaciones/**	serenasgo-service
/api/admin/**	serenasgo-service
Requisitos previos
Java 21
MySQL ejecutándose en localhost:3306
Usuario: root
Contraseña: 123456
Bases de datos:
auth_db
incident_db
serenasgo_db
Instalación y ejecución
Orden de arranque
eureka-server
auth-service
incident-service
serenasgo-service
api-gateway
Comandos

Dentro de cada carpeta de servicio:

./mvnw clean package
./mvnw test
./mvnw spring-boot:run
java -jar target/<name>-0.0.1-SNAPSHOT.jar
Autenticación y autorización

El sistema usa JWT con algoritmo HS256.

Secreto configurable: JWT_SECRET
Expiración configurable: JWT_EXPIRATION
Valor por defecto del secreto: alertaVecinalSecretClaveSuperSegura1234567890
Duración por defecto: 24 horas (86400000 ms)
Claims del JWT
sub: username
id: userId
rol: rol del usuario
Acceso público

Solo son públicos:

POST /api/auth/login
POST /api/auth/register

El resto de endpoints requiere token JWT válido.

Roles
ROLE_VECINO
ROLE_SERENAZGO
ROLE_SUPERVISOR
ROLE_ADMIN
Permisos generales
Rol	Permisos
VECINO	Crear incidentes y ver sus propios incidentes
SERENAZGO	Ver incidentes asignados y cambiar estados
SUPERVISOR	Ver todos los incidentes, asignar y reasignar patrullas, revisar historial
ADMIN	Acceso total a patrullas, usuarios, dashboard, reportes 
Módulos del sistema
auth-service

Gestiona usuarios, roles y supervisores.

Endpoints principales:

/api/auth/usuarios/**
/api/auth/supervisores/**
/api/auth/login
/api/auth/register
incident-service

Gestiona los incidentes y su historial.

Endpoints principales:

/api/incidents/**
serenasgo-service

Gestiona patrullas, asignaciones, historial operativo, comentarios, evidencias, dashboard y reportes.

Endpoints principales:

/api/patrullas/**
/api/asignaciones/**
/api/serenazgo/**
/api/admin/**
Estados del sistema
EstadoIncidente
PENDIENTE → ASIGNADO → EN_PROCESO → ATENDIDO → CERRADO
EstadoPatrulla
DISPONIBLE ⇄ ATENDIENDO | FUERA_DE_SERVICIO

Solo las patrullas en estado DISPONIBLE pueden recibir asignaciones.

Flujo operativo
El vecino se registra e inicia sesión.
Crea un incidente.
El supervisor asigna el incidente a una patrulla disponible.
La patrulla cambia el estado del incidente hasta cerrarlo.
Cuando el último incidente activo se cierra, la patrulla vuelve a DISPONIBLE.
Base de datos

Cada servicio usa su propia base de datos.

auth-service → auth_db
incident-service → incident_db
serenasgo-service → serenasgo_db
Tablas principales
auth_db
usuarios
incident_db
incidentes
serenasgo_db
patrullas
asignaciones_incidentes
historial_estados
comentarios_incidente
evidencias
Notas de persistencia
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
No se usan restricciones FK físicas entre servicios; las relaciones son lógicas.
Se aplica soft-delete en usuarios, incidentes y patrullas mediante el campo activo.
API resumida
auth-service
POST /api/auth/login
POST /api/auth/register
GET /api/auth/usuarios
GET /api/auth/usuarios/{id}
PUT /api/auth/usuarios/{id}/rol
PUT /api/auth/usuarios/{id}/bloquear
PUT /api/auth/usuarios/{id}/reset-password
POST /api/auth/supervisores
GET /api/auth/supervisores
incident-service
GET /api/incidents
GET /api/incidents/mis-incidentes
GET /api/incidents/{id}
POST /api/incidents
PUT /api/incidents/{id}
DELETE /api/incidents/{id}
serenasgo-service
POST /api/patrullas
GET /api/patrullas
GET /api/patrullas/{id}
PUT /api/patrullas/{id}
DELETE /api/patrullas/{id}
POST /api/asignaciones
GET /api/asignaciones
GET /api/asignaciones/{id}
PUT /api/asignaciones/{id}/reasignar
GET /api/serenazgo/incidentes
GET /api/serenazgo/mis-incidentes
GET /api/serenazgo/incidentes/{id}
PUT /api/serenazgo/incidentes/{id}/estado
GET /api/serenazgo/incidentes/{id}/historial
POST /api/serenazgo/incidentes/{id}/comentarios
GET /api/serenazgo/incidentes/{id}/comentarios
POST /api/serenazgo/incidentes/{id}/evidencias
GET /api/serenazgo/incidentes/{id}/evidencias
GET /api/admin/dashboard
GET /api/admin/reportes/incidentes
Comunicación entre servicios
OpenFeign

serenasgo-service se comunica con:

incident-service para leer y actualizar incidentes
auth-service para crear usuarios de patrulla
RabbitMQ

serenasgo-service publica eventos de negocio en RabbitMQ.

Eventos principales:

incidente.creado
incidente.asignado
incidente.actualizado
incidente.cerrado
Respuesta estándar

El proyecto usa un formato común de respuesta:

{
  "response": {},
  "error": {
    "statusCode": 400,
    "message": "mensaje de error",
    "dateError": "2026-06-19"
  }
}
Estructura general de paquetes
controller
service
entity
repository
dto
security
config
exception
client
enums
Características destacadas
Arquitectura basada en microservicios
Seguridad con JWT y control por roles
Integración entre servicios con OpenFeign
Gateway centralizado para las rutas
Eventos asincrónicos con RabbitMQ
Soft delete para entidades principales
Historial de cambios de estado
Carga y almacenamiento de evidencias
Generación de reportes PDF
