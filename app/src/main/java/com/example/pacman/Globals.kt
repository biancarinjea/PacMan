package com.example.pacman

import kotlin.jvm.Synchronized

class Globals  // Restrict the constructor from being instantiated
private constructor() {
    // Global variable
    var highScore = 0
    var score = 0;
    companion object {
        @JvmStatic
        @get:Synchronized
        var instance: Globals? = null
            get() {
                if (field == null) {
                    field = Globals()
                }
                return field
            }
            private set
    }
}