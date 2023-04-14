# 9gag-ITT
ITT Final project

## Configuration

The `config/application.properties.template` file contains all the configuration options required by the application, but with placeholders for sensitive information. To configure the application, follow these steps:

1. Copy `config/application.properties.template` to `config/application.properties`.
2. Fill in the placeholders in `config/application.properties` with your actual values.
3. Make sure to exclude `config/application.properties` from Git by adding the line `config/application.properties` to the `.gitignore` file.
4. Run the application.

Note: Do not commit `config/application.properties` to Git, as it may contain sensitive information such as credentials and database connection strings.
