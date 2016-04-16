#!/bin/bash

echo "$(tput setaf 3) start: installing Debug symbols $(tput sgr0)"
# 1. Debug symbols for openssl
echo "$(tput setaf 3) installing openssl-dbgsym $(tput sgr0)"
sudo apt-get --yes install openssl-dbgsym
# 2. Debug symbols for vim
echo "$(tput setaf 3) installing openssl-dbgsym $(tput sgr0)"
sudo apt-get --yes install vim-dbg
# 3. Debug symbols for xserver
sudo apt-get --yes install xserver-xorg-core-dbg
echo "$(tput setaf 3) done: installing Debug symbols $(tput sgr0)"

