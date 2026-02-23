@rem Gradle wrapper for Windows
@if "%DEBUG%"=="" @echo off
setlocal
set DIRNAME=%~dp0
set CLASSPATH=%DIRNAME%\gradle\wrapper\gradle-wrapper.jar
java -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
