# ms-franchises

Microservicio reactivo desarrollado con Spring Boot WebFlux y persistencia en Amazon DynamoDB, como parte de una prueba técnica.
El servicio permite administrar franquicias, sucursales y productos con stock.

Además, el flujo de CI/CD está completamente automatizado mediante GitHub Actions.
Cada vez que se realiza un commit en el repositorio:

 - Se compila el proyecto y se genera el JAR.

 - Se construye la imagen Docker utilizando el Dockerfile.

 - La imagen es publicada automáticamente en Docker Hub.

 - Finalmente, se realiza el despliegue automático en una instancia EC2 de AWS.

# Tecnologías utilizadas

- Java 17
- Spring Boot 3.4.4
- Spring WebFlux
- Postman
- AWS DynamoDB (persistence)
- Gradle
- Docker
- GitHub Actions (CI/CD)
- Mockito - Junit 5
- AWS EC2
- Arquitectura Hexagonal

# Estructura del proyecto
```text
ms-franchises
└── src
    └── main
        ├── java
        │   └── com
        │       └── seti
        │           └── ms
        │               └── franchises
        │                   ├── application
        │                   │   ├── usecase
        │                   │   │   └── FranchiseServiceImpl.java
        │                   │   └── port
        │                   │       ├── in
        │                   │       │   └── IFranchiseService.java
        │                   │       └── out
        │                   │           └── IFranchiseRepository.java
        │                   │
        │                   ├── domain
        │                   │   ├── model
        │                   │   │   ├── Franchise.java
        │                   │   │   ├── Branch.java
        │                   │   │   └── Product.java
        │                   │   └── exception
        │                   │       └── MyHandleException.java
        │                   │
        │                   ├── infrastructure
        │                   │   ├── config
        │                   │   │   └── DynamoDBConfig.java
        │                   │   └── db
        │                   │       └── dynamodb
        │                   │           └── DynamoFranchiseRepository.java
        │                   │
        │                   └── handler
        │                   │   └── FranchiseHandler.java
        │                   │
        │                   └── router
        │                       └── FranchiseRouter.java
        │
        └── resources
            └── application.properties

```
# Endpoints expuestos

- El servicio se encuentra desplegado en un servidor EC2 de AWS y está accesible públicamente en:

- Base URL: http://54.226.216.15:8080

Todos los endpoints deben ser consumidos a partir de esta URL base. Ejemplo completo:
GET http://54.226.216.15:8080/franchises/v1
```text
| Método | Ruta                                                                                          | Descripción                                           |
|--------|-----------------------------------------------------------------------------------------------|-------------------------------------------------------|
| GET    | /franchises/v1/{id}                                                                           | Obtener una franquicia por ID                         |
| POST   | /franchises/v1                                                                                | Crear una nueva franquicia                            |
| PUT    | /franchises/v1/{franchiseId}/update-name                                                      | Actualizar el nombre de una franquicia                |
| PUT    | /franchises/v1/{franchiseId}/branches/{branchName}/update-name                                | Actualizar el nombre de una sucursal                  |
| POST   | /franchises/v1/{franchiseId}/branches/{branchName}/products                                   | Agregar un producto con stock a una sucursal          |
| DELETE | /franchises/v1/{franchiseId}/branches/{branchName}/products/{productName}                     | Eliminar un producto de una sucursal                  |
| PUT    | /franchises/v1/{franchiseId}/branches/{branchName}/products/{productName}/stock               | Actualizar el stock de un producto en una sucursal    |
| GET    | /franchises/v1/{franchiseId}/top-stock-products                                               | Obtener el producto con más stock por sucursal        |
```
# Ejemplo de servicio con consumo hacia AWS
![Customer with postman to AWS.png](img-doc/Customer%20with%20postman%20to%20AWS.png)

# Deploy del proyecto
Para crear el artefacto o el jar debe ejecutar el sigueitne comando en la raiz del proyecto de prueba_Seti
```bash
./gradlew bootJar
```
![Execute bootJar.png](img-doc/Execute%20bootJar.png)

# Construccion imagen
Para crear la imagen Docker basada en este proyecto, ejecutá el siguiente comando. Este proceso construirá la imagen 
utilizando el Dockerfile presente en la raíz del proyecto

```bash
docker build -t img_franchise_webflux:V1 .
```
![creation Dockerimg.png](img-doc/creation%20Dockerimg.png)

# Construccion del contenedor
Para crear del contenedor de Docker basada en este proyecto, ejecutá el siguiente comando. Este proceso construirá 
el contenedor:

```bash
docker run -d --name ms-franchises-container -p 8080:8080 img_franchise_webflux:V1
```
![Creation container img.png](img-doc/Creation%20container%20img.png)

Para validar que el contenedor se creo correcto por favor validar en dockerDesktop
que se encuentre en estado activo (luz verde)

![Docker active.png](img-doc/Docker%20active.png)

# Prueba en postman
Para realizar la validacion de que los servicios se encuetran activos se debe realizar la peticion desde postman
```bash
http://localhost:8080/franchises/v1/franchise-002
```
![Test postman.png](img-doc/Test%20postman.png)

# Crear contener con imagen de Docker Hup
Si deseas crear el contenedor utilizando la imagen publicada en Docker Hub, puedes hacerlo con la siguiente imagen: ingflorez940315/img_franchise_webflux:V1
```bash
docker run -d --name ms-franchises-container -p 8080:8080 ingflorez940315/img_franchise_webflux:V1
```