-- Habilitar la extensión PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;

-- 1. TABLA DE USUARIOS
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    ubicacion GEOGRAPHY(POINT, 4326) NOT NULL, -- ubicación geoespacial
    creado_en TIMESTAMP DEFAULT NOW()
);

-- 2. TABLA DE SECTORES
CREATE TABLE sector (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    ubicacion GEOGRAPHY(POINT, 4326) NOT NULL -- punto representativo del sector
);

-- 3. TABLA DE TAREAS

CREATE TABLE tarea (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_vencimiento DATE,
    completada BOOLEAN DEFAULT FALSE,
    id_usuario INT REFERENCES usuario(id) ON DELETE CASCADE,
    id_sector INT REFERENCES sector(id) ON DELETE SET NULL,
    ubicacion GEOGRAPHY(POINT, 4326) NOT NULL, -- ubicación espacial de la tarea
    creada_en TIMESTAMP DEFAULT NOW()
);


-- 4. INDICES ESPACIALES
CREATE INDEX idx_usuario_ubicacion ON usuario USING GIST (ubicacion);
CREATE INDEX idx_sector_ubicacion ON sector USING GIST (ubicacion);
CREATE INDEX idx_tarea_ubicacion ON tarea USING GIST (ubicacion);

-- 5. VISTAS ÚTILES PARA CONSULTAS
-- Tareas completadas por sector y usuario
CREATE VIEW vista_tareas_por_sector AS
SELECT 
    u.username,
    s.nombre AS sector,
    COUNT(t.id) AS tareas_completadas
FROM tarea t
JOIN usuario u ON t.id_usuario = u.id
JOIN sector s ON t.id_sector = s.id
WHERE t.completada = TRUE
GROUP BY u.username, s.nombre;
