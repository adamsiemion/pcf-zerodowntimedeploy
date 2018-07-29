import de.jodamob.kotlin.testrunner.SpotlinTestRunner
import org.junit.runner.RunWith
import spock.lang.Specification

@RunWith(KotlinTestRunner::class)
class ZeroDownTimeDeploySpecification extends Specification {

    def "should fail when the old application exists"() {
        given:
        def cloudFoundryClient = Mock(CloudFoundryClient)
        cloudFoundryClient.appStatus("app-old") >> CloudFoundryClient.AppStatus.STARTED
        def zeroDownTimeDeploy = new ZeroDownTimeDeploy(cloudFoundryClient)

        when:
        zeroDownTimeDeploy.deploy("app", "manifest")

        then:
        thrown RuntimeException
    }
}
