(ns url-shorten.common
  (:use [noir.core :only [defpartial defpage url-for]]
        [hiccup.page :only [include-js include-css html5]]
        [hiccup.element :only [mail-to link-to]]))

(def links
  [["Clojure" "http://clojure.org"]
   ["Noir" "http://www.webnoir.org"]
   ["Blog" "http://jerrypeng.me"]
   ["Github" "http://github.com/moonranger/url-shorten"]])

(defpartial layout [& content]
  (html5
   [:head
    [:title "Little URL Shortener"]
    (include-css "/css/bootstrap.css")
    (include-css "/css/bootstrap-responsive.css")
    (include-css "/css/docs.css")]
   [:body
    [:a {:href "https://github.com/moonranger/url-shorten"}
     [:img {:style "position: absolute; z-index: 9999; top: 0; right: 0; border: 0;"
            :src "https://s3.amazonaws.com/github/ribbons/forkme_right_orange_ff7600.png"
            :alt "Fork me on Github"}]]
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.navbar-inner
      [:div.container
       [:button.btn.btn-navbar {:type "button"
                                :data-toggle "collapse"
                                :data-target ".nav-collapse"}
        [:span.icon-bar]
        [:span.icon-bar]
        [:span.icon-bar]]
       [:div.nav-collapse.collapse
        [:ul.nav
         [:li (link-to "/" "Home")]
         [:li (link-to "/about/me" "About")]]]]]]
    [:div.container
     content]
    [:footer.footer
     [:div.container
      [:p "Powered by Clojure and Noir"]
      [:ul.footer-links
       (interleave (map (fn [[k v]] [:li (link-to v k)]) links)
                   (repeat [:li.muted "&middot;"]))]]]
    (include-js "/js/jquery.js")
    (include-js "/js/bootstrap.js")]))

(defpage "/about/me" []
  (layout
   [:h3 "Jerry's first Noir project, a small URL shortener."]
   [:p "Clojure is cool, Noir is cool, this is only a small demo
        showing how cool they could be!"]))
