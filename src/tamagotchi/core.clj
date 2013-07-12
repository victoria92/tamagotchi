(ns tamagotchi.core)

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
        new-value (if (> old-value 95) 100 (+ (action panda) 5))]
      (assoc (assoc panda action new-value) :happiness (calculate-happiness panda))))

(defn get-state
  [panda & component]
  (if (= nil component) panda ((first component) panda)))

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

(defn main
  [panda]
  (while (is-alive? @panda)
    (send panda needs :feed)))