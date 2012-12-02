(ns url-shorten.views
  (:use [url-shorten.common]
        [url-shorten.shorten]
        [noir.core :only [defpage defpartial]]
        [hiccup.form :only [form-to text-field submit-button]]
        [hiccup.element :only [link-to]])
  (:require [noir.response :as resp]
            [noir.request :as req]))

(defn- short-url-link
  [id]
  (let [{:keys [scheme server-name server-port]} (req/ring-request)
        host (if (= server-port 80)
               server-name
               (str server-name ":" server-port))]
    (link-to (str "/" id)
             (str (name scheme) "://" host "/" id))))

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
              (submit-button {:class "btn btn-success span2 offset2"}
                             "Shorten It!"))
     [:legend "Hottest URLs:"]
     [:table.table.table-hover
      [:thead
       [:tr [:td "Short URL"] [:td "Original URL"]]]
      [:tbody
       (map (fn [[id url]]
              [:tr [:td (short-url-link id)] [:td (link-to url url)]])
            (get-urls))]]
      ]]))

(defpage [:post "/"] {:keys [url]}
  (layout
   [:h3 "Here is your shortened URL"]
   [:h3 (short-url-link (shorten-url! url))]
   [:p (link-to "/" "Back")]))

(defpage "/:id" {:keys [id]}
  (if-let [url (access-url! id)]
    (resp/redirect url)
    (resp/status
     404
     (layout
      [:h3 "404 Not Found"]
      [:div.alert
       [:strong "Oops!"] "I'm sorry but the URL is not found on this site."]
      [:p (link-to "/" "Back")]))))


