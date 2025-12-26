CREATE EXTENSION IF NOT EXISTS pgcrypto;

TRUNCATE TABLE tarea RESTART IDENTITY CASCADE;
TRUNCATE TABLE usuario RESTART IDENTITY CASCADE;
TRUNCATE TABLE sector RESTART IDENTITY CASCADE;

-- Inserta usuarios
INSERT INTO usuario (username, password_hash, ubicacion, posicion_tiempo_real)
VALUES
('juan', crypt('1234', gen_salt('bf')),
 ST_GeogFromText('SRID=4326;POINT(-70.6483 -33.4569)'),
 ST_GeogFromText('SRID=4326;POINT(-70.6475 -33.4572)')),

('maria', crypt('abcd', gen_salt('bf')),
 ST_GeogFromText('SRID=4326;POINT(-71.54297 -35.6751)'),
 ST_GeogFromText('SRID=4326;POINT(-71.5400 -35.6745)')),

('pedro', crypt('pass', gen_salt('bf')),
 ST_GeogFromText('SRID=4326;POINT(-72.5984 -38.7359)'),
 ST_GeogFromText('SRID=4326;POINT(-72.6002 -38.7348)')),

('ana', crypt('clave', gen_salt('bf')),
 ST_GeogFromText('SRID=4326;POINT(-73.1167 -36.8333)'),
 ST_GeogFromText('SRID=4326;POINT(-73.1150 -36.8325)')),

('carla', crypt('hola', gen_salt('bf')),
 ST_GeogFromText('SRID=4326;POINT(-70.6745 -33.4691)'),
 ST_GeogFromText('SRID=4326;POINT(-70.6730 -33.4680)'));

INSERT INTO usuario (username, password_hash, ubicacion, posicion_tiempo_real)
VALUES (
  'usuariotesteo',
  crypt('prueba123', gen_salt('bf')),
  ST_GeogFromText('SRID=4326;POINT(-70.6400 -33.4550)'),
  ST_GeogFromText('SRID=4326;POINT(-70.6390 -33.4540)')
);

-- Inserta sectores
INSERT INTO sector (nombre, descripcion, ubicacion)
VALUES
('Centro Santiago', 'Sector céntrico con alta densidad comercial y oficinas',
 ST_GeogFromText('SRID=4326;POINT(-70.6500 -33.4500)')),

('Las Condes', 'Zona residencial y empresarial',
 ST_GeogFromText('SRID=4326;POINT(-70.567 -33.416)')),

('San Miguel', 'Barrio mixto residencial-comercial',
 ST_GeogFromText('SRID=4326;POINT(-70.645 -33.485)')),

('Talca Centro', 'Área urbana con universidades y servicios',
 ST_GeogFromText('SRID=4326;POINT(-71.6667 -35.4333)')),

('Temuco Norte', 'Zona residencial en expansión',
 ST_GeogFromText('SRID=4326;POINT(-72.6000 -38.7000)')),

('Concepción Centro', 'Corazón de la ciudad universitaria y comercial',
 ST_GeogFromText('SRID=4326;POINT(-73.0500 -36.8200)'));




--Inserta categorías

INSERT INTO categoria (nombre, descripcion)
VALUES
('Mantenimiento', 'Tareas relacionadas con el mantenimiento de equipos e infraestructuras'),
('Inspección', 'Tareas de inspección y verificación de sistemas y áreas'),
('Instalación', 'Tareas de instalación de nuevos equipos o sistemas'),
('Monitoreo', 'Tareas de monitoreo ambiental y de seguridad'),
('Construcción', 'Tareas relacionadas con obras y construcción'),
('Planeamiento', 'Tareas de planificación y evaluación de proyectos');



-- Inserta tareas
INSERT INTO tarea (titulo, descripcion, fecha_vencimiento, completada, id_usuario, id_sector, ubicacion)
VALUES
('Revisar sensores', 'Verificar sensores ambientales instalados en el sector',
 '2025-11-10', FALSE, 1, 1,
 ST_GeogFromText('SRID=4326;POINT(-70.649 -33.447)')),

('Actualizar software', 'Actualizar el sistema de monitoreo de calidad de aire',
 '2025-11-08', TRUE, 1, 1,
 ST_GeogFromText('SRID=4326;POINT(-70.651 -33.453)')),

('Inspección de cámaras', 'Controlar funcionamiento de cámaras de seguridad',
 '2025-11-12', FALSE, 1, 2,
 ST_GeogFromText('SRID=4326;POINT(-70.565 -33.418)')),

('Instalar panel solar', 'Montaje e instalación de sistema solar',
 '2025-11-20', TRUE, 2, 4,
 ST_GeogFromText('SRID=4326;POINT(-71.665 -35.433)')),

('Mantención eléctrica', 'Chequeo de transformadores y red interna',
 '2025-11-18', FALSE, 2, 4,
 ST_GeogFromText('SRID=4326;POINT(-71.660 -35.435)')),

('Revisión cámaras', 'Comprobación del sistema de seguridad en Temuco Norte',
 '2025-11-09', TRUE, 3, 5,
 ST_GeogFromText('SRID=4326;POINT(-72.601 -38.710)')),

('Relevar nuevas áreas', 'Mapeo con dron de expansión urbana',
 '2025-11-15', FALSE, 3, 5,
 ST_GeogFromText('SRID=4326;POINT(-72.602 -38.720)')),

('Control de residuos', 'Evaluar puntos de recolección de residuos',
 '2025-11-11', TRUE, 4, 6,
 ST_GeogFromText('SRID=4326;POINT(-73.053 -36.821)')),

('Inspección alumbrado', 'Verificar postes y luminarias defectuosas',
 '2025-11-13', FALSE, 4, 6,
 ST_GeogFromText('SRID=4326;POINT(-73.048 -36.824)')),

('Reporte de ruido', 'Analizar niveles de ruido nocturno',
 '2025-11-07', TRUE, 5, 2,
 ST_GeogFromText('SRID=4326;POINT(-70.670 -33.420)')),

('Evaluar tránsito', 'Medir flujo vehicular en avenida principal',
 '2025-11-09', FALSE, 5, 1,
 ST_GeogFromText('SRID=4326;POINT(-70.660 -33.445)'));

INSERT INTO tarea (titulo, descripcion, fecha_vencimiento, completada, id_usuario, id_sector, ubicacion)
VALUES
('Verificar luminarias', 'Revisión de luminarias en sector asignado',
 '2025-11-14', FALSE, 6, 1,
 ST_GeogFromText('SRID=4326;POINT(-70.6410 -33.4560)')),

('Actualizar informe diario', 'Carga de datos recopilados durante la jornada',
 '2025-11-15', FALSE, 6, 1,
 ST_GeogFromText('SRID=4326;POINT(-70.6420 -33.4570)')),

('Inspección sensores', 'Validación de sensores ambientales operativos',
 '2025-11-10', TRUE, 6, 2,
 ST_GeogFromText('SRID=4326;POINT(-70.5680 -33.4170)')),

('Monitoreo ruido', 'Medición de niveles de ruido en horario nocturno',
 '2025-11-09', TRUE, 6, 2,
 ST_GeogFromText('SRID=4326;POINT(-70.5690 -33.4180)'));
-- Fin de los datos de carga

