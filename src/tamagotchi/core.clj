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
    :life 50
    :health 50
    :feed 50
    :sleep 50
    :play 50
    :clean 50
    :happiness 50
  }))

(defn calculate-happiness
  [panda]
  (/ (+ (:health panda) (:feed panda) (:sleep panda) (:play panda) (:clean panda)) 5))

(defn care
  "Takes panda and action we have to
  make to care for the animal"
  [panda action]
  (let [old-value (action panda)
        new-value (if (> old-value 95) 100 (+ (action panda) 5))]
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
        new-value (if (< old-value 5) 0 (- (need panda) (rand-int 5)))]
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
(def sleep (button :text "Sleep"))
(def clean (button :text "Clean"))
(def start-game (button :text "Start"))
(def play (button :text "Play"))
(def vet (button :text "Go to vet"))

(defn make-panel
  []
  (grid-panel :columns 2
              :items [eat
                      (progress-bar :orientation :horizontal :min 0 :max 100 :value (get-state @animal :feed))
                      sleep
                      (progress-bar :orientation :horizontal :min 0 :max 100 :value (get-state @animal :sleep))
                      clean
                      (progress-bar :orientation :horizontal :min 0 :max 100 :value (get-state @animal :clean))
                      play
                      (progress-bar :orientation :horizontal :min 0 :max 100 :value (get-state @animal :play))
                      vet
                      (progress-bar :orientation :horizontal :min 0 :max 100 :value (get-state @animal :health))
                      "Happiness :"
                      (progress-bar :orientation :horizontal :min 0 :max 100 :value (get-state @animal :happiness))
                      (make-widget (label :icon "http://www.pandanda.com/web/images/sm_henry_hardhat.jpg"))]))

(def main-frame (frame :title "Tamagotchi"
                  :size [800 :by 800]
                  :content (make-panel)))

(defn all-needs
  [panda]
  (while (is-alive? @panda)
    (send-off panda needs :feed)
    (config! main-frame :content (make-panel))))

(def a (agent 0))

(defn -main [& args]

  ; (while true

    ; ((send a (fn [e] (do
    ;                   (send animal needs :sleep)
    ;                   (config! main-frame :content (make-panel)))))

    ; (.start (Thread. (timer (fn [e] (do
    ;                   (send animal needs :feed)
    ;                   (config! main-frame :content (make-panel)))))))

    ; (send a (fn [e] (timer (fn [e] (do
    ;                   (send animal needs :sleep)
    ;                   (config! main-frame :content (make-panel)))))))

    ; (timer (fn [e] (do
    ;                   (send animal needs :play)
    ;                   (config! main-frame :content (make-panel)))))

    ; (timer (fn [e] (do
    ;                   (send animal needs :health)
    ;                   (config! main-frame :content (make-panel)))))

    ; (timer (fn [e] (do
    ;                   (send animal needs :clean)
    ;                   (config! main-frame :content (make-panel)))))
    ; (.start (Thread. (while true (send a (fn [e] (do
    ;                                               (send animal needs :sleep)
    ;                                               (Thread/sleep 3000)
    ;                                               (config! main-frame :content (make-panel))))))))

    (invoke-later

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
      (listen play
        :mouse-clicked (fn [e]
                        (send animal care :play)
                        (config! main-frame :content (make-panel))))
      (listen vet
        :mouse-clicked (fn [e]
                        (send animal care :health)
                        (config! main-frame :content (make-panel))))
      (listen start-game
        :mouse-clicked (fn [e]
                          true))
      (show! main-frame)))