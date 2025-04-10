## Overview

This project is a part of the `leyCM/repository` repository. It includes a library for managing menus. The library source is located in the `dev/org/ley/menu/source/menu` directory.

## Features

- **Menu Management**: Provides functionalities to manage menus efficiently.
- **Time Manager**: Integrates with the time manager to ensure accurate timing within menus.

## Requirements

- **Java 21+**
- **Maven 3.6+**

## Repository 
To use the menu library in your project, add the following dependency to your `pom.xml` file:

```xml
<repository>
    <id>ley.org</id>
    <url>https://leycm.github.io/repository/</url>
</repository>
```

```xml
<dependency>
    <groupId>org.ley.menu</groupId>
    <artifactId>menu</artifactId>
    <version>1.0.1</version>
</dependency>
<dependency>
    <groupId>org.ley.time</groupId>
    <artifactId>time</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any improvements or bug fixes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Installation Local

1. Clone the repository:
    ```sh
    git clone https://github.com/leyCM/repository.git
    cd repository
    ```

2. Navigate to the project directory:
    ```sh
    cd dev/org/ley/menu/source/menu
    ```

3. Build the project using Maven:
    ```sh
    mvn clean install
    ```
