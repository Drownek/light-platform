
# light-platform

A platform library for creating Minecraft plugins with ease.


## 🚀 About This Project

This library includes a lightweight, Spring Boot–like class management system:
- It uses reflection to scan the project tree for annotated classes and automatically injects them into a simple dependency container.
- Other components can then easily access and use these injected classes — making development more modular, clean, and intuitive.

This project uses code from [okaeri-platform](https://github.com/OkaeriPoland/okaeri-platform), originally created by Dawid Sawicki.

Significant changes have been made, including:

- Moving from Maven to Gradle
- Adding [LiteCommands](https://github.com/Rollczi/LiteCommands) support
- Modified serdes for better ItemStack support
- And some other features to speed up plugin development even more.

Also, my own [bukkit-utils]() **(TODO)** are bundled with the platform

The original project is licensed under the MIT License, which is retained in this repository.

## Usage/Examples
**(TODO)**

## 📜 License

Project is licensed under [MIT](https://choosealicense.com/licenses/mit/).

This means that...

- ✅ You can freely use, copy, modify, and distribute this project, even for commercial purposes.
- 🧾 You **must include the original license and copyright notice** in any copies or substantial portions.
- ❌ The software is provided **"as is"**, without warranty of any kind. The author is **not liable** for any damages or issues caused by using it.
