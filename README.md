# ODYXS — Guía turística de Cartagena

Aplicación web de guía turística de Cartagena (lugares, eventos, actividades, reseñas, chatbot y panel de administración), construida con **Spring Boot 3.2 + Java 21 + Thymeleaf** y base de datos **MySQL/MariaDB**.

---

## 🧱 Stack

| Capa            | Tecnología                                  |
|-----------------|---------------------------------------------|
| Backend         | Spring Boot 3.2.5, Java 21                   |
| Vistas          | Thymeleaf + Spring Security (extras)         |
| Persistencia    | Spring Data JPA / Hibernate                  |
| Base de datos   | MariaDB 10.11 (compatible con MySQL 8)       |
| i18n            | Español / Inglés                             |
| Chatbot         | Google Gemini (opcional)                     |

---

## 🚀 Puesta en marcha

### 1. Requisitos

- Java 21
- Maven 3.9+
- Docker + Docker Compose (para la base de datos)

### 2. Configurar variables de entorno

```bash
cp .env.example .env
# edita .env y ajusta las contraseñas y (opcionalmente) tu GEMINI_API_KEY
```

### 3. Levantar la base de datos con Docker

La base de datos está contenerizada. El esquema y los datos iniciales
(`sql/odyxsvg_db.sql`) se cargan **automáticamente** la primera vez que se
crea el contenedor.

```bash
docker compose up -d
```

Comprobar que está lista:

```bash
docker compose ps          # STATUS debe decir "healthy"
docker compose logs -f db  # ver el arranque
```

Para reiniciar la base de datos desde cero (borra los datos):

```bash
docker compose down -v && docker compose up -d
```

> **¿Puerto 3306 ocupado?** Si ya tienes un MySQL/MariaDB local corriendo,
> detén ese servicio o usa otro puerto para el contenedor definiendo
> `DB_PORT` en tu `.env` (p. ej. `DB_PORT=3307`) y ajustando `DB_URL` al
> mismo puerto.
>
> **Fedora / RHEL (SELinux):** el montaje del `.sql` usa la etiqueta `:z`
> en `docker-compose.yml`, necesaria para que el contenedor pueda leerlo.

### 4. Arrancar la aplicación

```bash
./mvnw spring-boot:run       # o:  mvn spring-boot:run
```

La app queda disponible en 👉 **http://localhost:8080**

---

## 🔑 Credenciales por defecto

| Rol   | Correo             | Contraseña  |
|-------|--------------------|-------------|
| Admin | admin@odyxs.com    | admin2026   |

> El administrador se crea/migra automáticamente al arrancar
> (`UsuarioService.inicializarAdmin`). **Cambia esta contraseña en producción.**

---

## ⚙️ Configuración

Toda la configuración sensible se lee de variables de entorno con valores por
defecto pensados para desarrollo (ver `.env.example`):

| Variable          | Descripción                          | Default          |
|-------------------|--------------------------------------|------------------|
| `DB_URL`          | URL JDBC                             | localhost:3306   |
| `DB_USERNAME`     | Usuario de BD                        | `odyxs_user`     |
| `DB_PASSWORD`     | Contraseña de BD                     | `odyxs_pass`     |
| `GEMINI_API_KEY`  | API key de Gemini (chatbot, opcional)| *(vacío)*        |

---

## 📁 Estructura

```
src/main/java/com/odyxs/vg/
├── config/       Seguridad, i18n, recursos estáticos
├── controller/   Controladores MVC
├── entity/       Entidades JPA
├── repository/   Repositorios Spring Data
└── service/      Lógica de negocio
src/main/resources/
├── templates/    Vistas Thymeleaf
├── static/       CSS, imágenes
└── i18n/         Mensajes ES/EN
sql/              Esquema + datos iniciales
docker-compose.yml
```

---

## 🗺️ Roadmap / mejoras pendientes

Ver la sección "Puntos a mejorar" acordada con el equipo (seguridad, tests,
validación, etc.).
