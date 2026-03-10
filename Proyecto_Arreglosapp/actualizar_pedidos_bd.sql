-- ACTUALIZACIÓN DE LAS TABLAS PEDIDOS Y CITAS (SINTAXIS ESTÁNDAR)
-- Este script añade las columnas necesarias para el seguimiento de pagos, entregas y asistencia.

USE PROYECTO_ARREGLOSAPP;

-- ACTUALIZACIÓN DE TABLA PEDIDOS
-- Se añade cada columna de forma individual. Si ya existen, MySQL dará error 1060 (puedes ignorarlo).
ALTER TABLE pedidos ADD COLUMN pedido_pago_estado VARCHAR(50) DEFAULT 'pendiente';
ALTER TABLE pedidos ADD COLUMN pedido_entrega_estado VARCHAR(50) DEFAULT 'pendiente';
ALTER TABLE pedidos ADD COLUMN pedido_monto_abonado DECIMAL(10,2) DEFAULT 0.00;

-- ACTUALIZACIÓN DE TABLA CITAS
-- Se añade la columna de asistencia para gestión administrativa.
ALTER TABLE citas ADD COLUMN cita_asistencia VARCHAR(50) DEFAULT 'pendiente';

-- Verificar los cambios realizados
DESCRIBE pedidos;
DESCRIBE citas;
