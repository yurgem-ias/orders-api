ORDERS API

Api Rest para la gestión de órdenes de compra, desarrollada con principios de programacion.

Cómo ejecutrar el proyecto

En el entorno local la aplicacion se levanta de la siguiente manera:
./gradlew bootRun

Las pruebas unitarias que validan el dominio puro y los casos de uso:
./gradlew test


Arquitectura y Decisiones Técnicas

el proyecto sigue una arquitectura limpia, protegiendo las reglas del negocio de cualquier detalle tecnologico externo.

com.orders
----domain
        -exception
        -model
        -port
        -usecase
----infrastructure
        -adapter  
            -in.web
            -out.persistence
        -config

El dominio es totalmente independiente del framework, aqui residen entidades como Order,Product, se utilizo programacion fncional "ProductCatalog" para transformar y filtrar datos sin efectos secundarios.

El caso de uso orquesta la lógica. verifica reglas, calcula el total inyectando funciones puras y delega la persistencia.

Lopes puertos IN & OUT, el puerto de entrada "CreateOrderUseCase" y el puerto de salida "OrderRepositoryPort"

Adaptadores Web "OrderController" traduce las peticiones http de json a modelos de dominio.
            persistence "InMemoryOrderRepositoryAdapter" implementa el almacenamiento guardando en memoria

Inversion de dependencias (DIP) el caso de uso jamás conoce el adaptador de persistencia. la inyectccion se hace manualmente a través de la clase "UseCaseConfig" in infrastructure, evitando propagar la anotación de service al dominio.

