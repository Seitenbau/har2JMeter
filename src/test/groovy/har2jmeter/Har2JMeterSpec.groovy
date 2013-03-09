package har2jmeter

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class Har2JMeterSpec extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder()

    static googleHarFile
    static googleQueryHarFile
    static groovyConsoleHarFile

    def outputFile

    def setupSpec() {
        def resourceFolder = "src/test/resources/har2jmeter"
        googleHarFile = new File(resourceFolder, "www.google.de.har")
        groovyConsoleHarFile = new File(resourceFolder, "groovyconsole.appspot.com.har")
        googleQueryHarFile = new File(resourceFolder, "query.google.de.har")
    }

    def setup() {
        outputFile = folder.newFile()
    }

    def cleanup() {
        assert folder.folder.deleteDir(): "Output folder should be clean up."
    }

    def createGoogleJmx() {
        given:
            Har2JMeter har2jMeter = new Har2JMeter()
        when:
            har2jMeter.convert(googleHarFile, outputFile)
        then:
            assertXMLEqual(outputFile.text,
                    har2jMeter.toJmx([new JMeterHttpSampler(
                            url: new URL("https://www.google.de/"),
                            method: "GET",
                            headers: [
                                    host: "www.google.de"
                            ]
                    )]))
    }

    def createGoogleJmxWithNoHeaders() {
        given:
            Har2JMeter har2jMeter = new Har2JMeter(withHttpHeaders: false)
        when:
            har2jMeter.convert(googleHarFile, outputFile)
        then:
            assertXMLEqual(outputFile.text,
                    har2jMeter.toJmx([new JMeterHttpSampler(
                            url: new URL("https://www.google.de/"),
                            method: "GET",
                    )]))
    }

    def createGroovyConsoleJmx() {
        given:
            Har2JMeter har2jMeter = new Har2JMeter()
        when:
            har2jMeter.convert(groovyConsoleHarFile, outputFile)
        then:
            assertXMLEqual(outputFile.text, har2jMeter.toJmx([ new JMeterHttpSampler(
                 url:       new URL("http://groovyconsole.appspot.com/executor.groovy"),
                 method:    "POST",
                 headers:   ["Origin":"http://groovyconsole.appspot.com"],
                 arguments: ["script":"println+%22Hello+World%22%0A"]
            )]))
    }

    def createGoogleJmxWithQueryParams() {
        given:
            Har2JMeter har2jMeter = new Har2JMeter()
        when:
            har2jMeter.convert(googleQueryHarFile, outputFile)
        then:
            assertXMLEqual(outputFile.text, har2jMeter.toJmx([ new JMeterHttpSampler(
                    url:       new URL("http://www.google.de/s"),
                    method:    "GET",
                    arguments: ["hl":"de"]
            )]))
    }

}
