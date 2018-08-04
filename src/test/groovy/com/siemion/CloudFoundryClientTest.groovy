package com.siemion

import com.siemion.CloudFoundryClient.AppStatus
import de.jodamob.kotlin.testrunner.SpotlinTestRunner
import org.junit.runner.RunWith
import spock.lang.Specification
import spock.lang.Unroll

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

    @Unroll
    def "status should return status #status"(String commandOutput, AppStatus status) {
        expect:
        executeAppStatus(commandOutput) == status

        where:
        commandOutput | status
        "App app not found" | AppStatus.NOT_FOUND
        "requested state: started" | AppStatus.STARTED
        "requested state: starting" | AppStatus.NOT_STARTED
    }

    def executeAppStatus(String output) {
        commandExecutor.executeCommand(_) >> [output]
        return cloudFoundryClient.appStatus("app")
    }

    def "status should fail when unsupported output is returned"() {
        given:
        commandExecutor.executeCommand(_) >> ["unsupported output"]

        when:
        cloudFoundryClient.appStatus("app")

        then:
        thrown IllegalStateException
    }
}
