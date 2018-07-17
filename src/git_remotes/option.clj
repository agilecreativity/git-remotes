(ns git-remotes.option
  (:require [clojure.string :as str]))

(def cli-spec
  [["-d" "--base-dir    DIR"  :default "."]
   ["-o" "--output-file FILE" :default "./git-command.sh"]
   ["-v" "--verbose"]
   ["-h" "--help"]])

(defn usage [summary]
  (->> ["Extract git URL recursively from a given directory"
        ""
        "Usage: git-remotes [options]"
        summary
        ""
        "Options:"
        ""
        "--base-dir     DIR   path to the base directory, default to current directory."
        "--output-file  FILE  output file for command, default to 'git-command.sh'"
        "--verbose            print the output to the screen as well as output to file."]
       (str/join \newline)))

(defn error-message [errors]
  (str "The following error occured while parsing your commands: \n\n"
       (str/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))
