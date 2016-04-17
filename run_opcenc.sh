#!/bin/bash

reset

rm ./disass_all.sh

export DATA_ROOT_DIR=dataexch
keyFilesRootDir=/home/lvr/src/opcenc/opcenc/Keys/key

#if [ -d "$DATA_ROOT_DIR" ];
#then
#echo "$(tput setaf 5)erasing $DATA_ROOT_DIR$(tput sgr0)"
#fi
#echo "$(tput setaf 5)creating $DATA_ROOT_DIR$(tput sgr0)"
mkdir $DATA_ROOT_DIR;

# set scripts executable
chmod +x 00_apps/install_apps.sh

## define applications to be analysed
# appname
# declare -a app_names=("vim" "tar")
declare -a app_names=("tar")
# debugsymbol package names (for apt-get)
# declare -a app_debug_sym=("vim-dbg" "tar-dbgsym")
declare -a app_debug_sym=("tar-dbgsym")
# application paths (incl. application name) 
#declare -a app_paths=("/usr/bin/openssl" "/usr/bin/vim") 
#declare -a app_paths=("/usr/bin/openssl") 
# /usr/bin/caja
# declare -a app_paths=("/usr/bin/vim.tiny") # - works
#declare -a app_paths=("/usr/bin/openssl") # no dbgsym! 
declare -a app_paths=("/bin/tar") # - works
# declare -a app_paths=("/usr/bin/vim.tiny" "/bin/tar") # - works

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
done
echo "$(tput setaf 3) done: extracting function lists $(tput sgr0)"
####	b. call gdb to disassebmle each function 
echo "$(tput setaf 3) start: disassembling functions $(tput sgr0)"
chmod +x ./disass_all.sh
./disass_all.sh
echo "$(tput setaf 3) end: disassembling functions $(tput sgr0)"

#### (  III. ) Parse disassemblies, write to DB, generate shell scripts calling openSSL
for app_name in "${app_names[@]}"
do
    root_folder_app="$DATA_ROOT_DIR/disassembly/$app_name"
    root_folder_disassembly="$root_folder_app/dis"
    mkdir "$root_folder_disassembly"
    root_folder_opcodes="$root_folder_app/opc"
	mkdir "$root_folder_opcodes"
	# /home/lvr/src/opcenc/opcenc/dataexch/disassembly/tar/exp
	java -jar 02_OpcodeEncryption/opcenc.jar pd "$app_name" "$root_folder_app" "$keyFilesRootDir"
	chmod +x "$root_folder_app/encrypt_opcodes.sh"
	"./$root_folder_app/encrypt_opcodes.sh"
done
