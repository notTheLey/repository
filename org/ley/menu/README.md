 # Menu Library Documentation

## Overview

The `menu` library, located in the `/source/menu` directory, provides tools for efficient menu management and integrates seamlessly with your project. This guide will help you integrate the library into your project and provide tips on how to use it effectively.

### Current Version

- **Version**: 1.0.2

---

## How to Add the Menu Library to Your Projec

To use the `menu` library in your project, add the following dependency to your `pom.xml` file:
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
    <version>1.0.2</version>
</dependency>
```

---

## How to Use the Menu Library

### 1. Initialize the Menu Manager

Create an instance of the `MenuManager` class to start managing menus in your application:

```java
MenuBrowser menuBrowser = new MenuBrowser(this);
```
*You never have to use the `menuBrowser` again*

### 2. Create Simple Menu 

```java

```
