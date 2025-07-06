# SonarQube Allure 报告插件

[![Maven Central](https://img.shields.io/maven-central/v/org.sonarsource.plugins.allurereport/sonar-allurereport-plugin.svg)](https://search.maven.org/artifact/org.sonarsource.plugins.allurereport/sonar-allurereport-plugin)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![SonarQube](https://img.shields.io/badge/SonarQube-9.x-green.svg)](https://www.sonarqube.org/)
[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://openjdk.java.net/)

一个全面的 Allure 报告集成插件，为 SonarQube 提供自定义指标、报告可视化和与 CI/CD 管道的无缝集成。

> 🌍 **多语言支持**: [English](README_EN.md) | 中文

## 📋 目录

- [✨ 功能特性](#-功能特性)
- [🔧 系统要求](#-系统要求)
- [🚀 快速开始](#-快速开始)
- [⚙️ 配置说明](#️-配置说明)
- [📖 使用指南](#-使用指南)
- [🛠️ 开发指南](#️-开发指南)
- [🤝 贡献指南](#-贡献指南)
- [📄 许可证](#-许可证)

## ✨ 功能特性

- **📊 自定义指标**: 在 SonarQube 中跟踪 Allure 报告指标
- **🌐 报告集成**: 将 Allure 报告无缝集成到 SonarQube 中
- **🎨 现代化界面**: 基于 React 的现代化 Web 界面用于报告可视化
- **☁️ MinIO 上传**: 自动上传功能到 MinIO S3 兼容存储
- **🔗 站点地址支持**: 支持自定义站点地址替换 `/minio` 路径
- **🚀 CI/CD 就绪**: 专为现代 CI/CD 管道集成而设计
- **🌍 多语言支持**: 支持多种语言的国际化

## 🔧 系统要求

- **SonarQube**: 9.5 或更高版本
- **Java**: JDK 11 或更高版本
- **Node.js**: v16.14.0 或更高版本（用于开发）
- **Maven**: 3.6 或更高版本

## 🚀 快速开始

### 方法 1: 下载预构建 JAR（推荐）

1. 从 [GitHub Releases](https://github.com/seanly/sonar-allurereport-plugin/releases) 下载最新版本
2. 将 JAR 文件复制到 SonarQube 的 `extensions/plugins/` 目录
3. 重启 SonarQube

### 方法 2: 从源码构建

```bash
# 克隆仓库
git clone https://github.com/seanly/sonar-allurereport-plugin.git
cd sonar-allurereport-plugin

# 构建插件
mvn clean package

# 将生成的 JAR 复制到 SonarQube 插件目录
cp target/sonar-allurereport-plugin-*.jar /path/to/sonarqube/extensions/plugins/

# 重启 SonarQube
```

### 方法 3: Docker 开发环境

```bash
# 使用 Docker Compose 启动 SonarQube
docker-compose up -d

# 访问 SonarQube: http://localhost:9000
# 默认凭据: admin/admin
```

## ⚙️ 配置说明

### 插件设置

可以通过 SonarQube 的管理界面配置插件：

1. 转到 **管理** > **配置** > **Allure Report**
2. 配置以下设置：

#### MinIO 配置
- **MinIO Endpoint**: MinIO 服务器地址（例如：`http://localhost:9000`）
- **MinIO Access Key**: 认证访问密钥
- **MinIO Secret Key**: 认证秘密密钥
- **MinIO Bucket**: 用于存储报告的存储桶名称
- **MinIO 使用SSL**: 启用/禁用 SSL 连接
- **MinIO 上传启用**: 启用/禁用 MinIO 上传功能

#### 站点配置
- **站点地址**: 用于替换 `/minio` 路径的站点地址（例如：`https://example.com`）

#### 报告路径配置
- **HTML 报告路径**: Allure HTML 报告目录的路径
- **JSON 报告路径**: Allure 结果目录的路径

### 项目配置

在 `sonar-project.properties` 中添加以下内容：

```properties
# ===== Allure Report 插件配置 =====

# MinIO 配置
sonar.allure.minio.upload.enabled=true
sonar.allure.minio.endpoint=http://localhost:9000
sonar.allure.minio.accessKey=your-access-key
sonar.allure.minio.secretKey=your-secret-key
sonar.allure.minio.bucket=allure-reports
sonar.allure.minio.useSSL=false

# 站点地址配置（可选）
sonar.allure.site.address=https://example.com

# 报告路径配置
sonar.allureReport.htmlReportPath=target/site/allure-maven-plugin
sonar.allureReport.jsonReportPath=target/allure-results

# 项目版本（MinIO 上传必需）
sonar.projectVersion=1.0.0
```

## 📖 使用指南

### 基本使用流程

1. **生成 Allure 报告**: 确保您的测试生成 Allure 报告
2. **配置插件**: 在 SonarQube 中设置插件配置
3. **运行分析**: 在您的项目上执行 SonarQube 分析
4. **查看报告**: 通过 SonarQube Web 界面访问 Allure 报告

### Web 界面访问

插件提供了一个现代化的 Web 界面，可通过以下方式访问：

- **项目级别**: 导航到您的项目并查找菜单中的 "Allure-Report"
- **全局级别**: 从主 SonarQube 菜单访问

### URL 生成机制

插件支持两种 URL 生成模式：

#### 1. 站点地址模式（推荐）
当配置了站点地址时：
```
原始路径: /minio/bucket/project/branch/site/index.html
生成URL: https://example.com/bucket/project/branch/site/index.html
```

#### 2. Nginx 代理模式
当未配置站点地址时：
```
生成URL: /minio/bucket/project/branch/site/index.html
```

### MinIO 集成

插件自动将 Allure HTML 报告上传到 MinIO：

- **上传路径**: `{bucket}/{project-key}/{branch}/site/`
- **文件类型**: HTML、CSS、JS、图像和所有其他报告资源
- **认证**: 使用配置的 MinIO 凭据进行 S3 兼容认证

### 工作原理

插件在 SonarQube 分析期间自动工作：

1. **分析期间**: 插件的传感器 (`AllureReportSensor`) 在 SonarQube 分析期间运行
2. **HTML 报告上传**: 自动将整个 Allure HTML 报告目录上传到 MinIO
3. **URL 生成**: 根据配置创建指向 MinIO 报告的 URL
4. **指标存储**: 将生成的 URL 存储为 SonarQube 指标以供显示

## 🛠️ 开发指南

### 前置要求

- Java 11+
- Node.js v16.14.0+
- Maven 3.6+
- Git

### 项目结构

```
sonar-allurereport-plugin/
├── src/
│   ├── main/
│   │   ├── java/                    # Java 后端代码
│   │   │   └── org/sonarsource/plugins/allurereport/
│   │   │       ├── AllureReportPlugin.java
│   │   │       ├── measures/        # 自定义指标
│   │   │       ├── settings/        # 插件设置
│   │   │       ├── utils/           # 工具类
│   │   │       └── web/             # Web 界面
│   │   ├── js/                      # 前端 JavaScript
│   │   │   └── report_page/
│   │   └── resources/               # 资源和国际化
│   └── test/                        # 测试文件
├── conf/                            # 配置文件
│   ├── jest/                        # Jest 测试配置
│   └── webpack/                     # Webpack 配置
├── scripts/                         # 构建和开发脚本
└── docker-compose.yml              # Docker 开发设置
```

### 开发设置

```bash
# 克隆和设置
git clone https://github.com/seanly/sonar-allurereport-plugin.git
cd sonar-allurereport-plugin

# 安装依赖
npm install

# 启动开发服务器
npm start

# 运行测试
npm test

# 构建生产版本
npm run build
```

### Docker 开发工作流

用于快速开发和测试的 Docker：

```bash
# 1. 启动 SonarQube 容器
docker-compose up -d

# 2. 构建插件更改
mvn clean package

# 3. 更新运行容器中的插件
docker cp target/sonar-allurereport-plugin-9.0.0.jar sonarqube:/opt/sonarqube/extensions/plugins/

# 4. 重启 SonarQube 以加载更改
docker-compose restart sonarqube

# 5. 检查日志中的任何问题
docker-compose logs -f sonarqube
```

**此工作流的好处:**
- ⚡ **快速迭代**: 无需重建 Docker 镜像
- 🔄 **快速测试**: 立即更新插件
- 🐛 **易于调试**: 直接访问容器日志
- 🧹 **清洁环境**: 隔离的开发设置

### 构建说明

```bash
# 完整构建（Java + 前端）
mvn clean package

# 仅前端构建
npm run build

# 开发构建
npm run build:dev
```

### 测试说明

```bash
# Java 测试
mvn test
mvn test jacoco:report

# JavaScript 测试
npm test
npm run test:coverage
npm run test:watch

# 集成测试
mvn verify
```

## 🤝 贡献指南

我们欢迎贡献！请按照以下步骤：

1. **Fork** 仓库
2. **创建** 功能分支 (`git checkout -b feature/amazing-feature`)
3. **提交** 您的更改 (`git commit -m 'Add amazing feature'`)
4. **推送** 到分支 (`git push origin feature/amazing-feature`)
5. **打开** Pull Request

### 开发指南

- 遵循现有的代码风格
- 为新功能添加测试
- 根据需要更新文档
- 确保在提交前所有测试都通过

### 代码风格

- **Java**: 遵循 Google Java 风格指南
- **JavaScript**: 使用 ESLint 配置
- **提交消息**: 使用约定式提交格式

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 👨‍💻 作者

**Seanly Liu** - [seanly@opsbox.dev](mailto:seanly@opsbox.dev)

- GitHub: [@seanly](https://github.com/seanly)

## 🙏 致谢

- [SonarSource](https://www.sonarsource.com/) 提供 SonarQube
- [Allure Framework](https://allure.qatools.ru/) 提供测试报告
- [React](https://reactjs.org/) 提供 Web 界面
- [Maven](https://maven.apache.org/) 提供构建管理

## 📞 支持

- **问题**: [GitHub Issues](https://github.com/seanly/sonar-allurereport-plugin/issues)
- **讨论**: [GitHub Discussions](https://github.com/seanly/sonar-allurereport-plugin/discussions)
- **邮箱**: [seanly@opsbox.dev](mailto:seanly@opsbox.dev)

---

**由 SonarQube Allure Report Plugin 团队用心制作 ❤️**
