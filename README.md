# CartNova

CartNova is a Java Servlet/JSP storefront for buyers, sellers and administrators, backed by MySQL.

## Stack

- JDK 26, Maven, Jakarta Servlet 6 / JSP 3.1, Tomcat 10.1
- MySQL 8, Connector/J 9.7, JSTL 3, BCrypt password hashing

## Prerequisites

Install JDK 26, Maven 3.9+, MySQL 8 and Apache Tomcat 10.1. Configure `JAVA_HOME`; make sure `java` and `mvn` resolve in a new terminal. Maven is available in the detected environment at `C:\Users\Ambuj Pandey\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin`.

## Database setup

Run `src/main/resources/db/schema.sql` in MySQL Workbench to create the database and tables. Then run `src/main/resources/db/seed-data.sql` to load sample categories, users and products. The schema script drops and recreates the CartNova tables, so back up existing data before running it again.

The application supports a Tomcat JNDI datasource named `jdbc/CartNovaDB`; otherwise it uses JDBC properties. Set these JVM properties when launching Tomcat (replace the sample password):

```
-Ddb.url=jdbc:mysql://localhost:3306/cartnova?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
-Ddb.user=root
-Ddb.pass=your_mysql_password
```

For Tomcat JNDI, configure a `jdbc/CartNovaDB` resource in Tomcat's context with the same database URL and credentials. Never commit production database credentials.

## Build and deploy

From the project root:

```
mvn clean package
```

Copy `target/CartNova.war` to Tomcat's `webapps` directory, start Tomcat, then open `http://localhost:8080/CartNova/`.

## Sample accounts

The seed script documents demo accounts: `admin@cartnova.com`, `techseller@cartnova.com`, `fashionhub@cartnova.com`, `john.doe@gmail.com`, and `sarah.smith@gmail.com`. The demo password hashes in the seed file should be regenerated with the application's BCrypt utility before relying on those sample credentials; change or remove these accounts before exposing a deployment.

## Features

- Search, category/price filters, product details, reviews and wishlists
- Buyer registration/login, cart, checkout, order history and profile
- Seller catalog, inventory, order management and reports
- Admin dashboard and account status management
- Role based authorization, parameterized JDBC operations and BCrypt passwords

Online payment capture, image uploads, email notifications and CSRF tokens are not integrated; checkout records the selected payment method and creates the order in MySQL.
