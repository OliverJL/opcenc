#!/bin/bash

# required files in folder of this script:
# - gdb.info_functions.cfg - commands to export binary file to be analyzed`s functions via gdb

if [ $# -eq 0 ]
then
  echo "$(tput setaf 1)No valid file: $1$(tput sgr0) - pass a valid file to be analyzed"
  exit;
fi

OUT_FOLDER=$DATA_ROOT_DIR/disassembly

if [ ! -d "$OUT_FOLDER" ];
then
#echo "$(tput setaf 5)erasing $OUT_FOLDER$(tput sgr0)"
#rm -R $DATA_ROOT_DIR
	echo "$(tput setaf 5)creating $OUT_FOLDER$(tput sgr0)"
	mkdir $OUT_FOLDER
fi

# binfile_namepath: entire path & filename to the binary file to be analyzed
binfile_namepath=$1
# binfile_name: the name of the binary file to be analyzed
export fspec=$binfile_namepath;
binfile_name=`basename $fspec`

echo "$(tput setaf 3)Register $OUT_FOLDER/disassemble_$binfile_name.sh$(tput sgr0)"
echo "chmod +x $OUT_FOLDER/disassemble_$binfile_name.sh" >> disass_all.sh
echo $OUT_FOLDER/disassemble_$binfile_name.sh >> disass_all.sh

##### 1. create list of functions for executable file to be analyzed #####

if [ -d "$OUT_FOLDER/$binfile_name" ]; 
then
 
  if [ ! -d "$OUT_FOLDER/bak" ];
  then
    echo "$(tput setaf 5)creating backup folder: bak$(tput sgr0)"
    mkdir $OUT_FOLDER/bak;
  fi

  bakfolder_name=$OUT_FOLDER/bak/$binfile_name"_"$(date +"%Y%m%d_%H%M%S")
  echo "$(tput setaf 5)backup: moving folder $binfile_name to $bakfolder_name$(tput sgr0)"
  mv $OUT_FOLDER/$binfile_name $bakfolder_name
fi
# creating clean out folders
echo "$(tput setaf 5)creating folder $OUT_FOLDER/$binfile_name$(tput sgr0)"
mkdir $OUT_FOLDER/$binfile_name;
echo "$(tput setaf 5)creating folder $OUT_FOLDER/$binfile_name/cfg$(tput sgr0)"
mkdir $OUT_FOLDER/$binfile_name/cfg;
echo "$(tput setaf 5)creating folder $OUT_FOLDER/$binfile_name/exp$(tput sgr0)"
mkdir $OUT_FOLDER/$binfile_name/exp;

# functions_namepath: list of potential functions of executable file to be analyzed
functions_namepath=$OUT_FOLDER/$binfile_name/function_names.$binfile_name.txt
echo "$(tput setaf 3)creating list of functions to: $functions_namepath$(tput sgr0)"
echo "gdb $binfile_namepath -x ./01_DisassembleBinaries/gdb.info_functions.cfg > $functions_namepath"
gdb $binfile_namepath -x ./01_DisassembleBinaries/gdb.info_functions.cfg > $functions_namepath

##### 2. export function op-code to file (one file per function) #####

#	The list of functions divides into 3 sections
#	...	[exportFunctionMode => 0]
#	All defined functions: [exportFunctionMode => 1]
#	functions with available dbg info/symbols <-- We want these (functions defined within the executable file to be analyzed)
#	Non-debugging symbols: [exportFunctionMode => 2]
#	functions without available dbg info/symbols <-- We can skip those (external libs)
exportFunctionMode=0;  
FILE=$functions_namepath;
disassemble_function_names="";

## gdb is reluctant if you request to many functions for disassembly, hence we limit the number of function and create several cfg files
#exportedFuncCntr: current index of function within a cfg file
declare -i exportedFuncCntr=0;
#exportFuncLimit:  max. number of functions/cfg file
declare -i exportFuncLimit=9;
#exportedFuncCntr: current index of cfg file
declare -i cfgFileCntr=0;

flush()
{
	# We have reached the max. number functions per cfg file or have reached the line "Non-debugging symbols:" 
	if [ "$exportedFuncCntr" == 0 ]
	then
		echo "$(tput setaf 1)No functions have been found! Please ensure $binfile_namepath is a Debugversion i.e. Debug symbols are existing! $(tput sgr0)"
		return;
	fi
	
	# dissFunc_namepath: name&path of the cfg file to instruct gdb to disassemble the function of the previous cycle 
	dissFunc_namepath="$OUT_FOLDER/$binfile_name/exp/func_disass."${cfgFileCntr}".dis"
	#echo "$(tput setaf 3)gdb $binfile_namepath -x $cfgFileName >> $dissFunc_namepath$(tput sgr0)";
	#echo "echo \"disass: gdb $binfile_namepath -x $cfgFileName >> $dissFunc_namepath\";" >> $OUT_FOLDER/disassemble_$binfile_name.sh
	#echo "gdb $binfile_namepath -x $cfgFileName >> $dissFunc_namepath;" >> $OUT_FOLDER/disassemble_$binfile_name.sh
	#echo "sleep 10" >> $OUT_FOLDER/disassemble_$binfile_name.sh
	echo -e $disassemble_function_names >> $cfgFileName
	echo quit >> $cfgFileName
	disassemble_function_names=""
	exportedFuncCntr=0
	cfgFileCntr+=1
	exportFunctionMode=1
}

while read line; 
do
	# extract function names from prototype/function definition, i.e. remove any return value & parameter info.
	# f.e.: "static const gchar *media_source_real_get_indexable_keywords(Indexable *);" will be reduced to "media_source_real_get_indexable_keywords" so gdb can resolve the name
	function_name=$line;
	function_name=${function_name#static* };
	function_name=${function_name%(*};
	function_name=${function_name##* };
	function_name=${function_name%%*);};
	function_name=${function_name%%*,};
	function_name=${function_name%%*.c:};
	function_name=${function_name%%*.cpp:};
	function_name=${function_name%%*.h:};
	function_name=${function_name#\*\**};
	function_name=${function_name#\**};
	function_name=${function_name#(\*)*}; 
	#echo $function_name;

	# We are interessted in the functions listed between the lines
	# All defined functions: <---	start
	# [functions with available dbg info/symbols] <-- We want these (functions defined within the executable file to be analyzed)
	# Non-debugging symbols: <--	end
	# [functions without available dbg info/symbols] <-- We can skip those (external libs)

	if [ "$line" == "Non-debugging symbols:" ]
	then
	echo "BBBYYYYYYYYYYYYYYYE"
		flush;
		exit;
	fi

	# If debug information is available & the line has not been erased (erased because the line is just a part of function definition of a previous line, broken by a line break)
	if [ $exportFunctionMode == 1 -a ! ${#function_name} == 0 ]
	then 
		echo "zZZZZzzZZZzzZZZ"
		# create a cfg file for gdb listing all function names to be disassembled 
		cfgFileName=$OUT_FOLDER/$binfile_name/"cfg/disassemble_"$binfile_name"_functions.${cfgFileCntr}.cfg"
		echo "$(tput setaf 3)gdb Writing to: $cfgFileName - disassembling: $function_name$(tput sgr0)"
		echo "define disassemble_$function_name" >> $cfgFileName
		echo "	echo \"disassemble function: $function_name\""
		echo "	disassemble /r $function_name" >> $cfgFileName
		echo "end" >> $cfgFileName;
		echo "" >> $cfgFileName;
		disassemble_function_names+="disassemble_${function_name}\n" # collect all the disassemble_$function_name commands to call those in a single command XXXXXXXXXXXXX
		exportedFuncCntr+=1;
	fi  

	if [ "$line" == "All defined functions:" ]
	then	
		echo "HHHHHHHHHHHHHHHAAAAAAAAAAAAAALO"
		exportFunctionMode=1; # start exporting
	fi  

	if [ $exportedFuncCntr == $exportFuncLimit ]
	then
		flush;
	fi
	
done < $FILE

#echo "$(tput bel)"
echo "$(tput sgr0)"
