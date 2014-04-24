package org.celllife.vmmc.framework.restclient

/**
 * User: Kevin W. Sewell
 * Date: 2013-04-03
 * Time: 13h23
 */
class RESTClient {

    def static get(String uri) {

        def client = new groovyx.net.http.RESTClient(uri)
        client.auth.basic("internal", "password")

        return client.get([:]).data
    }

    def static get(String uri, Map<String, Object> query) {

        def client = new groovyx.net.http.RESTClient(uri)
        client.auth.basic("internal", "password")

        return client.get(query:query).data
    }

    def static post(String uri, Map<String, Object> query) {

        def client = new groovyx.net.http.RESTClient(uri)
        client.auth.basic("internal", "password")

        return client.post(query:query).data

    }

}
