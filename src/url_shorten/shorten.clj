(ns url-shorten.shorten
  (:require [clj-redis.client :as redis]))

(def ^:private alphabetics
  "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")

(def ^:private alpha->num (zipmap alphabetics (range)))

(def ^:private alpha-len (count alphabetics))

(defn encode-id 
  "Encode an ID to a string of characters"
  [id]
  (apply str
         (if (= id 0)
           (first alphabetics)
           (loop [n id chars []]
             (if (= n 0)
               chars
               (recur (quot n alpha-len)
                      (conj chars (nth alphabetics (mod n alpha-len)))))))))

(defn decode-id 
  "Decode an ID to a number"
  [idstr]
  (apply + (map-indexed
            #(* (long (Math/pow alpha-len %1)) (alpha->num %2))
            idstr)))

(def ^:private counter-key "urlshorten.idcounter")

(def ^:private url-id-key "urlshorten.url2id")

(def ^:private id-url-key "urlshorten.id2url")

(def ^:private url-access "urlshorten.access")

(defonce db (redis/init))

(redis/setnx db counter-key "10000")

(defn shorten-url!
  "Shorten an URL"
  [url]
  (or (redis/hget db url-id-key url)
      (let [id (encode-id (redis/incr db counter-key))]
        (redis/hset db url-id-key url id)
        (redis/hset db id-url-key id url)
        (redis/zadd db url-access 0 id)
        id)))

(defn- get-real-url
  "Decode the given id and find the real URL"
  [id]
  (redis/hget db id-url-key id))

(defn access-url!
  "Access an URL, increment the access number"
  [id]
  (when-let [url (get-real-url id)]
    (redis/zincrby db url-access 1 id)
    url))

(defn get-urls
  "Get a list of the ID and URLs"
  []
  (let [ids (redis/zrevrange db url-access 0 50)]
    (map vector ids (apply redis/hmget db id-url-key ids))))
