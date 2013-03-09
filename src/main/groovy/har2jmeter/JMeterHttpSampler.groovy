package har2jmeter

class JMeterHttpSampler {

    URL url

    String method

    Map headers = [:]

    Map arguments = [:]

    def getPort() {
        url.port != -1 ? url.port : ("HTTPS".equalsIgnoreCase(url.protocol) ? 443 : 80)
    }

    def getDomain() {
        url.host
    }

    def getProtocol() {
        url.protocol
    }

    def getPath() {
        url.path
    }

}
