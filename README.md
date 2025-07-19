<h1 align="center">
  Approval System Backend
</h1>

<p align="center">
  English | <a href="./doc/README_zh.md">简体中文</a>
</p>

## :pushpin: Overview

Approval System Backend is a refactoring of the legacy approval system, built with Spring Boot and Maven.

## :rocket: Quick Start

### Prerequisites

- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+
- [pre-commit](https://pre-commit.com/#quick-start)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/NJUPT-SAST/approval-system-backend.git
   cd approval-system-backend
   ```

2. **Install pre-commit hooks**
   ```bash
   # For Arch Linux
   sudo pacman -S pre-commit
   # For Pipx users (cross-platform)
   pipx install pre-commit

   # Install the hook
   pre-commit install
   ```

3. **Build the project**
   ```bash
   mvn clean compile
   ```

4. **Run the application**
   ```bash
   # Option 1: Direct run
   mvn spring-boot:run -pl approval-server

   # Option 2: Package and run
   mvn clean package
   java -jar approval-server/target/approval-server-*.jar
   ```

### Development

#### Code Formatting

This project uses `Pre-commit` for code formatting. Run the following command before you commit to format your code:

```bash
pre-commit run --all-files
```

> [!TIP]
> If you encounter issues with pre-commit hooks, you can temporarily skip them using `git commit --no-verify`.

## Contributing

Pull requests and any feedback are welcome. For major changes, please open an issue first to discuss what you would like to change.

### Contributors

<a href="https://github.com/NJUPT-SAST/approval-system-backend/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=NJUPT-SAST/approval-system-backend" />
</a>

## License

// TODO
