-- ACTUALIZAR TABLA FAVORITOS PARA QUE SEA DINÁMICA
-- ================================================

-- 1. Eliminar datos estáticos si existen
DELETE FROM FAVORITOS WHERE user_id = 2;

-- 2. Verificar estructura actual de la tabla
DESCRIBE FAVORITOS;

-- 3. Si la tabla no existe o tiene estructura incorrecta, recrearla
DROP TABLE IF EXISTS FAVORITOS;

CREATE TABLE FAVORITOS (
    favorito_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    arreglo_id INT NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    nombre_categoria VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2),
    imagen_url VARCHAR(255),
    cantidad INT DEFAULT 1,
    fecha_agregado DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USUARIOS(user_id) ON DELETE CASCADE,
    FOREIGN KEY (arreglo_id) REFERENCES ARREGLOS(arreglo_id) ON DELETE CASCADE,
    UNIQUE KEY unique_favorito (user_id, arreglo_id)
);

-- 4. Verificar que los arreglos existan
SELECT * FROM ARREGLOS;

-- 5. Insertar arreglos de ejemplo si no existen
INSERT IGNORE INTO ARREGLOS (arreglo_id, categoria_id, arreglo_nombre, arreglo_precio_base, arreglo_descripcion, arreglo_imagen_url, arreglo_tiempo_estimado) VALUES
(1, 1, 'Sastrería y Dobladillos', 25000.00, 'Trabajos profesionales de sastrería y dobladillos manuales', '../../Assets/image/imagen-sastreria.jpg', '5-7 días'),
(2, 2, 'Costuras Generales', 15000.00, 'Reparaciones y costuras básicas', '../../Assets/image/imagen-costura.jpg', '3-5 días'),
(3, 3, 'Planchado Profesional', 8000.00, 'Planchado de prendas delicadas', '../../Assets/image/imagen-planchado.jpg', '1-2 días'),
(4, 4, 'Arreglo de Medidas y Ajustes', 15000.00, 'Ajustes de medidas y arreglos generales', '../../Assets/image/imagen-arreglos-de-vestidos-de-fiesta.jpg', '4-6 días'),
(5, 5, 'Ajuste Fino de Vestidos y Ropa', 20000.00, 'Ajustes precisos en vestidos finos y ropa delicada', '../../Assets/image/imagen-vestidos-finos.jpg', '5-7 días');

-- 6. Verificar la tabla final
SELECT * FROM FAVORITOS;

-- 7. Verificar que no haya datos estáticos
SELECT COUNT(*) as total_favoritos FROM FAVORITOS;
