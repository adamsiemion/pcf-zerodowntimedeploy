package com.siemion

class ZeroDownTimeDeploy(val cloudFoundryClient: CloudFoundryClient) {

    fun deploy(appName: String, manifest: String) {
        val oldAppName = "$appName-old"

        if (cloudFoundryClient.appStatus(oldAppName) != CloudFoundryClient.AppStatus.NOT_FOUND) {
            throw IllegalStateException("Old application $oldAppName exists")
        }
        cloudFoundryClient.renameApp(appName, oldAppName)
        cloudFoundryClient.push(appName, manifest)
    }
}
