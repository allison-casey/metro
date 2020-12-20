(defn character
  ([type_ faction defense aim health mobility perks abilities]
   (character type_ (name type_) faction defense aim health mobility perks abilities))
  ([type_ name_ faction defense aim health mobility perks abilities]
   {:id (java.util.UUID/randomUUID)
    :type type_
    :name name_
    :faction faction
    :defense defense
    :aim aim
    :health health
    :mobility mobility
    :perks perks
    :abilities abilities
    :cover :none
    :flying? false
    :elevation 0}))

(defn long-str
  ([& strings] (clojure.string/join " " strings)))

(defmacro ability
  [name description & r]
  `(def ~name
     ~(eval description)
     (hash-map :name ~(clojure.core/name name)
               ~@r)))

(ability shoot
         "Take a shot against a single target."
         :cooldown 0
         :type :ceance)

(ability suppression
         (long-str
          "Can fire a special shot that grants reaction fire at a single target"
          "if it moves. The target also suffers a -30 aim penalty")
         :cooldown 0
         :effects [{:effected :defender
                    :func (fn [_ defender _] (update defender :aim #(- % 6)))}])

(ability mind-merge
         (long-str
          "Psionically assist an ally, granting the ally an array of stat bonuses."
          "The Sectoid receiving Mind Merge will have +1 HP, +25 Will and +25 Critical Chance"))

(defmacro defperk
  [name description activation-order affectee func]
  (let [description (eval description)]
    `(def ~name
       ~description
       (hash-map :name ~(clojure.core/name name)
                 :description ~description
                 :activation-order ~activation-order
                 :affectee ~affectee
                 :func ~func))))

;; ACTIVATION ORDERS
;; :on-attack
;; :on-defend
;; :all

;; AFFECTEES
;; :attacker
;; :defender
;; :world

(defperk aggression
         "Confers +1 critical chance per enemy in sight (max +3)"
         :on-attack
         :attacker
         (fn [attacker defender entities]
           (let [aim-mod (-> entities count (min 3))]
             (update attacker #(+ % aim-mod)))))

(defn evasion
  [entity _]
  (if (:flying? entity)
    (update entity :defense #(+ % 4))
    entity))

(defn sectoid
  []
  (character :sectoid :opfor 0 12 3 7 [] [shoot suppression mind-merge]))

(defn thin-man
  []
  (character :thin-man :opfor 0 13 3 9 [] [shoot]))

(defn rookie
  []
  (character :rookie :blufor 0 12 4 7 [aggression] [shoot]))

(def entities
  [(rookie) (sectoid) (sectoid) (thin-man)])

(defn find-by-id [id entities]
  (first (filter #(= (:id %) id) entities)))

(defn filter-by-ids [entities & ids]
  (let [ids (into #{} ids)]
    (into [] (filter #(-> % :id ids not) entities))))

(defn elevated? [{elev-attack :elevation} {elev-defend :elevation}]
  (> elev-attack elev-defend))

(defn calculate-elevation-advantage [attacker defender]
  (if (elevated? attacker defender) 4 0))

(defn calculate-defense-modifier [entity]
  (case (:cover entity)
    :full 8
    :half 4
    :none 0))

(defn resolve-action [{:keys [attacker-id target-id] :as action} action entities]
  (let [attacker (find-by-id attacker-id entities)
        target (find-by-id target-id entities)]
    (print attacker)))

(resolve-action {:attacker-id (:id (first entities))
                 :target-id (:id (second entities))}
                entities)

; (defn calculate-chance-to-hit [attacker defender entities]
;   (let [elevation-mod (calculate-elevation-advantage attacker defender)
;         defense-mod (calculate-defense-modifier defender)
;         aim (-> attacker :aim (+ elevation-mod))
;         defense (-> defender :defense (+ defense-mod))]
;     (- aim defense)))
;
; (let [attacker (-> entities first (update :elevation inc))
;       defender (-> entities second (assoc :cover :full))
;       chance-to-hit (calculate-chance-to-hit attacker defender entities)]
;   chance-to-hit)
