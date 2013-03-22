(ns artscentre.schema)

(def ShortUri [{:db/ident        :shorturi/alias
                :db/valueType    :db.type/string
                :db/cardinality  :db.cardinality/one
                :db/unique       :db.unique/identity
                :db/id #db/id[:db.part/db]
                :db.install/_attribute :db.part/db}

               {:db/ident        :shorturi/uri
                :db/valueType    :db.type/string
                :db/cardinality  :db.cardinality/one
                :db/id #db/id[:db.part/db]
                :db.install/_attribute :db.part/db}

               {:db/ident        :shorturi/owner
                :db/valueType    :db.type/ref
                :db/cardinality  :db.cardinality/one
                :db/id #db/id [:db.part/db]
                :db.install/_attribute :db.part/db}])

(def User [{:db/ident        :user/email
            :db/valueType    :db.type/string
            :db/cardinality  :db.cardinality/one
            :db/unique       :db.unique/identity
            :db/id #db/id[:db.part/db]
            :db.install/_attribute :db.part/db}

           {:db/ident        :user/name
            :db/valueType    :db.type/string
            :db/cardinality  :db.cardinality/one
            :db/id #db/id[:db.part/db]
            :db.install/_attribute :db.part/db}

           {:db/ident        :user/following
            :db/valueType    :db.type/ref
            :db/cardinality  :db.cardinality/many
            :db/id #db/id[:db.part/db]
            :db.install/_attribute :db.part/db}

           {:db/ident        :user/favorites
            :db/valueType    :db.type/ref
            :db/cardinality  :db.cardinality/many
            :db/id #db/id[:db.part/db]
            :db.install/_attribute :db.part/db}])
