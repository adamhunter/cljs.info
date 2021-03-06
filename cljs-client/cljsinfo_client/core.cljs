(ns cljsinfo-client.core
  (:require
    [cljsinfo-client.threading-macro :as threading-macro]
    [cljsinfo-client.state :refer [current-size]]
    [cljsinfo-client.util :refer [js-log log]]))

(def $ js/jQuery)
(def $body ($ "body"))
(def $window ($ js/window))

;;------------------------------------------------------------------------------
;; Window Size
;;------------------------------------------------------------------------------

;; NOTE: this could probably be replaced with CSS media-queries, but it might
;; come in handy to have it in an atom at some point

(def widest-layout-class "widest-6792e")
(def regular-layout-class "reg-width-aa3fc")

(def layout-classes (str widest-layout-class " "
                         regular-layout-class))

(defn- width->size [w]
  (cond
    (>= w 1200) :widest
    (>= w 960)  :regular
    :else       :regular))

(defn- on-change-size [_ _ _ new-size]
  (.removeClass $body layout-classes)
  (.addClass $body
    (case new-size
      :widest  widest-layout-class
      :regular regular-layout-class
      :else nil)))

(add-watch current-size :change on-change-size)

(defn- on-window-resize []
  (let [width (.width $window)
        size (width->size width)]
    (when (not= size @current-size)
      (reset! current-size size))))

;; trigger initial resize
(on-window-resize)

;;------------------------------------------------------------------------------
;; Global Events
;;------------------------------------------------------------------------------

(defn- docs-page-init! []
  (apply (aget js/hljs "initHighlightingOnLoad") []))

;;------------------------------------------------------------------------------
;; Global Events
;;------------------------------------------------------------------------------

;; some global events
(defn- add-events! []
  (.resize $window on-window-resize))

;;------------------------------------------------------------------------------
;; Global Client Init
;;------------------------------------------------------------------------------

(defn- init! [page]
  ;; add global events
  (add-events!)

  ;; initialize a specific page
  (case page
    "threading-macro" (threading-macro/init!)
    "docs"            (docs-page-init!)
    nil))

(js/goog.exportSymbol "CLJSINFO.init" init!)
