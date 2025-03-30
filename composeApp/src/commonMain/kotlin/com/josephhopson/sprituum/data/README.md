# Data Layer

This layer is responsible for handling data operations such as fetching data from local sources, caching, and data transformation. It provides data to either the Domain Layer or the ViewModels through repositories.

## Structure

- **model**: Data transfer objects (DTOs) and database entities 
- **repository**: Repository implementations that provide data access to the domain layer
- **source**: Data sources such as local database and preference storage

## Responsibilities

- Converting DTOs to domain entities
- Handling data persistence operations
- Managing caching strategies
- Isolating data sources from the domain layer
