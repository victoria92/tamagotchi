(ns tamagotchi.core
  (:use seesaw.core))

; (import '(javax.imageio ImageIO)
;         '(java.io File))

(native!)

(defn new-panda
  "Initialize new panda"
  [name]
  (agent {
    :name name
    :life 100
    :health 100
    :feed 100
    :sleep 100
    :play 100
    :clean 100
    :happiness 100
  }))

(defn calculate-happiness
  [panda]
  (/ (+ (:health panda) (:feed panda) (:sleep panda) (:play panda) (:clean panda)) 5))

(defn care
  "Takes panda and action we have to
  make to care for the animal"
  [panda action]
  (let [old-value (action panda)
        new-value (if (> old-value 95) 200 (+ (action panda) 5))]
      (assoc (assoc panda action new-value) :happiness (calculate-happiness panda))))

(defn get-state
  [panda & component]
  (if (= nil component) panda ((first component) panda)))

(defn get-string-state
  [panda]
  (pr-str panda))

(defn needs
  [panda need]
  (let [old-value (need panda)
        new-value (if (< old-value 5) 0 (- (need panda) 5))]
      (Thread/sleep (* (rand-int 7) 1000))
      (assoc (assoc panda need new-value) :happiness (calculate-happiness panda))))

(defn is-alive?
  [panda]
  (> (:happiness panda) 0))

(defn is-sick?
  [panda]
  (and (is-alive? panda) (< (:happiness panda) 10)))

(def animal (new-panda "Kristofer"))

(def eat (button :text "Feed"))
(def sleep (button :text "Feed"))
(def clean (button :text "Feed"))
(def start-game (button :text "Start"))

(defn make-panel
  []
  (vertical-panel :items [(get-string-state @animal)
                          eat
                          sleep
                          clean
                          start-game
                          (make-widget (label :icon "http://www.pandanda.com/web/images/sm_henry_hardhat.jpg"))]))

(def main-frame (frame :title "Tamagotchi"
                  :size [800 :by 480]
                  :content (make-panel)))

(defn all-needs
  [panda]
  (while (is-alive? @panda)
    (send panda needs :feed)
    (config! main-frame :content (make-panel))))

(defn -main [& args]
  (invoke-later
    (send animal needs :feed)
    (config! main-frame :content (make-panel))
    (listen main-frame
      :item-state-changed (fn [e]
                            (send animal needs :feed)
                            (config! main-frame :content (make-panel))))
    (listen eat
      :mouse-clicked (fn [e]
                      (send animal care :feed)
                      (config! main-frame :content (make-panel))))
    (listen sleep
      :mouse-clicked (fn [e]
                      (send animal care :sleep)
                      (config! main-frame :content (make-panel))))
    (listen clean
      :mouse-clicked (fn [e]
                      (send animal care :clean)
                      (config! main-frame :content (make-panel))))
    (listen start-game
      :mouse-clicked (fn [e]
                        (all-needs animal)))
    (show! main-frame)))