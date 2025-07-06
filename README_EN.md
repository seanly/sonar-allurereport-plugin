# SonarQube Allure Report Plugin

[![Maven Central](https://img.shields.io/maven-central/v/org.sonarsource.plugins.allurereport/sonar-allurereport-plugin.svg)](https://search.maven.org/artifact/org.sonarsource.plugins.allurereport/sonar-allurereport-plugin)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![SonarQube](https://img.shields.io/badge/SonarQube-9.x-green.svg)](https://www.sonarqube.org/)
[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://openjdk.java.net/)

A comprehensive Allure Report integration plugin for SonarQube that provides custom metrics, report visualization, and seamless integration with your CI/CD pipeline.

> 🌍 **Multi-language Support**: English | [中文](README.md)

## 📋 Table of Contents

- [Features](#-features)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Usage](#-usage)
- [Development](#-development)
- [Building](#-building)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

- **Custom Metrics**: Track Allure report metrics within SonarQube
- **Report Integration**: Seamlessly integrate Allure reports into SonarQube
- **Web Interface**: Modern React-based web interface for report visualization
- **MinIO Upload**: Automated upload capabilities to MinIO S3-compatible storage
- **CI/CD Ready**: Designed for integration with modern CI/CD pipelines
- **Multi-language Support**: Internationalization support for multiple languages

## 🔧 Requirements

- **SonarQube**: 9.5 or higher
- **Java**: JDK 11 or higher
- **Node.js**: v16.14.0 or higher (for development)
- **Maven**: 3.6 or higher

## 🚀 Installation

### Method 1: Download Pre-built JAR

1. Download the latest release from [GitHub Releases](https://github.com/seanly/sonar-allurereport-plugin/releases)
2. Copy the JAR file to your SonarQube's `extensions/plugins/` directory
3. Restart SonarQube

### Method 2: Build from Source

```bash
# Clone the repository
git clone https://github.com/seanly/sonar-allurereport-plugin.git
cd sonar-allurereport-plugin

# Build the plugin
mvn clean package

# Copy the generated JAR to SonarQube plugins directory
cp target/sonar-allurereport-plugin-*.jar /path/to/sonarqube/extensions/plugins/

# Restart SonarQube
```

### Method 3: Docker Development

```bash
# Start SonarQube with Docker Compose
docker-compose up -d

# Access SonarQube at http://localhost:9000
# Default credentials: admin/admin
```

### Method 4: Docker Plugin Update (Development)

For development workflow with Docker:

```bash
# Build the plugin
mvn clean package

# Copy the plugin JAR to the running SonarQube container
docker cp target/sonar-allurereport-plugin-9.0.0.jar sonarqube:/opt/sonarqube/extensions/plugins/

# Restart the SonarQube container to load the updated plugin
docker-compose restart sonarqube
```

**Note**: This method is ideal for development as it allows you to quickly test plugin changes without rebuilding the entire Docker image.

## ⚙️ Configuration

### Plugin Settings

The plugin can be configured through SonarQube's administration interface:

1. Go to **Administration** > **Configuration** > **Allure Report**
2. Configure the following settings:
   - **MinIO Endpoint**: URL for MinIO server
   - **MinIO Access Key**: Authentication access key
   - **MinIO Secret Key**: Authentication secret key
   - **MinIO Bucket**: Bucket name for storing reports
   - **MinIO Use SSL**: Enable/disable SSL connection
   - **Upload Enabled**: Enable/disable MinIO upload functionality
   - **HTML Report Path**: Path to Allure HTML report directory
   - **JSON Report Path**: Path to Allure results directory

### Project Configuration

Add the following to your `sonar-project.properties`:

```properties
# Allure Report Plugin Configuration
sonar.allure.minio.upload.enabled=true
sonar.allure.minio.endpoint=http://localhost:9000
sonar.allure.minio.accessKey=your-access-key
sonar.allure.minio.secretKey=your-secret-key
sonar.allure.minio.bucket=allure-reports
sonar.allure.minio.useSSL=false
sonar.allureReport.htmlReportPath=target/site/allure-maven-plugin
sonar.allureReport.jsonReportPath=target/allure-results
sonar.projectVersion=1.0.0
```

## 📖 Usage

### Basic Usage

1. **Generate Allure Reports**: Ensure your tests generate Allure reports
2. **Configure Plugin**: Set up the plugin settings in SonarQube
3. **Run Analysis**: Execute SonarQube analysis on your project
4. **View Reports**: Access Allure reports through the SonarQube web interface

### Web Interface

The plugin provides a modern web interface accessible through:

- **Project Level**: Navigate to your project and look for "Allure-Report" in the menu
- **Global Level**: Access from the main SonarQube menu

### How It Works

The plugin works automatically during SonarQube analysis:

1. **During Analysis**: The plugin's sensor (`AllureReportSensor`) runs during SonarQube analysis
2. **HTML Report Upload**: Automatically uploads the entire Allure HTML report directory to MinIO
3. **URL Generation**: Creates a MinIO URL pointing to the uploaded report's `index.html`
4. **Metric Storage**: Stores the MinIO URL as a SonarQube metric for display

### MinIO Integration

The plugin automatically uploads Allure HTML reports to MinIO:

- **Upload Path**: `{endpoint}/{bucket}/{project-key}/{branch}/site/`
- **Report URL**: `{endpoint}/{bucket}/{project-key}/{branch}/site/index.html`
- **File Types**: HTML, CSS, JS, images, and all other report assets
- **Authentication**: S3-compatible authentication with configured MinIO credentials

### MinIO Proxy Service

The plugin provides a MinIO proxy service that allows secure access to MinIO content through SonarQube:

#### API Endpoints

1. **Path Proxy**: `/api/allurereport/minio/{bucket}/{projectKey}/{branch}/site/{file}`
   - Simplified path format without endpoint
   - Example: `/api/allurereport/minio/allure-reports/my-project/main/site/index.html`

2. **File Proxy**: `/api/allurereport/minio/proxy`
   - Parameters: `projectKey`, `branch`, `path` (optional, defaults to index.html)
   - Example: `/api/allurereport/minio/proxy?projectKey=my-project&branch=main&path=index.html`

3. **Bucket Proxy**: `/api/allurereport/minio/bucket`
   - Parameters: `projectKey`, `branch`, `path` (optional)
   - Example: `/api/allurereport/minio/bucket?projectKey=my-project&branch=main&path=site/index.html`

4. **Bucket List**: `/api/allurereport/minio/list`
   - Parameters: `projectKey`, `branch`, `prefix` (optional)
   - Example: `/api/allurereport/minio/list?projectKey=my-project&branch=main&prefix=site/`
   - Returns: JSON format file list

#### Benefits

- **Security**: Access through SonarQube proxy, avoiding direct exposure of MinIO endpoints
- **Unified Authentication**: Uses SonarQube's authentication mechanism
- **Access Control**: Can control access based on SonarQube permissions
- **CORS Friendly**: Avoids cross-origin issues

### Configuration Properties

Configure the plugin in your `sonar-project.properties`:

```properties
# Enable/disable MinIO upload
sonar.allure.minio.upload.enabled=true

# MinIO configuration
sonar.allure.minio.endpoint=http://localhost:9000
sonar.allure.minio.accessKey=your-access-key
sonar.allure.minio.secretKey=your-secret-key
sonar.allure.minio.bucket=allure-reports
sonar.allure.minio.useSSL=false

# MinIO proxy configuration
sonar.allure.minio.use.proxy=true

# Allure report paths
sonar.allureReport.htmlReportPath=target/site/allure-maven-plugin
sonar.allureReport.jsonReportPath=target/allure-results

# Project version (required for MinIO upload)
sonar.projectVersion=1.0.0
```

## 🛠️ Development

### Prerequisites

- Java 11+
- Node.js v16.14.0+
- Maven 3.6+
- Git

### Project Structure

```
sonar-allurereport-plugin/
├── src/
│   ├── main/
│   │   ├── java/                    # Java backend code
│   │   │   └── org/sonarsource/plugins/allurereport/
│   │   │       ├── AllureReportPlugin.java
│   │   │       ├── measures/        # Custom metrics
│   │   │       ├── settings/        # Plugin settings
│   │   │       ├── utils/           # Utility classes
│   │   │       └── web/             # Web interface
│   │   ├── js/                      # Frontend JavaScript
│   │   │   └── report_page/
│   │   └── resources/               # Resources and i18n
│   └── test/                        # Test files
├── conf/                            # Configuration files
│   ├── jest/                        # Jest test configuration
│   └── webpack/                     # Webpack configuration
├── scripts/                         # Build and development scripts
└── docker-compose.yml              # Docker development setup
```

### Development Setup

```bash
# Clone and setup
git clone https://github.com/seanly/sonar-allurereport-plugin.git
cd sonar-allurereport-plugin

# Install dependencies
npm install

# Start development server
npm start

# Run tests
npm test

# Build for production
npm run build
```

### Docker Development Workflow

For rapid development and testing with Docker:

```bash
# 1. Start SonarQube container
docker-compose up -d

# 2. Build your plugin changes
mvn clean package

# 3. Update plugin in running container
docker cp target/sonar-allurereport-plugin-9.0.0.jar sonarqube:/opt/sonarqube/extensions/plugins/

# 4. Restart SonarQube to load changes
docker-compose restart sonarqube

# 5. Check logs for any issues
docker-compose logs -f sonarqube
```

**Benefits of this workflow:**
- ⚡ **Fast iteration**: No need to rebuild Docker images
- 🔄 **Quick testing**: Immediate plugin updates
- 🐛 **Easy debugging**: Direct access to container logs
- 🧹 **Clean environment**: Isolated development setup

## 🔨 Building

### Full Build

```bash
# Build the complete plugin (Java + Frontend)
mvn clean package
```

### Frontend Only

```bash
# Build frontend assets
npm run build
```

### Development Build

```bash
# Build with development optimizations
npm run build:dev
```

## 🧪 Testing

### Java Tests

```bash
# Run Java unit tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### JavaScript Tests

```bash
# Run Jest tests
npm test

# Run tests with coverage
npm run test:coverage

# Run tests in watch mode
npm run test:watch
```

### Integration Tests

```bash
# Run integration tests
mvn verify
```

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Guidelines

- Follow the existing code style
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting

### Code Style

- **Java**: Follow Google Java Style Guide
- **JavaScript**: Use ESLint configuration
- **Commit Messages**: Use conventional commits format

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Seanly Liu** - [seanly@opsbox.dev](mailto:seanly@opsbox.dev)

- GitHub: [@seanly](https://github.com/seanly)

## 🙏 Acknowledgments

- [SonarSource](https://www.sonarsource.com/) for SonarQube
- [Allure Framework](https://allure.qatools.ru/) for test reporting
- [React](https://reactjs.org/) for the web interface
- [Maven](https://maven.apache.org/) for build management

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/seanly/sonar-allurereport-plugin/issues)
- **Discussions**: [GitHub Discussions](https://github.com/seanly/sonar-allurereport-plugin/discussions)
- **Email**: [seanly@opsbox.dev](mailto:seanly@opsbox.dev)

---

**Made with ❤️ by the SonarQube Allure Report Plugin Team** 