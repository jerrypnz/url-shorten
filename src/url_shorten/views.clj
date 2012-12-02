(ns url-shorten.views
  (:use [url-shorten.common :only [layout]]
        [url-shorten.shorten :only [shorten-url! get-real-url]]
        [noir.core :only [defpage defpartial]]
        [hiccup.form :only [form-to text-field submit-button]]
        [hiccup.element :only [link-to]])
  (:require [noir.response :as resp]
            [noir.request :as req]))

(defpage "/" []
  (layout
   [:h3 "Welcome to Little URL Shortener"]
   [:div.row-fluid
    [:div.span12
     (form-to {:class "form-inline"}
              [:post "/"]
              [:input {:type "url"
                       :name "url"
                       :class "span8"
                       :place-holder "Please input an URL"}]
              (submit-button {:class "btn btn-success span3 offset1"}
                             "Shorten It!"))]]))

(defpage [:post "/"] {:keys [url]}
  (layout
   [:h3 "Here is your shortened URL"]
   [:h3 (let [idstr (shorten-url! url)
              surl (str "/" idstr)
              {:keys [scheme server-name server-port]} (req/ring-request)
              host (if (= server-port 80)
                     server-name
                     (str server-name ":" server-port))]
          (link-to surl
                   (str (name scheme) "://" host surl)))]
   [:p (link-to "/" "Back")]))

(defpage "/:idstr" {:keys [idstr]}
  (if-let [url (get-real-url idstr)]
    (resp/redirect url)
    (resp/status
     404
     (layout
      [:h3 "404 Not Found"]
      [:div.alert
       [:strong "Oops!"] "I'm sorry but the URL is not found on this site."]
      [:p (link-to "/" "Back")]))))


