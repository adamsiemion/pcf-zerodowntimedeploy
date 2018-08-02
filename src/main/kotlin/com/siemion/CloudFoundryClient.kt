package com.siemion

class CloudFoundryClient(val commandExecutor: CommandExecutor) {

    enum class AppStatus {
        STARTED,
        NOT_STARTED,
        NOT_FOUND
    }

    fun appStatus(appName: String): AppStatus {
        val command = "cf app ${appName}"
        val output = commandExecutor.executeCommand(command)
        for (line in output) {
            if (line.startsWith("requested state:")) {
                if (line.endsWith("started")) {
                    return AppStatus.STARTED
                }
                return AppStatus.NOT_STARTED
            } else if (line.equals("App $appName not found")) {
                return AppStatus.NOT_FOUND
            }
        }
        throw RuntimeException("Could not parse the output from $command")
    }

    fun renameApp(oldAppName: String, newAppName: String) {
        commandExecutor.executeCommand("cf rename $oldAppName $newAppName");
    }

    fun push(appName: String, manifest: String) {
        commandExecutor.executeCommand("cf push $appName -f $manifest")
    }
}