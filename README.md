## Allure Report Builder

[![release](http://github-release-version.herokuapp.com/github/allure-framework/allure-report-builder/release.svg?style=flat)](https://github.com/allure-framework/allure-report-builder/releases/latest) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.yandex.qatools.allure/allure-report-builder/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/ru.yandex.qatools.allure/allure-report-builder)
[![build](https://img.shields.io/teamcity/http/teamcity.qatools.ru/s/allure_report_builder_master_deploy.svg?style=flat)](http://teamcity.qatools.ru/viewType.html?buildTypeId=allure_report_builder_master_deploy&guest=1)

**Warning: this is an internal Allure component not intended for direct usage. Please refer to [documentation](https://github.com/allure-framework/allure-core/wiki) for details on how to use Allure.**

## Purpose
The main purpose of this component is to provide an ability to generate Allure report for any version of input XML files. All you need is to specify concrete Allure version and this component will automatically download respective dependencies from Maven repository and will then generate Allure report. This module is mainly intended to be used in various report generation tools such as command line tools, plugins and so on.
