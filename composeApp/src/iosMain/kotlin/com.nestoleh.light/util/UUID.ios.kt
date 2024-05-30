package com.nestoleh.light.util

import platform.Foundation.NSUUID

actual fun randomUUID(): String {
    return NSUUID().UUIDString()
}