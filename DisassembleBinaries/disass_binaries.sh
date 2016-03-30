#!/bin/bash

rm disass_all.sh


# sudo apt-get install openssl-dbgsym # ??
# sudo apt-get install vim-dbg # ok


#sudo apt-get install xserver-xorg-core-dbg
#or
#sudo apt-get install xserver-xorg-core-dbgsym


## gather functions of following binaries for disassembling - only functions declared in binary will be considered (externals are excluded)
./disass_func.sh /home/lvr/src/Area51/SomeFunctionCalls/bin/Debug/SomeFunctionCalls
#./disass_func.sh /usr/bin/shotwell
#./disass_func.sh /usr/bin/openssl
./disass_func.sh /usr/bin/vim

## start disassembling functions
chmod +x disass_all.sh
./disass_all.sh
echo "$(tput setaf 3)all done! :) $(tput sgr0)";
