package com.plexquest.app

import java.util.UUID

object PlexConstants {
    val CLIENT_ID: String = UUID.nameUUIDFromBytes("plexquest-meta-quest".toByteArray()).toString()
}
