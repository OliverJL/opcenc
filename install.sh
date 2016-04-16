#!/bin/bash

# set scripts executable
chmod +x ./00_apps/install_apps.sh
chmod +x ./01_DisassembleBinaries/disass_func.sh

# install all programms to be analysed
00_apps/install_apps.sh
