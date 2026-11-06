# Lab 8 - GTICS

## Requisitos
- Docker Desktop (o Docker con `docker compose`)

## Levantar
En la carpeta del proyecto:

```bash
docker compose up --build
```

La primera vez MySQL carga `Lab5_script.sql` solo. Cuando aparezca `Started Lab5Application`, entrar a:

http://localhost:8080

## Usuarios de prueba (contraseña: `password`)
| Rol   | Correo                       |
|-------|------------------------------|
| admin | fermin.shanahan@example.com  |
| user  | nolan.elena@example.net      |

## Apagar
```bash
docker compose down        # apaga y conserva los datos
docker compose down -v     # apaga y borra la BD (el script se vuelve a cargar al levantar)
```

MySQL queda expuesto en `localhost:3307` (usuario `root`, contraseña `root`).
