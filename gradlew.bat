@rem Gradle wrapper for Windows
@if "%DEBUG%"=="" @echo off
@rem Set local scope for the variables with windows NT shell
setlocal
set DIRNAME=%~dp0
set CLASSPATH=%DIRNAME%\gradle\wrapper\gradle-wrapper.jar
java -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
:end
endlocal
