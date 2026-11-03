# ShopEase

ShopEase is a desktop storefront demo built with JavaFX and Swing. It includes a customer storefront, product catalog, shopping cart, and a separate admin interface.

## Requirements

- JDK 21 or newer
- Maven 3.9 or newer

## Run

From the project root:

```shell
mvn clean javafx:run
```

The first launch downloads the JavaFX and Maven dependencies. Use the **Admin** button and create a local admin account from the sign-up screen; account data is stored in `data/admins.txt` and is intentionally excluded from Git.

## Project layout

```text
data/                             Seed product catalog
resources/com/example/lavenderstore/ JavaFX views, styles, and images
src/ShopEase/                     Catalog, cart, order, and Swing admin code
src/com/example/lavenderstore/    JavaFX storefront and controllers
pom.xml                           Maven build and JavaFX run configuration
```

Local admin accounts and order history are runtime data and should not be committed. The `ShopEase/` directory at the project root is an outdated duplicate snapshot and is excluded; the current application sources are under `src/`.
