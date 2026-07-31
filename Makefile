.PHONY: up down logs build clean reset up-postgres up-redis up-api up-engine up-web up-studio

# Levanta toda la infraestructura en modo 'detached'
up:
	docker-compose up -d

# Levantar servicios individuales
up-postgres:
	docker-compose up -d postgres

up-redis:
	docker-compose up -d redis

up-api:
	docker-compose up -d api-core

up-engine:
	docker-compose up -d game-engine

up-web:
	docker-compose up -d cliente-web

up-studio:
	docker-compose up -d studio

# Detiene y elimina los contenedores
down:
	docker-compose down

# Reconstruye las imágenes (útil cuando instalas nuevas dependencias en package.json)
build:
	docker-compose build

# Muestra los logs en tiempo real de todos los servicios
logs:
	docker-compose logs -f

# Borra todo, incluyendo volúmenes de base de datos (¡Precaución! Borrará los datos de Postgres locales)
clean:
	docker-compose down -v

# Reinicia el entorno limpio (borra volúmenes y reconstruye)
reset: clean build up
