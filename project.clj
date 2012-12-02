(defproject url-shorten "0.1.0"
            :description "Little URL Shortener Powered By Noir and Redis"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.3.0-beta3"]
                           [clj-redis "0.0.12"]]
            :main url-shorten.server)

