package com.maxciv.infer.plugin.process

/**
 * @author maxim.oleynik
 * @since 29.11.2018
 */
enum class OperationSystems(val title: String, val key: String) {

    DEFAULT("Undefined", "nothing"),
    MAC_OS("Mac OS", "osx"),
    LINUX("Linux", "linux");

    companion object {

        fun defineOs(): OperationSystems {
            val os = System.getProperty("os.name").toLowerCase()
            return when {
                os.contains("mac") -> MAC_OS
                os.contains("nix") -> LINUX
                os.contains("nux") -> LINUX
                else -> DEFAULT
            }
        }

        fun valueOfTitle(title: String): OperationSystems {
            return when (title) {
                MAC_OS.title -> MAC_OS
                LINUX.title -> LINUX
                else -> DEFAULT
            }
        }
    }
}
