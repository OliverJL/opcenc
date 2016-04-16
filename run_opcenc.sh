#!/bin/bash

export DATA_ROOT_DIR=dataexch

if [ -d "$DATA_ROOT_DIR" ];
then
echo "$(tput setaf 5)erasing $DATA_ROOT_DIR$(tput sgr0)"
fi
echo "$(tput setaf 5)creating $DATA_ROOT_DIR$(tput sgr0)"
mkdir $DATA_ROOT_DIR;

# set scripts executable
chmod +x 00_apps/install_apps.sh

## define applications to be analysed
# appname
declare -a app_names=("openssl" "vim")
# debugsymbol package names (for apt-get)
declare -a app_debug_sym=("openssl-dbgsym" "vim-dbg")
# application paths (incl. application name) 
#declare -a app_paths=("/usr/bin/openssl" "/usr/bin/vim") 
declare -a app_paths=("/usr/bin/openssl") 

#### (  I. ) install all programms to be analysed
echo "$(tput setaf 3) start: installing Debug symbols $(tput sgr0)"
for app_debug_sym_pckg in "${app_debug_sym[@]}"
do
	echo "$(tput setaf 3) installing $app_debug_sym_pckg $(tput sgr0)"
	sudo apt-get --yes install $app_debug_sym_pckg
done
echo "$(tput setaf 3) done: installing Debug symbols $(tput sgr0)"
#### (  I. ) done

#### (  II. ) Disassemble files, extract funtions:
####	a. call gdb to list all funtions, etract those from list
echo "$(tput setaf 3) start: extracting function lists $(tput sgr0)"
for app_path in "${app_paths[@]}"
do
	echo "$(tput setaf 3) extracting function for $app_path $(tput sgr0)"
	./01_DisassembleBinaries/disass_func.sh $app_path
	#gdb $app_path -x 
done
echo "$(tput setaf 3) done: extracting function lists $(tput sgr0)"
####	b. call gdb to disassemle each function 

# gdb /home/lvr/src/Area51/SomeFunctionCalls/bin/Debug/SomeFunctionCalls -x /home/lvr/src/Area51/dissas/out/SomeFunctionCalls/cfg/disassemble_SomeFunctionCalls_functions.0.cfg >> /home/lvr/src/Area51/dissas/out/SomeFunctionCalls/exp/func_disass.0.dis;
#sleep 10

