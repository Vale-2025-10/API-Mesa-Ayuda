# Helpdesk API REST con SLA y JWT

API REST para la gestión de tickets de soporte técnico con cálculo automático de SLA y autorización basada en roles (RBAC).

## Stack Tecnológico
- Java 17
- Spring Boot 3
- Spring Security + JWT (Access Token + Refresh Token)
- Spring Data JPA + H2 Database

## Estrategia de Refresh Token (Opción A)
Se seleccionó la **Opción A (Persistencia en Base de Datos)** para almacenar los `RefreshToken`.
- **Razón:** Permite invalidar/revocar tokens activamente durante el `/logout`, ofreciendo un control de sesión seguro del lado del servidor.

## Reglas de Negocio (SLA)
El cálculo de `slaVenceEn` se realiza en el servidor según la prioridad:
- **ALTA:** 4 Horas
- **MEDIA:** 24 Horas
- **BAJA:** 72 Horas
