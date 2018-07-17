(defproject git-remotes "0.1.0"
  :description "Extract git URL recursively from a given directory."
  :url "https://github.com/agilecreativity/git-remote"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-binplus "0.6.4"]
            [lein-cljfmt "0.5.7"]
            [lein-kibit "0.1.6"]
            [lein-auto "0.1.3"]]
  :profiles {:dev {:dependencies [[lein-binplus "0.6.4"]]}
             :uberjar {:aot :all}}
  :bin {:name "git-remotes"
        :bin-path "~/bin"
        :bootclasspath false}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.7"]
                 [clojure-ini "0.0.2"]
                 [circleci/circleci.test "0.4.1"]
                 [akvo/fs "20180618-165206.5f3fb50b"]]
  :aliases {"test" ["run" "-m" "circleci.test/dir" :project/test-paths]
            "tests" ["run" "-m" "circleci.test"]
            "retest" ["run" "-m" "circleci.test.retest"]}
  :main git-remotes.core)
