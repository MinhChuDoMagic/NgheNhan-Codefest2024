# JavaFX 17.0.3 Setup Guide

This guide will walk you through downloading and setting up JavaFX version 17.0.3 for Java projects in IntelliJ IDEA.

## Downloading JavaFX 17.0.3

1. Download JavaFX 17.0.3 SDK for Windows from the following link:
    - [JavaFX 17.0.3 SDK for Windows (64-bit)](https://download2.gluonhq.com/openjfx/17.0.13/openjfx-17.0.13_windows-x64_bin-sdk.zip)

2. Extract the downloaded ZIP file to a directory on your computer.

## Setting Up JavaFX in IntelliJ IDEA

To configure IntelliJ IDEA to use JavaFX:

1. Open IntelliJ IDEA, and go to **File > Settings** (or **Preferences** on macOS).
2. Navigate to **Appearance & Behavior > Path Variables**.
3. Click on **+** to add a new path variable.
4. Set the **Name** of the variable to `PATH_TO_FX`.
5. For the **Value**, browse to the `lib` folder of your extracted JavaFX SDK and select it.
6. Click **Apply** to save the path variable.

## Running JavaFX Applications in IntelliJ

When you’re ready to run a JavaFX application:

1. Go to **Run > Edit Configurations** in IntelliJ.
2. Under **VM options**, add the following:

```plaintext
--module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml
```
   