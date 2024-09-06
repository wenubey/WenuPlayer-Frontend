package org.wenubey.wenuplayerfrontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform