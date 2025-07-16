```
FILE: README.MD
DESC: Instructions for Academic Events System

# ===================
# 1. PREREQUISITES
# ===================
# - Docker
# - Docker Compose

# ===================
# 2. ENVIRONMENT SETUP
# ===================

# The project is fully containerized. No local Java or PostgreSQL
# installation is required. The setup process involves building
# the Docker images and initializing the database volume.

# 1. Open a terminal in the project's root directory.
# 2. Execute the build command. This will download the base images
#    for PostgreSQL and OpenJDK, compile the Java source code,
#    and create the application image.

$ docker-compose build

# --- EXPECTED OUTPUT (TERMINAL) ---
# [+] Building X.Xs (or cached)
#  => [internal] load build definition from Dockerfile
#  ...
#  => => exporting to image
#  => => => writing image sha256:...
#  => => => naming to docker.io/library/academic-events-system-app
# [+] Done
# ------------------------------------

# ===================
# 3. EXECUTION
# ===================

# The system architecture requires two running services: the database (`db`)
# and the application (`app`). The standard execution procedure is to
# start the database in the background and then run the interactive
# application in the foreground.

# 1. Start the PostgreSQL database service in detached mode.
#    This command also creates the persistent volume `pgdata` on
#    its first run.

$ docker-compose up -d db

# --- EXPECTED OUTPUT (TERMINAL) ---
# [+] Running 1/1
#  ✔ Container academic_postgres  Started
# ------------------------------------

# 2. Run the application service. This command starts a new
#    container from the application image and attaches the
#    terminal for interactive use.

$ docker-compose run --rm app

# --- EXPECTED OUTPUT (TERMINAL) ---
# === Academic Events Management System ===
# Functional and Declarative
#
# [+] Database connection successful!
#
# === Main Menu ===
# 1. Event Management
# 2. Participant Management
# 3. Registration Management
# 4. Reports
# 5. Exit
#
# Enter your choice: _
# ------------------------------------

# The application is now running and awaiting user input.
# To exit the application, select option 5.

# ===================
# 4. SHUTDOWN AND CLEANUP
# ===================

# To stop all services and remove the containers and network:
$ docker-compose down

# To also remove the database volume (all data will be lost):
$ docker-compose down -v
```
