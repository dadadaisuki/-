pluginManagement {
    repositories {
        // 国内网络：优先走镜像，避免 dl.google.com 连不上导致 AGP 解析失败
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral() // 兜底：Aliyun 镜像缺失的库直接从 Maven Central 下载
    }
}

rootProject.name = "TravelSuperApp"
include(":app")
