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

        fun defineOs(): OperationSystems = with(System.getProperty("os.name").toLowerCase()) {
            when {
                contains("mac") -> MAC_OS
                contains("nix") -> LINUX
                contains("nux") -> LINUX
                else -> DEFAULT
            }
        }

        fun valueOfTitle(title: String): OperationSystems = when (title) {
            MAC_OS.title -> MAC_OS
            LINUX.title -> LINUX
            else -> DEFAULT
        }
    }
}
