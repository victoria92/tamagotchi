(ns tamagotchi.core-test
  (:use clojure.test
        tamagotchi.core))

(deftest tamagotchi-test

  (testing "Initialization of panda"
    (is (= {:name "Kristofer"
            :life 100
            :health 100
            :feed 100
            :sleep 100
            :play 100
            :clean 100
            :happiness 100}
           @(new-panda "Kristofer"))))

  (testing "Calculate happiness of a panda"
    (is (= (calculate-happiness {:name "Kristofer"
                                 :health 100
                                 :feed 50
                                 :sleep 50
                                 :play 50
                                 :clean 0})
            50)))

   (testing "Get full state"
    (is (= {:name "Kristofer"
            :life 100
            :health 100
            :feed 100
            :sleep 100
            :play 100
            :clean 100
            :happiness 100}
            (get-state @(new-panda "Kristofer")))))

   (testing "Get component state"
    (is (= 100 (get-state @(new-panda "Kristofer") :feed))))

   (testing "Is panda still alive"
    (is (= true (is-alive? @(new-panda "Kristofer")))))

   (testing "Is panda death"
    (is (= false (is-alive? {:name "Kristofer" :happiness 0}))))

   (testing "Is panda health"
    (is (= false (is-sick? {:name "Kristofer" :happiness 25}))))

   (testing "Is panda sick"
    (is (= true (is-sick? {:name "Kristofer" :happiness 5}))))

   (testing "Needs of panda"
    (is (= 95 (get-state (needs @(new-panda "Kristofer") :feed) :feed))))

   (testing "Take care for panda"
    (is (= 55 (:feed (care {:name "Kristofer"
                                 :health 100
                                 :feed 50
                                 :sleep 50
                                 :play 50
                                 :clean 0}
                            :feed))))))

(run-tests)