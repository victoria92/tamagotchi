(ns tamagotchi.gui
  (:use seesaw.core))

(defn -main [& args]
  (invoke-later
    (show! (frame :title "tamagotchi" :size [500, 500] :content (button :text "Push me")))))