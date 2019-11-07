(ns git-remotes.core
  (:require [clojure-ini.core :refer [read-ini]]
            [clojure.java.io :as io :refer [file]]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts] :as cli]
            [me.raynes.fs :as fs :refer [expand-home]]
            [git-remotes.option :refer [usage
                                        error-message
                                        exit
                                        cli-spec]])
  (:import java.nio.file.FileSystems)
  (:gen-class))

(defn- remote-label
  [git-label]
  (-> git-label
      (str/replace #"remote\s+" "")
      (str/replace #"\"" "")))

(defn git-config
  ([]
   (git-config "."))
  ([base-dir]
   (let [grammar-matcher (.getPathMatcher
                          (FileSystems/getDefault)
                          "glob:config")]
     (->> base-dir
          expand-home
          str
          file
          file-seq
          (filter #(.isFile %))
          (filter #(and (.matches grammar-matcher (.getFileName (.toPath %)))
                        (.endsWith (.toPath %) ".git/config")))
          (mapv #(.getAbsolutePath %))))))

(defn git-url
  ([]
   (git-url ".git/config"))
  ([git-config]
   (let [config (read-ini (str (expand-home git-config)) :comment-char \#)]
     (filter identity (map (fn [[key value]]
                             (when (str/starts-with? key "remote")
                               {(remote-label key) (value "url")}))
                           config)))))

(defn- url-pairs
  [url]
  [(str/replace url #"/.git/config" "") (flatten (git-url url))])

(defn extract-git-urls
  [base-dir]
  (let [configs (git-config base-dir)]
    (map url-pairs configs)))

(defn- custom-url
  "Extract url having specific pattern.

  (custom-url \"git@github.com:agilecreativity/borkdude--jet.git\")
  ;;=> {:owner \"borkdude\" :repo \"jet\"}

  (custom-url \"git@github.com:agilecreativity/jet.git\")
  ;;=> nil"
  [full-url]
  (let [url (clojure.string/replace
             (subs full-url (+ 1 (clojure.string/last-index-of full-url "/")))
             ".git"
             "")]
    (if-let [[[_ owner repo]] (re-seq #"(\w+)--(\w+)" url)]
      {:owner owner
       :repo repo})))

(defn custom-projects
  [base-dir]
  (let [url-pairs (extract-git-urls base-dir)
        single-remote-fn (fn [x] (= 1 (count (last x))))
        custom-remote-fn (fn [x] (custom-url (last (last (first (last x))))))
        custom-output-fn (fn [x]
                           {(first x)
                            (custom-remote-fn x)})]
    (->> url-pairs
         (filter single-remote-fn)
         (filter custom-remote-fn)
         (map custom-output-fn))))

(comment

  (def apps (custom-projects "~/apps"))

  (count apps) ;;=> 700

  (first apps)
  ;;=> {"/home/bchoomnu/apps/clojurewerkz--elastisch" {:owner "clojurewerkz", :repo "elastisch"}}

  (last apps)
  ;;=> {"/home/bchoomnu/apps/xiaoxinghu--orgajs" {:owner "xiaoxinghu", :repo "orgajs"}}

  ;; TODO: add the following command
  ;; $git remote add upstream git@github.com:agilecreativity/<user>--<repo>.git
  )

(defn append-to-file
  "Uses spit to append to a file specified with its name as a string, or
   anything else that writer can take as an argument.  s is the string to
   append."
  [file-name s]
  (spit file-name s :append true))

(defn- git-command
  [dir url]
  (format "mkdir -p %s && cd %s && git clone %s %s 2> /dev/null\n" dir dir url dir))

;; TODO: there must be a better ways to make this less ugly!
(defn clone-repos
  ([repo-pairs]
   (clone-repos repo-pairs "git-commands.sh"))
  ([repo-pairs output-file]
   (let [output-file (expand-home output-file)]
     (spit output-file "#!/usr/bin/env sh\n")
     (doseq [[dir urls] repo-pairs]
       (let [k (first urls)
             label (first (keys k))
             url (first (vals k))
             ;; To keep the code portable, we like to use "~" so we could have different $HOME in different machine!
             dir (str/replace (str (expand-home dir)) (System/getenv "HOME") "~")]
         (if (= label "origin")
           (spit output-file (git-command dir url) :append true))))
     (println "Your output file : " (str (expand-home output-file))))))

#_(clone-repos (extract-git-urls "~/apps"))
#_(clone-repos (extract-git-urls "~/projects/personal") "sample.sh")

(defn -main [& args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-spec)]
    (cond
      ;; show help if the user ask for it
      (:help options)
      (exit 0 (usage summary))

      ;; check if we have valid directory
      (:base-dir options)
      (if (fs/exists? (fs/expand-home (:base-dir options)))
        (clone-repos (extract-git-urls (str (fs/expand-home (:base-dir options))))
                     (str (fs/expand-home (:output-file options))))
        (exit 0 (usage summary)))

      :else
      (exit 0 (usage summary)))))
