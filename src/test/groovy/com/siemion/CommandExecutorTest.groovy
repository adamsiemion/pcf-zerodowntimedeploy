package com.siemion

import de.jodamob.kotlin.testrunner.SpotlinTestRunner
import org.junit.runner.RunWith
import spock.lang.Specification

@RunWith(SpotlinTestRunner)
class CommandExecutorTest extends Specification {

    def "should return three lines"() {
        given:
        def file = File.createTempFile("temp",".tmp")
        file.write("line1\nline2\nline3\n")
        file.deleteOnExit()
        def path = file.absolutePath
        def commandExecutor = new CommandExecutor(Mock(Logger))

        when:
        def output = commandExecutor.executeCommand("cat $path")

        then:
        output == [ "line1", "line2", "line3" ]
    }

    def "should fail when command returned non zero exit code"() {
        given:
        def commandExecutor = new CommandExecutor(Mock(Logger))

        when:
        commandExecutor.executeCommand("cat no-such-file")

        then:
        thrown IllegalStateException
    }

}
