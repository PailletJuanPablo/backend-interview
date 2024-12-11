# Desarrollo de Backoffice para un eCommerce

Estás participando en el desarrollo del backoffice de un eCommerce. El equipo está trabajando en la implementación de un sistema de categorías para organizar y clasificar los productos del catálogo.

## Estructura de las Categorías

Las categorías se estructuran jerárquicamente en un esquema tipo árbol:

- **Categoría padre**: Puede tener múltiples subcategorías.
- **Subcategorías**: Pertenecen a una única categoría padre y pueden tener sus propias subcategorías.

### Ejemplos

- Bebidas > Cervezas
- Almacén > Panificados > Pan de Molde

Además, las subcategorías poseen un atributo adicional llamado `active`, que indica si están activas o no, permitiendo controlar la visibilidad de sus productos dentro del catálogo.

## Requerimientos

Implementar los endpoints necesarios para realizar las siguientes operaciones utilizando **JPA**:

1. **Crear una nueva categoría raíz** (sin categoría padre).
2. **Crear una subcategoría** bajo una categoría existente, inicializando el atributo `active` como `true` por defecto.
3. **Obtener todos los ancestros y descendientes** de una subcategoría específica.
4. **Actualizar el estado** (`active`) de una subcategoría específica.
5. **Eliminar una categoría o subcategoría**. Si una categoría tiene subcategorías, estas deben eliminarse en cascada.

## Validaciones

1. **Nombre único por nivel**: No debe haber categorías con el mismo nombre bajo el mismo padre.
2. **Relación padre-hijo válida**: Una categoría no puede ser su propio padre.
3. **Configuración de `active`**: El atributo `active` solo debe ser configurable en subcategorías (no en categorías raíz).
