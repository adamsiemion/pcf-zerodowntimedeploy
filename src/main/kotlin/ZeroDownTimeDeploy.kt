import java.util.*

class ZeroDownTimeDeploy(val cloudFoundryClient: CloudFoundryClient) {

    fun deploy(appName: String, manifest: String) {
        val newAppName = appName
        val oldAppName = "$appName-old"

        if (cloudFoundryClient.appStatus(oldAppName) != CloudFoundryClient.AppStatus.NOT_FOUND) {
            throw RuntimeException("Old application $oldAppName exists")
        }
        cloudFoundryClient.renameApp(oldAppName, newAppName)
        cloudFoundryClient.push(appName, manifest)

    }
}

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
        val exitValue = proc.exitValue()
        if (exitValue != 0) {
            throw RuntimeException("Command \"$command\" returned non-zero exit code: $exitValue")
        }
        logger.log("Command \"$command\" executed successfully")
        return output
    }
}

class Logger {
    fun log(message: String) {
        println(message)
    }
}