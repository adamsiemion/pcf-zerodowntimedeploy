package com.siemion

import java.util.*

class CommandExecutor(val logger: Logger) {

    fun executeCommand(command: String): List<String> {
        logger.log("Executing command \"$command\"")
        val proc = Runtime.getRuntime().exec(command)
        val output = ArrayList<String>()
        Scanner(proc.inputStream).use {
            while (it.hasNextLine()) {
                val line = it.nextLine()
                output.add(line)
                logger.log(line)
            }
        }
        proc.waitFor()
        val exitValue = proc.exitValue()
        if (exitValue != 0) {
            throw IllegalStateException("Command \"$command\" returned non-zero exit code: $exitValue")
        }
        logger.log("Command \"$command\" executed successfully")
        return output
    }
}