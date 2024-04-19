# currency-exchange-rate
Currency Exchange Rate Application
This is a Spring Boot application written in Java (version 17) with Gradle as the build tool. The purpose of this application is to provide a REST API where clients can perform various currency-related actions, including retrieving a list of currencies used in the project, getting exchange rates for a specific currency, and adding a new currency to fetch exchange rates.

Features
Retrieve List of Currencies: Clients can retrieve a list of currencies used in the project.
Get Exchange Rates: Clients can fetch exchange rates for a specific currency.
Add New Currency: Clients can add a new currency to the system to fetch exchange rates for it.

Installation
Clone the repository:
git clone https://github.com/your-username/currency-exchange-rate.git

Navigate to the project directory:
cd currency-exchange-rate-app

Build the project using Gradle:
./gradlew buil
Run the application:
java -jar build/libs/currency-exchange-rate.jar

Optionally, run the application along with PostgreSQL using Docker:
docker-compose up -d

API Documentation
The API is documented using Swagger/OpenAPI Specification. You can access the API documentation at http://localhost:8080/swagger-ui.html.
