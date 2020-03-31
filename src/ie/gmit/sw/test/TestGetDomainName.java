package ie.gmit.sw.test;

import ie.gmit.sw.DomainFrequency;

public class TestGetDomainName {
    public static void main(String[] args) {
        String[] testUrls = {
                "https://www.google.ie/",
                "http://www.google.ie",
                "http://www.google.ie",
                "http://ww3.google.ie",
                "http://google.ie",
                "http://192.168.0.28/index.html",
                "http://usa.gov"
        };

        String domain;
        for (String testUrl : testUrls) {
            domain = DomainFrequency.getDomainName(testUrl);
            System.out.printf("%s => %s%n", testUrl, domain);
        }
    }
}
