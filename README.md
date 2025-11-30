MoneyTracker - Control de Gastos Personales

MoneyTracker es una aplicación nativa de Android desarrollada como proyecto bimestral para la materia de Aplicaciones Móviles. Su objetivo es ayudarte a gestionar tus finanzas personales de manera eficiente, registrando ingresos y gastos, controlando tu presupuesto mensual con alertas visuales y visualizando estadísticas claras.

La aplicación funciona bajo un modelo "Offline-First", garantizando que tus datos estén siempre disponibles en tu dispositivo, integrando conectividad únicamente para funciones avanzadas como la conversión de divisas en tiempo real.

🧑‍💻 Autor

Jostin Damian Quilca Portilla

Carrera: Ingeniería de Software

Universidad: Universidad Técnica del Norte

Materia: Desarrollo de Aplicaciones Móviles

📱 Funcionalidades Principales

Gestión de Transacciones (CRUD): Registra, edita y elimina tus movimientos financieros fácilmente.

Control de Presupuesto: Establece un límite mensual y recibe alertas visuales (Semáforo: Verde, Amarillo, Rojo) según tu nivel de gasto.

Conversión de Divisas: Consulta el valor de tus gastos en otras monedas usando tasas de cambio en tiempo real (API REST).

Estadísticas Visuales: Gráficos de pastel interactivos para entender en qué categorías gastas más.

Filtrado Inteligente: Visualiza tus movimientos por tipo (Ingresos/Gastos) o por fecha de inicio.

Base de Datos Local: Todos tus datos se guardan de forma segura en tu dispositivo usando SQLite.

🛠️ Tecnologías y Herramientas

Este proyecto fue desarrollado utilizando las mejores prácticas y estándares modernos de desarrollo Android:

Lenguaje: Java (JDK 11+)

Entorno: Android Studio Ladybug/Koala

Persistencia de Datos: * SQLite (Base de datos relacional nativa)

SharedPreferences (Configuración de usuario)

Conectividad y Red:

Retrofit 2 (Cliente HTTP seguro y eficiente)

GSON (Parseo automático de JSON)

Gráficos:

MPAndroidChart (Visualización de datos)

Interfaz de Usuario:

XML Layouts

Material Design Components (Cards, Floating Action Buttons)

📸 Capturas de Pantalla

(Aquí puedes subir tus imágenes a la carpeta del repo y enlazarlas, o borrar esta sección si no tienes las imágenes listas)

Dashboard Principal

Formulario de Registro

Estadísticas

<img src="screenshots/dashboard.png" width="200"/>

<img src="screenshots/form.png" width="200"/>

<img src="screenshots/stats.png" width="200"/>

🚀 Instalación y Uso

Clonar el repositorio:

git clone [https://github.com/JostinQuilca/MoneyTracker.git](https://github.com/JostinQuilca/MoneyTracker.git)


Abrir en Android Studio:

Abre Android Studio y selecciona "Open an existing Android Studio project".

Navega a la carpeta donde clonaste el repositorio.

Sincronizar Gradle:

Espera a que Android Studio descargue las dependencias necesarias.

Ejecutar:

Conecta tu dispositivo Android (con Depuración USB activa) o usa un Emulador.

Presiona el botón Run (Play).

