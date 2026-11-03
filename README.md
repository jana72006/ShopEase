# ShopEase

ShopEase is a feel-good finds storefront in two formats: a browser-based demo and the original JavaFX/Swing desktop application.

## Try the web app

Open the live GitHub Pages site: **https://jana72006.github.io/ShopEase/**

The web edition includes product search and category filters, a shopping bag, demo checkout, and a demo inventory/order dashboard. It is a static front-end with no server or payment processing:

- Cart, demo orders, and inventory edits are saved only in the current browser's local storage.
- The demo admin page has no secure sign-in. Changes are private to that browser and are not shared with other visitors.
- Checkout does not collect payment details or create a real order.

## Run the web app locally

Requirements: Node.js 22 or newer and npm.

```shell
cd web
npm ci
npm run dev
```

To create and preview a production build:

```shell
npm run build
npm run preview
```

## Run the desktop app

Requirements: JDK 21 or newer and Maven 3.9 or newer.

```shell
mvn clean javafx:run
```

## Project layout

```text
web/                              React storefront, styles, and bundled catalog
src/ShopEase/                     Desktop catalog, cart, order, and Swing admin code
src/com/example/lavenderstore/    JavaFX desktop storefront and controllers
resources/com/example/lavenderstore/ Desktop views, styles, and images
data/products.txt                 Seed catalog for the desktop app
pom.xml                           Maven desktop build
.github/workflows/                Java CI and GitHub Pages deployment
```

The `ShopEase/` directory at the project root is an outdated duplicate source snapshot and is excluded. Local admin credentials and desktop order history are runtime data and are not committed.
