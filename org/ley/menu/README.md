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
public  class FirstMenu implements SimpleMenu {

    public SimpleMenu() {
        super("first.menu.exaple.id", 5 * 9); // or super("first.menu.exaple.id, InventoryType.CHEST)
    }

    public abstract void onMenuClick(Player clicker, InventoryClickEvent event, Inventory inventory, HashMap<String, String> args){

    }

    public abstract MenuComponent onMenuOpen(Player player, Inventory inventory, HashMap<String, String> args)
         return new MenuComponent("coolname", inventory) // or new MenuComponent(holder, "coolname", inventory);
    }
}
```

### 3. Create Initialize the Menu
Be sure to only do this one time

```java
FirstMenu firstMenu = new FirstMenu();
```

If you have do this one you can `open` it like this

```java
firstMenu.open(player, new HashMap<>());
MenuBrowser.openForPlayer(player, "first.menu.exaple.id"); // If you dont have the firstMenu in range 
```

### 4. Use of Args in Menus
To give args in a menu with `openForPlayer` you can use the MenuBrowser tool `buildURL()`

```java
String url = MenuBrowser.buildURL("first.menu.exaple.id", new HashMap<>());
MenuBrowser.openForPlayer(player, url);
```
