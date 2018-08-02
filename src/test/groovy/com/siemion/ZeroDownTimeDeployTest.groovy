package com.siemion

import com.siemion.CloudFoundryClient
import com.siemion.CloudFoundryClient.AppStatus
import com.siemion.ZeroDownTimeDeploy
import de.jodamob.kotlin.testrunner.SpotlinTestRunner
import org.junit.runner.RunWith
import spock.lang.Specification
import spock.lang.Unroll

@RunWith(SpotlinTestRunner)
class ZeroDownTimeDeployTest extends Specification {

    def "should fail when the old application exists"() {
        given:
        def cloudFoundryClient = Mock(CloudFoundryClient)
        cloudFoundryClient.appStatus("app-old") >> AppStatus.STARTED
        def zeroDownTimeDeploy = new ZeroDownTimeDeploy(cloudFoundryClient)

        when:
        zeroDownTimeDeploy.deploy("app", "manifest")

        then:
        thrown RuntimeException
    }

    def "should rename, push when the old application not exists"() {
        given:
        def cloudFoundryClient = Mock(CloudFoundryClient)
        cloudFoundryClient.appStatus("app-old") >> AppStatus.NOT_FOUND
        def zeroDownTimeDeploy = new ZeroDownTimeDeploy(cloudFoundryClient)

        when:
        zeroDownTimeDeploy.deploy("app", "manifest")

        then:
        1 * cloudFoundryClient.renameApp("app", "app-old")
        1 * cloudFoundryClient.push("app", "manifest")
    }

}
