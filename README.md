# StoreMates API

**Backend API REST para una tienda llamada StoreMates, desarrollada con Spring Boot y organizada bajo Feature-Based Architecture. Incluye CRUD de productos y gestión interna, paginación, validaciones y manejo centralizado de excepciones.**

## 🚀 Tecnologías utilizadas
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
---
## 📁 Arquitectura del proyecto

**Feature-Based Architecture (Package by Feature)**  
Cada módulo contiene su propio `Controller`, `Service`, `Repository`, `Entity` y `DTO`.


---

## 📦 Endpoints principales

### 🛒 Productos
| Método | Endpoint               | Descripción               |
|--------|-------------------------|---------------------------|
| GET    | `/products`        | Listar productos (paginado) |
| GET    | `/products/{id}`   | Obtener un producto        |
| POST   | `/products`        | Crear un producto          |
| PUT    | `//products/{id}`   | Actualizar un producto     |
| DELETE | `/products/{id}`   | Eliminar un producto       |

---

## ⚙️ Cómo ejecutar el proyecto

1. Clonar el repositorio
   ```bash
   git clone https://github.com/gusmunoz1221/storemates-api.git