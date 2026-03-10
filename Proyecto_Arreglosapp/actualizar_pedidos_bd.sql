-- ACTUALIZACIÓN DE LA TABLA PEDIDOS (SINTAXIS ESTÁNDAR)
-- Este script añade las columnas necesarias para el seguimiento de pagos y entregas.

USE PROYECTO_ARREGLOSAPP;

-- Se añade cada columna de forma individual para evitar errores de sintaxis en versiones antiguas de MySQL.
-- Nota: Si la columna ya existe, MySQL mostrará un error (1060), puedes ignorarlo si es el caso.

ALTER TABLE pedidos ADD COLUMN pedido_pago_estado VARCHAR(50) DEFAULT 'pendiente';
ALTER TABLE pedidos ADD COLUMN pedido_entrega_estado VARCHAR(50) DEFAULT 'pendiente';
ALTER TABLE pedidos ADD COLUMN pedido_monto_abonado DECIMAL(10,2) DEFAULT 0.00;

-- Verificar los cambios realizados
DESCRIBE pedidos;
