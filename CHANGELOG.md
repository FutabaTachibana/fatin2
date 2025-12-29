# CHANGELOG

All notable changes to this project will be documented in this file. 

## [Unreleased]

### Fixed
- 重构文档注释，使用中文。x
- 重构 Config 的反序列化逻辑，增加 config_version 字段以支持向后兼容。x
- 

## [0.1.2] - 2025-12-29

### Fixed
- 修复了从 Maven 仓库下载依赖时缺少依赖的问题（这次应该真的修好了）。
- 重构了部分软件包。
- 重构了大部分 API 的文档注释。

## [0.1.1] - 2025-12-26

### Fixed
- 修复了从 Maven 仓库下载依赖时缺少 `pom.xml` 文件的问题。

## [0.1.0] - 2025-12-25

### Fixed
- 完善了 CI，修复了一些构建问题。

### Changed
- 现在中文文档是 `README.md`，而英文文档是 `README_EN.md`。