(ns url-shorten.shorten)

; Make the id starts from a bigger number, so that the result URL will
; have at least two characters.
(def ^:private id-counter (ref 1000))

(def ^:private urls (ref {}))

(def ^:private url->id (ref {}))

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

(defn shorten-url!
  "Shorten an URL"
  [url]
  (encode-id
   (or (@url->id url)
       (dosync
        (let [id (alter id-counter inc)]
          (alter urls assoc id url)
          (alter url->id assoc url id)
          id)))))

(defn get-real-url
  "Decode the given id and find the real URL"
  [idstr]
  (let [id (decode-id idstr)]
    (@urls id)))