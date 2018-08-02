package com.siemion

import com.siemion.CloudFoundryClient.AppStatus
import de.jodamob.kotlin.testrunner.SpotlinTestRunner
import org.junit.runner.RunWith
import spock.lang.Specification

@RunWith(SpotlinTestRunner.class)
class CloudFoundryClientTest extends Specification {

    CommandExecutor commandExecutor
    CloudFoundryClient cloudFoundryClient

    def setup() {
        commandExecutor = Mock(CommandExecutor)
        cloudFoundryClient = new CloudFoundryClient(commandExecutor)
    }

    def "rename should call command executor"() {
        when:
        cloudFoundryClient.renameApp("old", "new")

        then:
        1 * commandExecutor.executeCommand("cf rename old new")
    }

    def "push should call command executor"() {
        when:
        cloudFoundryClient.push("app", "manifest")

        then:
        1 * commandExecutor.executeCommand("cf push app -f manifest")
    }

//    def "status should call command executor"() {
//        given:
//        commandExecutor.executeCommand(_) >> ["x"]
//
//        when:
//        cloudFoundryClient.appStatus("app")
//
//        then:
//        1 * commandExecutor.executeCommand("cf app app")
//    }


    def "status should return gg"(String output, AppStatus expectedStatus) {
        given:
//        commandExecutor.executeCommand(_) >> [output]

        when:
        def status = cloudFoundryClient.appStatus("app")

        then:
        status == expectedStatus

        where:
        expectedStatus | output
        com.siemion.CloudFoundryClient.AppStatus.NOT_FOUND | "App app not found"
    }

    def "status should return NOT_FOUND"() {
        given:
        commandExecutor.executeCommand(_) >> ["App app not found"]

        when:
        def status = cloudFoundryClient.appStatus("app")

        then:
        status == CloudFoundryClient.AppStatus.NOT_FOUND
    }

    def "status should return STARTED"() {
        given:
        commandExecutor.executeCommand(_) >> ["requested state: started"]

        when:
        def status = cloudFoundryClient.appStatus("app")

        then:
        status == CloudFoundryClient.AppStatus.STARTED
    }

    def "status should return NOT_STARTED"() {
        given:
        commandExecutor.executeCommand(_) >> ["requested state: starting"]

        when:
        def status = cloudFoundryClient.appStatus("app")

        then:
        status == CloudFoundryClient.AppStatus.NOT_STARTED
    }
}
