-- Script para crear administrador por defecto
-- Contraseña: admin123
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) 
VALUES ('admin@arreglosapp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Administrador', 'Oficina Principal', 1);

-- Verificar que se creó correctamente
SELECT * FROM USUARIOS WHERE user_email = 'admin@arreglosapp.com';
