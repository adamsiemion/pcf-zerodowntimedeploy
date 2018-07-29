package com.siemion

import kotlin.system.exitProcess

fun main(args : Array<String>) {

    if(args.size != 2) {
        println("Usage: <appName> <manifest>")
        exitProcess(1)
    } else {
        return ZeroDownTimeDeploy(CloudFoundryClient(CommandExecutor(Logger())))
                .deploy(args[0], args[1])
    }
}