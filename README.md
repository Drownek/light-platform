
# light-platform

A platform library for creating Minecraft plugins with ease.


## 🚀 About This Project

This library includes a lightweight, Spring-Boot-like class management system:
- It uses reflection to scan the project tree for annotated classes and automatically injects them into a simple dependency container.
- Other components can then easily access and use these injected classes — making development more modular, clean, and intuitive.

This project uses code from [okaeri-platform](https://github.com/OkaeriPoland/okaeri-platform), originally created by Dawid Sawicki.

Significant changes have been made, including:

- Moving from Maven to Gradle
- Adding [LiteCommands](https://github.com/Rollczi/LiteCommands) support in place of okaeri-commands
- Modified serdes for better ItemStack support
- And some other features to speed up plugin development even more.

Also, my own [bukkit-utils](https://github.com/Drownek/bukkit-utils) are bundled with the platform

The original project is licensed under the MIT License, which is retained in this repository.

## Screenshots

<details> 
<summary><strong>CustomAmountSelectionGui</strong></summary>

<img width="552" height="294" alt="image" src="https://github.com/user-attachments/assets/a47c01a7-c36b-410b-8c60-47d7ed014a1c" />

```java
GuiItemInfo moneyDisplay = new GuiItemInfo(
    13,
    XMaterial.GOLD_INGOT,
    "&6&lWithdraw Money",
    Arrays.asList(
        "&7Withdraw from your bank account",
        "",
        "&fCurrent Balance: &a$" + balance,
        "&fWithdraw Amount: &e${VALUE}",
        "&fRemaining: &a${REMAINING}",
        "",
        "&7Shift-click for ±$1000",
        "&eClick to withdraw!"
    )
);

AmountSelectionGui.builder()
    .title("&8Bank Withdrawal")
    .displayItem(moneyDisplay)
    .initialValue(100)
    .minValue(1)
    .maxValue(balance)
    .increaseStep(100)
    .decreaseStep(100)
    .increaseStepShift(1000)
    .decreaseStepShift(1000)
    .rows(4)
    .additionalPlaceholders(integer -> Map.of("{REMAINING}", balance - integer))
    .onConfirm(amount -> {
        player.sendMessage(TextUtil.color("&aWithdrew &e$" + amount + " &afrom your account!"));
        // Add actual withdrawal logic here
    })
    .build()
    .open(player);
```

</details>

<details> 
<summary><strong>WaitingTask</strong></summary>

![waiting_task](https://github.com/user-attachments/assets/1ed3782e-2ac5-4b66-bd56-6a46eb7347d5)

```java
WaitingTask.builder()
    .actionName("TEST")
    .duration(Duration.ofSeconds(5))
    .successAction(() -> player.sendMessage("!!!"))
    .build()
    .start(player);
```

</details>

## 📦 Installation
<details>

<summary>Bukkit</summary>

### Gradle (Kotlin DSL)
```kotlin
repositories {
    mavenCentral()
    maven("https://storehouse.okaeri.eu/repository/maven-releases/")
    maven("https://repo.panda-lang.org/releases")
}

dependencies {
    implementation("io.github.drownek:platform-bukkit:{version}")
}
```
### Gradle (Groovy)
```groovy
repositories {
    mavenCentral()
    maven { url 'https://storehouse.okaeri.eu/repository/maven-releases/' }
    maven { url 'https://repo.panda-lang.org/releases' }
}

dependencies {
    implementation 'io.github.drownek:platform-bukkit:{version}'
}
```
### Maven
```xml
<repositories>
    <repository>
        <id>okaeri-repo</id>
        <url>https://storehouse.okaeri.eu/repository/maven-public/</url>
    </repository>
    <repository>
        <id>panda-repo</id>
        <url>https://repo.panda-lang.org/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.drownek</groupId>
        <artifactId>platform-bukkit</artifactId>
        <version>{version}</version>
    </dependency>
</dependencies>
```

</details>

<details>

<summary>Velocity</summary>

### Gradle (Kotlin DSL)
```kotlin
repositories {
    mavenCentral()
    maven("https://storehouse.okaeri.eu/repository/maven-releases/")
}

dependencies {
    implementation("io.github.drownek:platform-velocity:{version}")
}
```
### Gradle (Groovy)
```groovy
repositories {
    mavenCentral()
    maven { url 'https://storehouse.okaeri.eu/repository/maven-releases/' }
}

dependencies {
    implementation 'io.github.drownek:platform-velocity:{version}'
}
```
### Maven
```xml
<repositories>
    <repository>
        <id>okaeri-repo</id>
        <url>https://storehouse.okaeri.eu/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.drownek</groupId>
        <artifactId>platform-velocity</artifactId>
        <version>{version}</version>
    </dependency>
</dependencies>
```

</details>

## Usage/Examples
You can look at an example plugin that shows basic functionality like a database, LiteCommands usage, and injector.

## 📜 License

Project is licensed under [MIT](https://choosealicense.com/licenses/mit/).

This means that...

- ✅ You can freely use, copy, modify, and distribute this project, even for commercial purposes.
- 🧾 You **must include the original license and copyright notice** in any copies or substantial portions.
- ❌ The software is provided **"as is"**, without warranty of any kind. The author is **not liable** for any damages or issues caused by using it.
