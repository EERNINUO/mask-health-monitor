# 基于微传感器阵列的呼吸健康监测系统

<p align="center">
  <a href="https://github.com/EERNINUO/mask-health-monitor" style="margin: 2px;">
    <img alt="仓库状态" src="https://img.shields.io/badge/status-暂停维护-blue">
  </a>
  <a href="https://github.com/EERNINUO/mask-health-monitor" style="margin: 2px;">
    <img alt="GitHub 仓库星标" src="https://img.shields.io/github/stars/EERNINUO/mask-health-monitor">
  </a>                                  
  <br/>
  <a href="https://raw.githubusercontent.com/EERNINUO/mask-health-monitor/main/LICENSE" style="margin: 2px;">
    <img src="https://img.shields.io/badge/License-MIT-red" alt="许可证">
  </a>
  <a href="https://raw.githubusercontent.com/EERNINUO/mask-health-monitor/main/LICENSE" style="margin: 2px;">
    <img src="https://img.shields.io/badge/License-CC--BY--SA--4.0-green" alt="许可证">
  </a>
    <a href="https://raw.githubusercontent.com/EERNINUO/mask-health-monitor/main/LICENSE" style="margin: 2px;">
    <img src="https://img.shields.io/badge/License-CERN--OHL--S--2.0-blue" alt="许可证">
  </a>
  <!-- <a href="https://github.com/EERNINUO/mask-health-monitor/releases" style="margin: 2px;">
    <img src="https://img.shields.io/github/v/release/EERNINUO/mask-health-monitor?color=blueviolet&include_prereleases" alt="发布版本">
  </a> -->
</p>
</div>

## 🎯 项目概述

| 模块 | 状态 | 说明 |
|------|------|------|
| 传感器阵列 + STM32L 采集固件 | ✅ 完成 | 可正常采集多路传感器数据，通过串口/CH9141 透传输出 |
| 硬件设计（STM32L+CH9141 方案） | ✅ 完成 | 含原理图、PCB、BOM，已打样验证 |
| Android 基础 BLE 接收端 | ⚠️ 部分过时 | 基于旧版 Google BLE 驱动，Android 新版需重构 |
| 多分支 CNN 呼吸模式识别模型 | ✅ 完成 | 可对传感器阵列数据进行分类 |
| 单芯片方案（CH573）硬件 | ✅ 完成 | PCB 已设计，待固件开发 |
| CH573 BLE 协议栈固件 | 🚧 开发中 | 基础透传已完成，应用逻辑待移植 |
| 文档与重构 | 📝 进行中 | 当前仓库为整理后版本，历史仓库封存 |

## ✨ 功能特性
- [x] 硬件设计（STM32L 版本含原理图/PCB，CH573 版本进行中）
- [x] 固件开发（CH9141 透传方案完整）
- [ ] 单芯片 BLE 方案（CH573）—— 固件开发中
- [x] Android 基础 BLE 通信框架（待适配新版驱动，贡献欢迎）
- [x] 基于 Python 的多分支神经网络模型

## 🏗️ 系统架构
![硬件设计图](docs/pic/硬件设计.png)
![单片机软件](docs/pic/单片机软件.png)
![Android软件](docs/pic/Android软件.png)
> *本项目的核心在于传感器阵列数据、BLE 通信和 AI 模型的完整闭环。*
>*注：当前稳定链路为 STM32L + CH9141；CH573 单芯片方案可降低 BOM 成本，但 BLE 协议栈集成仍在进行中。*

## 📊 项目历史与技术演进
本项目的技术方案经历了两轮主要迭代，这里如实记录演进路径及中途遇到的挑战：
1.  **方案一 (STM32L + CH9141)**：功能完整验证了数据采集、透传和模型推理能力，但是未能实现数据波形的绘制，模型在 Android 端的部署。
2.  **方案二 (CH573 单芯片)**：为降低成本、简化设计转向单芯片集成。目前硬件已完成，但固件端 CH573 BLE 协议栈调试尚在进行中，这是本项目的**主要瓶颈**。
3.  **技术挑战**：在方案二推进期间，Android 新版 API 更新了权限模型，虽然在新版本 Android 上能进行兼容，但是在某些非原生 Android 权限模型可能与原生 Android 不同，导致旧有 Android 代码需要重构 BLE 驱动部分，目前该项目暂处于维护休眠期。

## 📁 项目结构
mask-type-health-monitoring-system/
├── README.md               
├── LICENSE                 # GPL-3.0
├── docs/                   # 详细文档
├── firmware/ 
│   ├── stm32l_project_v2.0/ # STM32L + CH9141 方案固件
│   └── ch573_project_v1.0/ # CH573 单芯片方案固件
├── hardware/               # 硬件设计文件
├── android/                # Android 应用源码
├── training/               # 模型训练代码
└── mechanical/             # 机械结构（如果有）


## 🚀 快速开始

### 1. 硬件搭建（验证过的 STM32L 方案）
- 从 `hardware/stm32l_ch9141/` 获取原理图和 PCB 文件（立创 EDA 格式）。
- 打样并焊接。**注意**：传感器阵列需按 BOM 清单选用对应型号。
- 烧录固件：见 `firmware/stm32l/` 中的 `README`。

### 2. 固件编译与烧录
- 使用 Keil MDK 将固件编译并烧录到 STM32L 单片机上。

### 3. Android 端使用
- 使用 Android Studio 打开 android/ 项目，编译下载。

### 4. 运行呼吸识别模型（离线）
<font color="red">待补充</font>
训练数据请自行采集或使用公开呼吸数据集（如果有的话）。

## 🗺️ 路线图（未来计划）
- 短期：完成 CH573 固件中 BLE 数据收发与应用逻辑的集成。
- 短期：重构 Android 端 BLE 驱动，支持新版 API（欢迎 PR）。
- 中期：将 CNN 模型转换为 TensorFlow Lite Micro，尝试在 STM32L 或 CH573 上直接推理。
- 长期：优化传感器阵列融合算法，降低误报率；设计更紧凑的口罩集成外壳。

## 🤝 如何贡献
如果你对以下任意方向感兴趣，非常欢迎参与：
- CH573 BLE 调试（我有硬件但缺乏协议栈经验）
- Android BLE 驱动升级（需要熟悉 BluetoothLeScanner 新 API）
- 模型量化与边缘部署

<!-- 请先阅读 CONTRIBUTING.md， -->
提交 PR 时请使用规范的 commit message（feat: / fix: / docs: 等）。
`CONTRIBUTING.md` 待补充。

## 📄 许可证
- 硬件设计文件（原理图/PCB/BOM）：CERN-OHL-S-2.0
- 固件与 Android 代码：MIT License
- 文档与模型权重：CC BY-SA 4.0

选择不同许可证是为了明确区分设计文档、开源代码与硬件设计的重用权限。

## 致谢
- 沁恒微电子提供的CH573开发资料与社区支持
- 哈尔滨理工大学测控技术与仪器专业大创项目资助

## 📧 联系方式
- 项目维护：@EERNINUO
- 如有技术问题，欢迎通过 GitHub Issues 交流。
- 最后更新：2026-05-31
- 维护状态：🟡 低活跃度维护（主要精力转至 ArbWave30，但欢迎 PR）
---

## 写在最后
这个项目不会因为中途换芯片或 Android API 升级而“烂尾”——它只是暂停在了某个技术节点。如果你觉得它有点意思，哪怕只是点个 star，也是对我继续填坑的最大鼓励 🌟