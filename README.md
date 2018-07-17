# git-remotes

Extract git URL recursively from a given directory.

## Usage

```sh
git clone https://github.com/agilecreativity/git-remotes.git && cd git-remotes

# Generate the self-executed binary to `~/bin/git-remotes`
lein bin

# Confirm that we have the binary
ls -alt ~/bin/git-remotes
```

## Basic Usages `~/bin/git-remote -h`

```
Extract git URL recursively from a given directory

Usage: git-remotes [options]
  -d, --base-dir    DIR   .
  -o, --output-file FILE  ./git-command.sh
  -v, --verbose
  -h, --help

Options:

--base-dir     DIR   path to the base directory, default to current directory.
--output-file  FILE  output file for command, default to 'git-command.sh'
--verbose            print the output to the screen as well as output to file.
```

## Sample Usages


```shell
git-remotes -d ~/projects/typescript -o typescript-repos.sh
#Your output file :  typescript-repos.sh
```

And the content of the `typescript-repos.sh` will be something like:

```text
git-remotes -d ~/projects/typescript -o typescript-repos.sh
#Your output file :  typescript-repos.sh
```

And the content of the `typescript-repos.sh` will be something like:

```shell
#!/usr/bin/env sh
mkdir -p ~/projects/typescript/umbrella--thi-ng && cd ~/projects/typescript/umbrella--thi-ng && git clone git@github.com:thi-ng/umbrella.git ~/projects/typescript/umbrella--thi-ng 2> /dev/null
mkdir -p ~/projects/typescript/graphql-cli--graphcool && cd ~/projects/typescript/graphql-cli--graphcool && git clone git@github.com:graphcool/graphql-cli.git ~/projects/typescript/graphql-cli--graphcool 2> /dev/null
mkdir -p ~/projects/typescript/chromeless--graphcool && cd ~/projects/typescript/chromeless--graphcool && git clone git@github.com:graphcool/chromeless.git ~/projects/typescript/chromeless--graphcool 2> /dev/null
```

## License

Copyright © 2018 Burin Choomnuan

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
